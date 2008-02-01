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

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jsefa.common.lowlevel.LowLevelSerializationException;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;

import static org.jsefa.xml.namespace.NamespaceConstants.*;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;

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
            throw new LowLevelSerializationException("Unable to create stax writer", e);
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
            throw new LowLevelSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeStartElement(QName name) {
        writeStartElement(name, null);
    }

    /**
     * {@inheritDoc}
     */
    public void writeStartElement(QName name, QName dataTypeName) {
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
                if (defaultNamespaceExists()) {
                    this.namespaceManager.registerPrefix(DEFAULT_NAMESPACE_PREFIX, NO_NAMESPACE_URI);
                    this.streamWriter.writeNamespace(DEFAULT_NAMESPACE_PREFIX, NO_NAMESPACE_URI);
                }
            } else {
                boolean createNamespace = (this.namespaceManager.getPrefix(name.getUri(), true) == null);
                this.streamWriter.writeStartElement(this.namespaceManager.getOrCreatePrefix(name.getUri(), true),
                        name.getLocalName(), name.getUri());
                if (createNamespace) {
                    this.streamWriter.writeNamespace(this.namespaceManager.getOrCreatePrefix(name.getUri(), true),
                            name.getUri());
                }
            }
            if (dataTypeName != null) {
                if (hasNamespace(dataTypeName)
                        && this.namespaceManager.getPrefix(dataTypeName.getUri(), true) == null) {
                    this.streamWriter.writeNamespace(this.namespaceManager.getOrCreatePrefix(
                            dataTypeName.getUri(), true), dataTypeName.getUri());
                }
                String value = dataTypeName.getLocalName();
                if (hasNamespace(dataTypeName)) {
                    String prefix = this.namespaceManager.getOrCreatePrefix(dataTypeName.getUri(), true);
                    if (prefix.length() > 0) {
                        value = prefix + ":" + value;
                    }
                }
                writeAttribute(this.config.getDataTypeAttributeName(), value);
            }
        } catch (XMLStreamException e) {
            throw new LowLevelSerializationException("Unable to write element " + name, e);
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
                if (this.namespaceManager.getPrefix(name.getUri(), false) == null) {
                    this.streamWriter.writeNamespace(
                            this.namespaceManager.getOrCreatePrefix(name.getUri(), false), name.getUri());
                }
                this.streamWriter.writeAttribute(this.namespaceManager.getOrCreatePrefix(name.getUri(), false),
                        name.getUri(), name.getLocalName(), value);
            }
        } catch (XMLStreamException e) {
            throw new LowLevelSerializationException("Unable to write attribute " + name, e);
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
                throw new LowLevelSerializationException("Unable to write text", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeEndElement() {
        try {
            if (!this.lastWasStartElement) {
                writeIdent();
            }
            this.lastWasStartElement = false;
            this.streamWriter.writeEndElement();
            writeLineBreak();
        } catch (XMLStreamException e) {
            throw new LowLevelSerializationException("Unable to finish element", e);
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
            throw new LowLevelSerializationException("Unable to close writer", e);
        } catch (IOException e) {
            throw new LowLevelSerializationException("Unable to close writer", e);
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
        return !NO_NAMESPACE_URI.equals(name.getUri());
    }

    private boolean defaultNamespaceExists() {
        String registeredUri = this.namespaceManager.getUri(DEFAULT_NAMESPACE_PREFIX);
        return registeredUri != null && !registeredUri.equals(NO_NAMESPACE_URI);
    }

}
