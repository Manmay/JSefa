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

import static org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration.Defaults.DEFAULT_DATA_TYPE_ATTRIBUTE_NAME;
import static org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration.Defaults.DEFAULT_LINE_INDENTATION;
import static org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration.Defaults.DEFAULT_NAMESPACE_MANAGER_PROVIDER;
import static org.jsefa.xml.lowlevel.config.XmlLowLevelInitialConfigurationParameters.DATA_TYPE_ATTRIBUTE_NAME;
import static org.jsefa.xml.lowlevel.config.XmlLowLevelInitialConfigurationParameters.LINE_INDENTATION;
import static org.jsefa.xml.lowlevel.config.XmlLowLevelInitialConfigurationParameters.NAMESPACE_MANAGER;

import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.lowlevel.config.LowLevelConfiguration;
import org.jsefa.common.util.OnDemandObjectProvider;
import org.jsefa.xml.lowlevel.XmlLowLevelDeserializer;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;
import org.jsefa.xml.namespace.NamespaceConstants;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;

/**
 * Configuration object for creating a {@link XmlLowLevelSerializer} or {@link XmlLowLevelDeserializer}. It uses
 * lazy initialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlLowLevelConfiguration extends LowLevelConfiguration {
    private NamespaceManager namespaceManager;

    private QName dataTypeAttributeName;

    private String lineIndentation;

    /**
     * Constructs a new <code>XmlLowLevelConfig</code>.
     */
    public XmlLowLevelConfiguration() {
    }

    private XmlLowLevelConfiguration(XmlLowLevelConfiguration other) {
        super(other);
        setNamespaceManager(other.getNamespaceManager().createCopy());
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
     * Returns the <code>NamespaceManager</code> to be used for serialization only.
     * 
     * @return a the namespace manager
     */
    public NamespaceManager getNamespaceManager() {
        if (this.namespaceManager == null) {
            NamespaceManager manager = InitialConfiguration.get(NAMESPACE_MANAGER,
                    DEFAULT_NAMESPACE_MANAGER_PROVIDER);
            this.namespaceManager = manager.createCopy();
        }
        return this.namespaceManager;
    }

    /**
     * Returns the name of the attribute denoting a data type.
     * 
     * @return the data type attribute name
     */
    public QName getDataTypeAttributeName() {
        if (this.dataTypeAttributeName == null) {
            this.dataTypeAttributeName = InitialConfiguration.get(DATA_TYPE_ATTRIBUTE_NAME,
                    DEFAULT_DATA_TYPE_ATTRIBUTE_NAME);
        }
        return this.dataTypeAttributeName;
    }

    /**
     * Returns the line indentation used for serializing.
     * 
     * @return the line indentation
     */
    public String getLineIndentation() {
        if (this.lineIndentation == null) {
            this.lineIndentation = InitialConfiguration.get(LINE_INDENTATION, DEFAULT_LINE_INDENTATION);
        }
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
     * Sets the name of the attribute that denotes the data type of the respective element.
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
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {
        /**
         * The name of the attribute used to denote the data type as used in xml schema instances.
         */
        QName DEFAULT_DATA_TYPE_ATTRIBUTE_NAME = QName.create(NamespaceConstants.XML_SCHEMA_INSTANCE_URI, "type");

        /**
         * The default line indentation used if none is explicitly given.
         */
        String DEFAULT_LINE_INDENTATION = "  ";

        /**
         * The default namespace manager provider.
         */
        OnDemandObjectProvider DEFAULT_NAMESPACE_MANAGER_PROVIDER = new OnDemandObjectProvider() {
            @SuppressWarnings("unchecked")
            public NamespaceManager get() {
                NamespaceManager newNamespaceManager = NamespaceManager.create();
                newNamespaceManager.registerPreferredPrefix("xsi", NamespaceConstants.XML_SCHEMA_INSTANCE_URI);
                return newNamespaceManager;
            }
        };
    }

}
