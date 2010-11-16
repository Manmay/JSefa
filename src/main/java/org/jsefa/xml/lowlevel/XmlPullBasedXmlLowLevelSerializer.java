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

import java.io.IOException;
import java.io.Writer;

import org.jsefa.common.lowlevel.LowLevelSerializationException;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

/**
 * XmlPull V1 based implementation of {@link XmlLowLevelSerializer}. Suitable for the Android platform.
 * 
 * @author Sebastian Hoß
 * @author Norman Lahme-Huetig
 */
public final class XmlPullBasedXmlLowLevelSerializer implements XmlLowLevelSerializer {
    private final XmlLowLevelConfiguration config;

    private Writer                         writer;

    private XmlSerializer                  serializer;

    private int                            depth = -1;

    private boolean                        lastWasStartElement;

    private NamespaceManager               namespaceManager;

    /**
     * Constructs a new <code>StaxBasedXmlLowLevelSerializer</code>.
     * 
     * @param config the configuration object
     */
    public XmlPullBasedXmlLowLevelSerializer(final XmlLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public void open(final Writer writer) {
        this.writer = writer;
        XmlPullParserFactory factory;

        try {
            factory = XmlPullParserFactory.newInstance();
            this.serializer = factory.newSerializer();
            this.serializer.setOutput(this.writer);
        } catch (final XmlPullParserException e) {
            throw new LowLevelSerializationException("Error while opening the serialization stream", e); //$NON-NLS-1$
        } catch (final IllegalArgumentException e) {
            throw new LowLevelSerializationException("Error while opening the serialization stream", e); //$NON-NLS-1$
        } catch (final IllegalStateException e) {
            throw new LowLevelSerializationException("Error while opening the serialization stream", e); //$NON-NLS-1$
        } catch (final IOException e) {
            throw new LowLevelSerializationException("Error while opening the serialization stream", e); //$NON-NLS-1$
        }

        this.depth = -1;
        this.namespaceManager = this.config.getNamespaceManager();
    }

    /**
     * {@inheritDoc}
     */
    public void writeXmlDeclaration(final String version, final String encoding) {
        try {
            this.serializer.startDocument(encoding, true);
            this.writeLineBreak();
        } catch (final IllegalArgumentException e) {
            throw new LowLevelSerializationException(e);
        } catch (final IllegalStateException e) {
            throw new LowLevelSerializationException(e);
        } catch (final IOException e) {
            throw new LowLevelSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeDocTypeDeclaration(final QName rootElementName, final String publicId, final String systemId) {
        final StringBuilder dtd = new StringBuilder("<!DOCTYPE ");
        if (!this.hasNamespace(rootElementName)) {
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
            this.serializer.docdecl(dtd.toString());
            this.writeLineBreak();
        } catch (final IllegalArgumentException e) {
            throw new LowLevelSerializationException(e);
        } catch (final IllegalStateException e) {
            throw new LowLevelSerializationException(e);
        } catch (final IOException e) {
            throw new LowLevelSerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeStartElement(final QName name) {
        this.writeStartElement(name, null);
    }

    /**
     * {@inheritDoc}
     */
    public void writeStartElement(final QName name, final QName dataTypeName) {
        this.depth++;
        this.namespaceManager = NamespaceManager.createWithParent(this.namespaceManager);
        try {
            if (this.lastWasStartElement) {
                this.writeLineBreak();
            } else {
                this.lastWasStartElement = true;
            }
            this.writeIdent();
            if (!this.hasNamespace(name)) {

                if (this.defaultNamespaceExists()) {
                    this.serializer.setPrefix(DEFAULT_NAMESPACE_PREFIX, NO_NAMESPACE_URI);
                    this.serializer.startTag(NO_NAMESPACE_URI, name.getLocalName());
                    this.namespaceManager.registerPrefix(DEFAULT_NAMESPACE_PREFIX, NO_NAMESPACE_URI);
                }

                this.serializer.startTag(null, name.getLocalName());

            } else {
                String prefix = this.namespaceManager.getPrefix(name.getUri(), true);
                if (prefix == null) {
                    prefix = this.namespaceManager.createPrefix(name.getUri(), true);
                    this.namespaceManager.registerPrefix(prefix, name.getUri());
                }

                this.serializer.setPrefix(prefix, name.getUri());
                this.serializer.startTag(name.getUri(), name.getLocalName());
            }
            if (dataTypeName != null) {
                String value = dataTypeName.getLocalName();
                if (this.hasNamespace(dataTypeName)) {
                    String prefix = this.namespaceManager.getPrefix(dataTypeName.getUri(), true);
                    if (prefix == null) {
                        prefix = this.namespaceManager.createPrefix(dataTypeName.getUri(), true);
                        this.namespaceManager.registerPrefix(prefix, dataTypeName.getUri());
                        this.serializer.setPrefix(prefix, dataTypeName.getUri());
                    }
                    if (prefix.length() > 0) {
                        value = prefix + ":" + value;
                    }
                }
                this.writeAttribute(this.config.getDataTypeAttributeName(), value);
            }
        } catch (final IllegalArgumentException e) {
            throw new LowLevelSerializationException("Unable to write element " + name, e);
        } catch (final IllegalStateException e) {
            throw new LowLevelSerializationException("Unable to write element " + name, e);
        } catch (final IOException e) {
            throw new LowLevelSerializationException("Unable to write element " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeAttribute(final QName name, final String value) {
        if (value == null) {
            return;
        }
        try {
            if (!this.hasNamespace(name)) {
                this.serializer.attribute(null, name.getLocalName(), value);
            } else {
                String prefix = this.namespaceManager.getPrefix(name.getUri(), false);
                if (prefix == null) {
                    prefix = this.namespaceManager.createPrefix(name.getUri(), false);
                    this.namespaceManager.registerPrefix(prefix, name.getUri());
                    this.serializer.setPrefix(prefix, name.getUri());
                }
                this.serializer.attribute(name.getUri(), name.getLocalName(), value);
            }
        } catch (final IllegalArgumentException e) {
            throw new LowLevelSerializationException("Unable to write attribute " + name, e);
        } catch (final IllegalStateException e) {
            throw new LowLevelSerializationException("Unable to write attribute " + name, e);
        } catch (final IOException e) {
            throw new LowLevelSerializationException("Unable to write attribute " + name, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void writeText(final String text, final TextMode textMode) {
        if (text != null && text.length() != 0) {
            try {
                if (TextMode.CDATA.equals(textMode)) {
                    int index = text.indexOf("]]>");
                    int lastIndex = 0;
                    while (index > -1) {
                        this.serializer.cdsect(text.substring(lastIndex, index + 2));
                        lastIndex = index + 2;
                        index = text.indexOf("]]>", index + 1);
                    }
                    this.serializer.cdsect(text.substring(lastIndex));
                } else {
                    this.serializer.text(text);
                }
            } catch (final IllegalArgumentException e) {
                throw new LowLevelSerializationException("Unable to write text", e);
            } catch (final IllegalStateException e) {
                throw new LowLevelSerializationException("Unable to write text", e);
            } catch (final IOException e) {
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
                this.writeIdent();
            }
            this.lastWasStartElement = false;
            this.serializer.endTag(this.serializer.getNamespace(), this.serializer.getName());
            this.writeLineBreak();
        } catch (final IllegalArgumentException e) {
            throw new LowLevelSerializationException("Unable to finish element", e);
        } catch (final IllegalStateException e) {
            throw new LowLevelSerializationException("Unable to finish element", e);
        } catch (final IOException e) {
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
            this.serializer.flush();
        } catch (final Exception e) {
            throw new LowLevelSerializationException("Error while flushing the serialization stream", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close(final boolean closeWriter) {
        try {
            this.serializer.flush();
            if (closeWriter) {
                this.writer.close();
            }
        } catch (final Exception e) {
            throw new LowLevelSerializationException("Error while closing the serialization stream", e);
        }
    }

    private void writeLineBreak() throws IOException {
        this.serializer.text(this.config.getLineBreak().toCharArray(), 0, this.config.getLineBreak().length());
    }

    private void writeIdent() throws IOException {
        final String lineIndentation = this.config.getLineIndentation();
        if (lineIndentation.length() > 0) {
            for (int i = 0; i < this.depth; i++) {
                this.serializer.text(lineIndentation.toCharArray(), 0, lineIndentation.length());
            }
        }
    }

    private boolean hasNamespace(final QName name) {
        return !NO_NAMESPACE_URI.equals(name.getUri());
    }

    private boolean defaultNamespaceExists() {
        final String registeredUri = this.namespaceManager.getUri(DEFAULT_NAMESPACE_PREFIX);
        return registeredUri != null && !registeredUri.equals(NO_NAMESPACE_URI);
    }

}
