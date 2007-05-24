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

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jsefa.SerializationException;
import org.jsefa.xml.NamespaceManager;
import org.jsefa.xml.QName;
import org.jsefa.xml.XmlConstants;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;

/**
 * Stax based implementation of {@link XmlLowLevelSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class StaxBasedXmlLowLevelSerializer implements XmlLowLevelSerializer {
    private final XmlLowLevelConfiguration config;

    private Writer writer;

    private XMLStreamWriter streamWriter;

    private int depth = -1;

    private boolean lastWasStartElement;

    private NamespaceManager namespaceManager;

    /**
     * Constructs a new <code>StaxBasedXmlLowLevelSerializer</code>.
     * 
     * @param config the configuration object
     */
    public StaxBasedXmlLowLevelSerializer(XmlLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public void open(Writer writer) {
        this.writer = writer;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            this.streamWriter = factory.createXMLStreamWriter(writer);
        } catch (XMLStreamException e) {
            throw new SerializationException("Unable to create stax writer", e);
        }
        this.depth = -1;
        this.namespaceManager = this.config.getNamespaceManager();
    }

    /**
     * {@inheritDoc}
     */
    public void writeXmlDeclaration(String version, String encoding) {
        try {
            this.streamWriter.writeStartDocument(encoding, version);
            writeLineBreak();
        } catch (XMLStreamException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(QName name) {
        startElement(name, null);
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(QName name, QName dataTypeName) {
        this.depth++;
        this.namespaceManager = NamespaceManager.createWithParent(this.namespaceManager);
        try {
            if (this.lastWasStartElement) {
                writeLineBreak();
            } else {
                this.lastWasStartElement = true;
            }
            writeIdent();
            if (!hasNamespace(name)) {
                this.streamWriter.writeStartElement(name.getLocalName());
            } else {
                boolean createNamespace = (this.namespaceManager.getPrefix(name.getUri()) == null);
                this.streamWriter.writeStartElement(this.namespaceManager.createPrefix(name.getUri(), true), name
                        .getLocalName(), name.getUri());
                if (createNamespace) {
                    this.streamWriter.writeNamespace(this.namespaceManager.createPrefix(name.getUri(), true), name
                            .getUri());
                }
            }
            if (dataTypeName != null) {
                if (hasNamespace(dataTypeName) && this.namespaceManager.getPrefix(dataTypeName.getUri()) == null) {
                    this.streamWriter.writeNamespace(this.namespaceManager.createPrefix(dataTypeName.getUri(),
                            false), dataTypeName.getUri());
                }
                String value = dataTypeName.getLocalName();
                if (hasNamespace(dataTypeName)) {
                    String prefix = this.namespaceManager.createPrefix(dataTypeName.getUri(), false);
                    if (prefix.length() > 0) {
                        value = prefix + ":" + value;
                    }
                }
                writeAttribute(this.config.getDataTypeAttributeName(), value);
            }
        } catch (XMLStreamException e) {
            throw new SerializationException("Unable to write element " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeAttribute(QName name, String value) {
        if (value == null) {
            return;
        }
        try {
            if (!hasNamespace(name)) {
                this.streamWriter.writeAttribute(name.getLocalName(), value);
            } else {
                if (this.namespaceManager.getPrefix(name.getUri()) == null) {
                    this.streamWriter.writeNamespace(this.namespaceManager.createPrefix(name.getUri(), false),
                            name.getUri());
                }
                this.streamWriter.writeAttribute(this.namespaceManager.createPrefix(name.getUri(), false), name
                        .getUri(), name.getLocalName(), value);
            }
        } catch (XMLStreamException e) {
            throw new SerializationException("Unable to write attribute " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeText(String text) {
        if (text != null && text.length() != 0) {
            try {
                this.streamWriter.writeCharacters(text);
            } catch (XMLStreamException e) {
                throw new SerializationException("Unable to finish element", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void finishElement() {
        try {
            if (!this.lastWasStartElement) {
                writeIdent();
            }
            this.lastWasStartElement = false;
            this.streamWriter.writeEndElement();
            writeLineBreak();
        } catch (XMLStreamException e) {
            throw new SerializationException("Unable to finish element", e);
        }
        this.namespaceManager = this.namespaceManager.getParent();
        this.depth--;
    }

    /**
     * {@inheritDoc}
     */
    public void close(boolean closeWriter) {
        try {
            this.streamWriter.close();
            if (closeWriter) {
                this.writer.close();
            }
        } catch (XMLStreamException e) {
            throw new SerializationException("Unable to close writer", e);
        } catch (IOException e) {
            throw new SerializationException("Unable to close writer", e);
        }
    }

    private void writeLineBreak() throws XMLStreamException {
        this.streamWriter.writeCharacters(this.config.getLineBreak());
    }

    private void writeIdent() throws XMLStreamException {
        String lineIndentation = this.config.getLineIndentation();
        if (lineIndentation.length() > 0) {
            for (int i = 0; i < this.depth; i++) {
                this.streamWriter.writeCharacters(lineIndentation);
            }
        }
    }

    private boolean hasNamespace(QName name) {
        return !XmlConstants.NO_NAMESPACE_URI.equals(name.getUri());
    }

}
