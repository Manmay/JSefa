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

package org.jsefa.xml.lowlevel.config;

import org.jsefa.xml.NamespaceManager;
import org.jsefa.xml.QName;
import org.jsefa.xml.XmlConstants;
import org.jsefa.xml.config.XmlFormattingConfig;
import org.jsefa.xml.lowlevel.XmlLowLevelDeserializer;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;

/**
 * Configuration object for creating a {@link XmlLowLevelSerializer} or
 * {@link XmlLowLevelDeserializer}. It uses lazy initialization with standard
 * values for not explicity given settings.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlLowLevelConfig {
    private NamespaceManager namespaceManager;

    private XmlFormattingConfig formattingConfig;

    private QName dataTypeAttributeName;

    /**
     * Returns the <code>XmlFormattingConfig</code> to be used for
     * serialization only.
     * 
     * @return a <code>XmlFormattingConfig</code>
     */
    public XmlFormattingConfig getFormattingConfig() {
        if (this.formattingConfig == null) {
            this.formattingConfig = XmlFormattingConfig.STANDARD;
        }
        return this.formattingConfig;
    }

    /**
     * Returns the name of the attribute denoting a data type.
     * 
     * @return the data type attribute name
     */
    public QName getDataTypeAttributeName() {
        if (this.dataTypeAttributeName == null) {
            this.dataTypeAttributeName = XmlConstants.XML_SCHEMA_DATA_TYPE_ATTRIBUTE_NAME;
        }
        return this.dataTypeAttributeName;
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
     * Sets the namespace manager.
     * 
     * @param namespaceManager the namespace manager.
     */
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    /**
     * Sets the formatting configuration.
     * 
     * @param formattingConfig the formatting configuration
     */
    public void setFormattingConfig(XmlFormattingConfig formattingConfig) {
        this.formattingConfig = formattingConfig;
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

}
