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

import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;

/**
 * Configuration object for creating a {@link XmlLowLevelSerializer} or
 * {@link XmlLowLevelDeserializer}. It uses lazy initialization for the
 * namespace manager.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlLowLevelConfiguration {
    private NamespaceManager namespaceManager;

    private QName dataTypeAttributeName;

    private String lineBreak;

    private String lineIndentation;

    /**
     * Constructs a new <code>XmlLowLevelConfig</code>.
     */
    public XmlLowLevelConfiguration() {
        setDataTypeAttributeName(XmlLowLevelConstants.XML_SCHEMA_DATA_TYPE_ATTRIBUTE_NAME);
        setLineBreak(XmlLowLevelConstants.DEFAULT_LINE_BREAK);
        setLineIndentation(XmlLowLevelConstants.DEFAULT_LINE_INDENTATION);
    }

    private XmlLowLevelConfiguration(XmlLowLevelConfiguration other) {
        getNamespaceManager().registerAll(other.getNamespaceManager());
        setDataTypeAttributeName(other.getDataTypeAttributeName());
        setLineBreak(other.getLineBreak());
        setLineIndentation(other.getLineIndentation());
    }

    /**
     * Creates a copy of this <code>XmlLowLevelConfig</code>.
     * 
     * @return a copy of this <code>XmlLowLevelConfig</code>
     */
    public XmlLowLevelConfiguration createCopy() {
        return new XmlLowLevelConfiguration(this);
    }

    /**
     * Returns the <code>NamespaceManager</code> to be used for serialization
     * only.
     * 
     * @return a the namespace manager
     */
    public NamespaceManager getNamespaceManager() {
        if (this.namespaceManager == null) {
            this.namespaceManager = NamespaceManager.create();
        }
        return this.namespaceManager;
    }

    /**
     * Returns the name of the attribute denoting a data type.
     * 
     * @return the data type attribute name
     */
    public QName getDataTypeAttributeName() {
        return this.dataTypeAttributeName;
    }

    /**
     * Returns the line break used for serializing.
     * 
     * @return the line break
     */
    public String getLineBreak() {
        return this.lineBreak;
    }

    /**
     * Returns the line indentation used for serializing.
     * 
     * @return the line indentation
     */
    public String getLineIndentation() {
        return this.lineIndentation;
    }

    /**
     * Sets the namespace manager.
     * 
     * @param namespaceManager the namespace manager.
     */
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    /**
     * Sets the name of the attribute that denotes the data type of the
     * respective element.
     * 
     * @param dataTypeAttributeName the data type attribute name
     */
    public void setDataTypeAttributeName(QName dataTypeAttributeName) {
        this.dataTypeAttributeName = dataTypeAttributeName;
    }

    /**
     * Sets the line indentation to be used for serializing.
     * 
     * @param lineIndentation the line indentation
     */
    public void setLineIndentation(String lineIndentation) {
        this.lineIndentation = lineIndentation;
    }

    /**
     * Sets the line break to be used for serializing.
     * 
     * @param lineBreak the line break
     */
    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

}