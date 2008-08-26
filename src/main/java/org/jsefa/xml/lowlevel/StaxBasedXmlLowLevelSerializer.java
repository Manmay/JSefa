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

import static org.jsefa.xml.namespace.NamespaceConstants.DEFAULT_NAMESPACE_PREFIX;
import static org.jsefa.xml.namespace.NamespaceConstants.NO_NAMESPACE_URI;

import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.jsefa.common.lowlevel.LowLevelSerializationException;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;

/**
 * Stax based implementation of {@link XmlLowLevelSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * @author Matthias Derer
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
        if (factory.isPropertySupported("com.ctc.wstx.outputEscapeCr")) {
            factory.setProperty("com.ctc.wstx.outputEscapeCr", Boolean.FALSE);
        }
        try {
            this.streamWriter = factory.createXMLStreamWriter(writer);
        } catch (XMLStreamException e) {
            throw new LowLevelSerializationException("Error while opening the serialization stream", e);
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
    public void writeDocTypeDeclaration(QName rootElementName, String publicId, String systemId) {
        StringBuilder dtd = new StringBuilder("<!DOCTYPE ");
        if (!hasNamespace(rootElementName)) {
            dtd.append(rootElementName.getLocalName());
        } else {
            this.namespaceManager = NamespaceManager.createWithParent(this.namespaceManager);
            String prefix = this.namespaceManager.getPrefix(rootElementName.getUri(), true);
            if (prefix == null) {
                prefix = this.namespaceManager.createPrefix(rootElementName.getUri(), true);
                // do not register the prefix. Otherwise no xmlns attribute will be written when writing the root
                // element start tag.
            }
            if (prefix.length() > 0) {
                dtd.append(prefix).append(":");
            }
            dtd.append(rootElementName.getLocalName());
        }
        if (publicId != null) {
            dtd.append(" PUBLIC \"").append(publicId).append("\" \"").append(systemId).append("\">");
        } else {
            dtd.append(" SYSTEM \"").append(systemId).append("\">");
        }
        try {
            this.streamWriter.writeDTD(dtd.toString());
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
                String prefix = this.namespaceManager.getPrefix(name.getUri(), true);
                boolean createNamespace = (prefix == null);
                if (prefix == null) {
                    prefix = this.namespaceManager.createPrefix(name.getUri(), true);
                    this.namespaceManager.registerPrefix(prefix, name.getUri());
                }
                this.streamWriter.writeStartElement(prefix, name.getLocalName(), name.getUri());
                if (createNamespace) {
                    this.streamWriter.writeNamespace(prefix, name.getUri());
                }
            }
            if (dataTypeName != null) {
                String value = dataTypeName.getLocalName();
                if (hasNamespace(dataTypeName)) {
                    String prefix = this.namespaceManager.getPrefix(dataTypeName.getUri(), true);
                    if (prefix == null) {
                        prefix = this.namespaceManager.createPrefix(dataTypeName.getUri(), true);
                        this.namespaceManager.registerPrefix(prefix, dataTypeName.getUri());
                        this.streamWriter.writeNamespace(prefix, dataTypeName.getUri());
                    }
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
                String prefix = this.namespaceManager.getPrefix(name.getUri(), false);
                if (prefix == null) {
                    prefix = this.namespaceManager.createPrefix(name.getUri(), false);
                    this.namespaceManager.registerPrefix(prefix, name.getUri());
                    this.streamWriter.writeNamespace(prefix, name.getUri());
                }
                this.streamWriter.writeAttribute(prefix, name.getUri(), name.getLocalName(), value);
            }
        } catch (XMLStreamException e) {
            throw new LowLevelSerializationException("Unable to write attribute " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeText(String text, TextMode textMode) {
        if (text != null && text.length() != 0) {
            try {
                if (TextMode.CDATA.equals(textMode)) {
                    int index = text.indexOf("]]>");
                    int lastIndex = 0;
                    while (index > -1) {
                        this.streamWriter.writeCData(text.substring(lastIndex, index + 2));
                        lastIndex = index + 2;
                        index = text.indexOf("]]>", index + 1);
                    }
                    this.streamWriter.writeCData(text.substring(lastIndex));
                } else {
                    this.streamWriter.writeCharacters(text);
                }
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
    public void flush() {
        try {
            this.streamWriter.flush();
        } catch (Exception e) {
            throw new LowLevelSerializationException("Error while flushing the serialization stream", e);
        }
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
        } catch (Exception e) {
            throw new LowLevelSerializationException("Error while closing the serialization stream", e);
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
