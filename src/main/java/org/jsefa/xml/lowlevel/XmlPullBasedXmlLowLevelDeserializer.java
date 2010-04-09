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

package org.jsefa.xml.lowlevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.jsefa.common.lowlevel.InputPosition;
import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.lowlevel.model.Attribute;
import org.jsefa.xml.lowlevel.model.AttributeImpl;
import org.jsefa.xml.lowlevel.model.ElementEndImpl;
import org.jsefa.xml.lowlevel.model.ElementStartImpl;
import org.jsefa.xml.lowlevel.model.TextContentImpl;
import org.jsefa.xml.lowlevel.model.XmlItem;
import org.jsefa.xml.lowlevel.model.XmlItemType;
import org.jsefa.xml.namespace.NamespaceConstants;
import org.jsefa.xml.namespace.QName;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * XmlPull V1 based implementation of {@link XmlLowLevelDeserializer}. Suitable for the Android platform.
 * 
 * @author Sebastian Hoß
 * @author Norman Lahme-Huetig
 */
public final class XmlPullBasedXmlLowLevelDeserializer implements XmlLowLevelDeserializer {
    private final XmlLowLevelConfiguration config;

    private Reader                         reader;

    private XmlPullParser                  pullParser;

    private int                            depth = -1;

    private XmlItemType                    currentItemType;

    private XmlItem                        currentItem;

    private boolean                        eventPrefetched;

    private boolean                        decreaseDepthOnNextEvent;

    private int                            prefetchedEvent = -1;

    /**
     * Constructs a new <code>StaxBasedXmlLowLevelDeserializer</code>.
     * 
     * @param config the configuration object
     */
    public XmlPullBasedXmlLowLevelDeserializer(final XmlLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public void open(final Reader reader) {
        this.open(reader, null);
    }

    /**
     * {@inheritDoc}
     */
    public void open(final Reader reader, final String systemId) {
        if (reader instanceof BufferedReader) {
            this.reader = reader;
        } else {
            this.reader = new BufferedReader(reader);
        }

        XmlPullParserFactory factory;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            this.pullParser = factory.newPullParser();
            this.pullParser.setInput(this.reader);
        } catch (final XmlPullParserException e) {
            throw new LowLevelDeserializationException("Error while opening the deserialization stream", e);
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
            if (this.eventPrefetched) {
                return true;
            } else {
                this.prefetchedEvent = this.pullParser.next();
                this.eventPrefetched = true;

                return this.prefetchedEvent != XmlPullParser.END_DOCUMENT;
            }
        } catch (final XmlPullParserException e) {
            throw new LowLevelDeserializationException(e);
        } catch (final IOException e) {
            throw new LowLevelDeserializationException(e);
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
        if (!this.eventPrefetched && !this.hasNext()) {
            this.currentItemType = XmlItemType.NONE;
            this.currentItem = null;
            return;
        }
        try {
            int event;
            if (this.eventPrefetched) {
                event = this.prefetchedEvent;
                this.eventPrefetched = false;
                this.prefetchedEvent = -1;
            } else {
                event = this.pullParser.next();
            }
            switch (event) {
                case XmlPullParser.START_TAG:
                    this.depth++;
                    this.currentItemType = XmlItemType.ELEMENT_START;
                    break;
                case XmlPullParser.END_TAG:
                    this.currentItemType = XmlItemType.ELEMENT_END;
                    this.decreaseDepthOnNextEvent = true;
                    break;
                case XmlPullParser.TEXT:
                    this.currentItemType = XmlItemType.TEXT_CONTENT;
                    break;
                default:
                    this.currentItemType = XmlItemType.UNKNOWN;
            }
            this.currentItem = null;
        } catch (final XmlPullParserException e) {
            throw new LowLevelDeserializationException(e);
        } catch (final IOException e) {
            throw new LowLevelDeserializationException(e);
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
                final QName name = this.getElementName();
                final QName dataTypeName = this.getDataTypeName();
                final Attribute[] attributes = this.getAttributes();
                this.currentItem = new ElementStartImpl(name, dataTypeName, attributes, this.depth);
            } else if (this.currentItemType == XmlItemType.ELEMENT_END) {
                final QName name = this.getElementName();
                this.currentItem = new ElementEndImpl(name, this.depth);
            } else if (this.currentItemType == XmlItemType.TEXT_CONTENT) {
                this.currentItem = new TextContentImpl(this.pullParser.getText());
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
    public void close(final boolean closeReader) {
        try {
            //            this.pullParser.close(); // There is no XmlPullParser.close() method
            if (closeReader) {
                this.reader.close();
            }
        } catch (final Exception e) {
            throw new LowLevelDeserializationException("Error while closing the deserialization stream", e);
        }
        this.reader = null;
    }

    /**
     * {@inheritDoc}
     */
    public InputPosition getInputPosition() {
        if (this.reader != null) {
            final int lineNumber = this.pullParser.getLineNumber();
            final int columnNumber = this.pullParser.getColumnNumber();

            if (lineNumber >= 0 && columnNumber >= 0) {
                return new InputPosition(lineNumber, columnNumber);
            }
        }

        return null;
    }

    private QName getElementName() {
        return QName.create(this.normalizeURI(this.pullParser.getNamespace()), this.pullParser.getName());
    }

    private QName getDataTypeName() {
        QName dataTypeName = null;
        final String dataTypeNameStr = this.pullParser.getAttributeValue(this.config.getDataTypeAttributeName()
                .getUri(), this.config.getDataTypeAttributeName().getLocalName());
        if (dataTypeNameStr != null) {
            final int delimiterPos = dataTypeNameStr.indexOf(":");
            if (delimiterPos == -1) {
                dataTypeName = QName.create(this.normalizeURI(this.pullParser.getNamespace("")), dataTypeNameStr);
            } else {
                final String prefix = dataTypeNameStr.substring(0, delimiterPos);
                final String uri = this.pullParser.getNamespace(prefix);
                String localName = "";
                if (delimiterPos < dataTypeNameStr.length() - 1) {
                    localName = dataTypeNameStr.substring(delimiterPos + 1);
                }
                dataTypeName = QName.create(this.normalizeURI(uri), localName);
            }
        }
        return dataTypeName;
    }

    private Attribute[] getAttributes() {
        final int attributeCount = this.pullParser.getAttributeCount();
        final Attribute[] attributes = new Attribute[attributeCount];
        for (int i = 0; i < attributeCount; i++) {
            final QName name = QName.create(this.normalizeURI(this.pullParser.getAttributeNamespace(i)),
                    this.pullParser.getAttributeName(i));
            final String value = this.pullParser.getAttributeValue(i);
            attributes[i] = new AttributeImpl(name, value);
        }
        return attributes;
    }

    private String normalizeURI(final String uri) {
        if (uri != null) {
            return uri;
        } else {
            return NamespaceConstants.NO_NAMESPACE_URI;
        }
    }

}
