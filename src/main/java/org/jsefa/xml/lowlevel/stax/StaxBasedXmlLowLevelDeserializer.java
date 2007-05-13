/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsefa.xml.lowlevel.stax;

import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jsefa.DeserializationException;
import org.jsefa.xml.QName;
import org.jsefa.xml.XmlConstants;
import org.jsefa.xml.lowlevel.Attribute;
import org.jsefa.xml.lowlevel.XmlItem;
import org.jsefa.xml.lowlevel.XmlItemType;
import org.jsefa.xml.lowlevel.XmlLowLevelDeserializer;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfig;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Stax based implementation of {@link XmlLowLevelDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class StaxBasedXmlLowLevelDeserializer implements XmlLowLevelDeserializer {
    private final XmlLowLevelConfig config;

    private Reader reader;

    private XMLStreamReader streamReader;

    private int depth = -1;

    private XmlItemType currentItemType;

    private XmlItem currentItem;

    private boolean eventPrefetched;

    private boolean decreaseDepthOnNextEvent;

    /**
     * Constructs a new <code>StaxBasedXmlLowLevelDeserializer</code>.
     * 
     * @param config the configuration object
     */
    public StaxBasedXmlLowLevelDeserializer(XmlLowLevelConfig config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public void open(Reader reader) {
        this.reader = reader;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            this.streamReader = factory.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new DeserializationException("Error while opening the stream", e);
        }
        this.depth = -1;
        this.currentItem = null;
        this.currentItemType = XmlItemType.NONE;
        this.eventPrefetched = false;
        this.decreaseDepthOnNextEvent = false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        try {
            return this.eventPrefetched || this.streamReader.hasNext();
        } catch (XMLStreamException e) {
            throw new DeserializationException("Error while deserialization: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void moveToNext() {
        if (this.decreaseDepthOnNextEvent) {
            this.depth--;
            this.decreaseDepthOnNextEvent = false;
        }
        if (!this.eventPrefetched && !hasNext()) {
            this.currentItemType = XmlItemType.NONE;
            this.currentItem = null;
            return;
        }
        try {
            int event;
            if (this.eventPrefetched) {
                event = this.streamReader.getEventType();
                this.eventPrefetched = false;
            } else {
                event = this.streamReader.next();
            }
            switch (event) {
            case START_ELEMENT:
                this.depth++;
                this.currentItemType = XmlItemType.ELEMENT_START;
                break;
            case END_ELEMENT:
                this.currentItemType = XmlItemType.ELEMENT_END;
                this.decreaseDepthOnNextEvent = true;
                break;
            case CDATA:
                this.currentItemType = XmlItemType.TEXT_CONTENT;
                break;
            case CHARACTERS:
                this.currentItemType = XmlItemType.TEXT_CONTENT;
                break;
            default:
                this.currentItemType = XmlItemType.UNKNOWN;
            }
            this.currentItem = null;
        } catch (XMLStreamException e) {
            throw new DeserializationException("Error while deserialization: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public XmlItemType currentType() {
        return this.currentItemType;
    }

    /**
     * {@inheritDoc}
     */
    public XmlItem current() {
        if (this.currentItem == null) {
            if (this.currentItemType == XmlItemType.ELEMENT_START) {
                QName name = getElementName();
                QName dataTypeName = getDataTypeName();
                Attribute[] attributes = getAttributes();
                this.currentItem = new ElementStartImpl(name, dataTypeName, attributes, this.depth);
            } else if (this.currentItemType == XmlItemType.ELEMENT_END) {
                QName name = getElementName();
                this.currentItem = new ElementEndImpl(name, this.depth);
            } else if (this.currentItemType == XmlItemType.TEXT_CONTENT) {
                try {
                    if (!this.streamReader.hasNext()) {
                        this.currentItem = new TextContentImpl(this.streamReader.getText());
                    } else {
                        String text = this.streamReader.getText();
                        int event = this.streamReader.next();
                        this.eventPrefetched = true;
                        if ((event != CDATA) && (event != CHARACTERS)) {
                            this.currentItem = new TextContentImpl(text);
                        } else {
                            StringBuilder buffer = new StringBuilder();
                            buffer.append(text);
                            while (this.streamReader.hasNext()) {
                                event = this.streamReader.next();
                                if ((event == CDATA) || (event == CHARACTERS)) {
                                    buffer.append(this.streamReader.getText());
                                } else {
                                    break;
                                }

                            }
                            this.currentItem = new TextContentImpl(buffer.toString());
                        }
                    }
                } catch (XMLStreamException e) {
                    throw new DeserializationException("Error while deserialization: ", e);
                }

            }

        }
        return this.currentItem;
    }

    /**
     * {@inheritDoc}
     */
    public int currentDepth() {
        return this.depth;
    }

    /**
     * {@inheritDoc}
     */
    public void close(boolean closeReader) {
        try {
            this.streamReader.close();
            if (closeReader) {
                this.reader.close();
            }
        } catch (Exception e) {
            throw new DeserializationException("Error while closing", e);
        }
    }

    private QName getElementName() {
        return QName.create(normalizeURI(this.streamReader.getNamespaceURI()), this.streamReader.getLocalName());
    }

    private QName getDataTypeName() {
        QName dataTypeName = null;
        String dataTypeNameStr = this.streamReader.getAttributeValue(this.config.getDataTypeAttributeName()
                .getUri(), this.config.getDataTypeAttributeName().getLocalName());
        if (dataTypeNameStr != null) {
            int delimiterPos = dataTypeNameStr.indexOf(":");
            if (delimiterPos == -1) {
                dataTypeName = QName.create(normalizeURI(this.streamReader.getNamespaceURI("")), dataTypeNameStr);
            } else {
                String prefix = dataTypeNameStr.substring(0, delimiterPos);
                String uri = this.streamReader.getNamespaceURI(prefix);
                String localName = "";
                if (delimiterPos < dataTypeNameStr.length() - 1) {
                    localName = dataTypeNameStr.substring(delimiterPos + 1);
                }
                dataTypeName = QName.create(normalizeURI(uri), localName);
            }
        }
        return dataTypeName;
    }

    private Attribute[] getAttributes() {
        int attributeCount = this.streamReader.getAttributeCount();
        Attribute[] attributes = new Attribute[attributeCount];
        for (int i = 0; i < attributeCount; i++) {
            QName name = QName.create(normalizeURI(this.streamReader.getAttributeNamespace(i)), this.streamReader
                    .getAttributeLocalName(i));
            String value = this.streamReader.getAttributeValue(i);
            attributes[i] = new AttributeImpl(name, value);
        }
        return attributes;
    }

    private String normalizeURI(String uri) {
        if (uri != null) {
            return uri;
        } else {
            return XmlConstants.NO_NAMESPACE_URI;
        }
    }

}
