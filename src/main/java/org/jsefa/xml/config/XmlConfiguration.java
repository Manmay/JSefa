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

package org.jsefa.xml.config;

import static org.jsefa.xml.config.XmlInitialConfigurationParameters.DATA_TYPE_DEFAULT_NAME_REGISTRY;
import static org.jsefa.xml.config.XmlConfiguration.Defaults.*;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.util.OnDemandObjectProvider;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.mapping.XmlEntryPoint;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;
import org.jsefa.xml.mapping.support.XmlDataTypeDefaultNameRegistry;
import org.jsefa.xml.mapping.support.XmlSchemaBuiltInDataTypeNames;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;

/**
 * A configuration object used when creating an XML IO factory. It uses lazy initialization.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlConfiguration extends Configuration<XmlTypeMappingRegistry, XmlEntryPoint> {

    private XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry;

    private XmlLowLevelConfiguration lowLevelConfiguration;

    /**
     * Constructs a new <code>XmlConfiguration</code>.
     */
    public XmlConfiguration() {
    }

    private XmlConfiguration(XmlConfiguration other) {
        super(other);
        setDataTypeDefaultNameRegistry(other.getDataTypeDefaultNameRegistry().createCopy());
        setLowLevelConfiguration(other.getLowLevelConfiguration().createCopy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlConfiguration createCopy() {
        return new XmlConfiguration(this);
    }

    /**
     * Returns the data type default name registry to be used.
     * 
     * @return the data type default name registry.
     */
    public XmlDataTypeDefaultNameRegistry getDataTypeDefaultNameRegistry() {
        if (this.dataTypeDefaultNameRegistry == null) {
            XmlDataTypeDefaultNameRegistry initialRegistry = InitialConfiguration.get(
                    DATA_TYPE_DEFAULT_NAME_REGISTRY, DEFAULT_DATA_TYPE_DEFAULT_NAME_REGISTRY_PROVIDER);
            this.dataTypeDefaultNameRegistry = initialRegistry.createCopy();
        }
        return this.dataTypeDefaultNameRegistry;
    }

    /**
     * Returns the low level configuration object.
     * 
     * @return the low level configuration object
     */
    public XmlLowLevelConfiguration getLowLevelConfiguration() {
        if (this.lowLevelConfiguration == null) {
            this.lowLevelConfiguration = new XmlLowLevelConfiguration();
        }
        return this.lowLevelConfiguration;
    }

    /**
     * Sets the xml low level configuration object.
     * 
     * @param lowLevelConfig the xml low level configuration object.
     */
    public void setLowLevelConfiguration(XmlLowLevelConfiguration lowLevelConfig) {
        this.lowLevelConfiguration = lowLevelConfig;
    }

    /**
     * Returns the <code>NamespaceManager</code> to be used for serialization only.
     * 
     * @return a the namespace manager
     * @see XmlLowLevelConfiguration#getNamespaceManager
     */
    public NamespaceManager getNamespaceManager() {
        return getLowLevelConfiguration().getNamespaceManager();
    }

    /**
     * Returns the name of the attribute denoting a data type.
     * 
     * @return the data type attribute name
     * @see XmlLowLevelConfiguration#getDataTypeAttributeName
     */
    public QName getDataTypeAttributeName() {
        return getLowLevelConfiguration().getDataTypeAttributeName();
    }

    /**
     * Returns the line break used for serializing.
     * 
     * @return the line break
     * @see XmlLowLevelConfiguration#getLineBreak
     */
    public String getLineBreak() {
        return getLowLevelConfiguration().getLineBreak();
    }

    /**
     * Returns the line indentation used for serializing.
     * 
     * @return the line indentation
     * @see XmlLowLevelConfiguration#getLineIndentation
     */
    public String getLineIndentation() {
        return getLowLevelConfiguration().getLineIndentation();
    }

    /**
     * Sets the data type default name registry.
     * 
     * @param dataTypeDefaultNameRegistry the data type default name registry
     */
    public void setDataTypeDefaultNameRegistry(XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry) {
        this.dataTypeDefaultNameRegistry = dataTypeDefaultNameRegistry;
    }

    /**
     * Sets the namespace manager.
     * 
     * @param namespaceManager the namespace manager.
     * @see XmlLowLevelConfiguration#setNamespaceManager
     */
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        getLowLevelConfiguration().setNamespaceManager(namespaceManager);
    }

    /**
     * Sets the name of the attribute that denotes the data type of the respective element.
     * 
     * @param dataTypeAttributeName the data type attribute name
     * @see XmlLowLevelConfiguration#setDataTypeAttributeName
     */
    public void setDataTypeAttributeName(QName dataTypeAttributeName) {
        getLowLevelConfiguration().setDataTypeAttributeName(dataTypeAttributeName);
    }

    /**
     * Sets the line indentation to be used for serializing.
     * 
     * @param lineIndentation the line indentation
     * @see XmlLowLevelConfiguration#setLineIndentation
     */
    public void setLineIndentation(String lineIndentation) {
        getLowLevelConfiguration().setLineIndentation(lineIndentation);
    }

    /**
     * Sets the line break to be used for serializing.
     * 
     * @param lineBreak the line break
     * @see XmlLowLevelConfiguration#setLineBreak
     */
    public void setLineBreak(String lineBreak) {
        getLowLevelConfiguration().setLineBreak(lineBreak);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected XmlTypeMappingRegistry createDefaultTypeMappingRegistry() {
        return new XmlTypeMappingRegistry();
    }

    /**
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {
        /**
         * The default data type default name registry provider.
         */
        OnDemandObjectProvider DEFAULT_DATA_TYPE_DEFAULT_NAME_REGISTRY_PROVIDER = new OnDemandObjectProvider() {
            @SuppressWarnings("unchecked")
            public XmlDataTypeDefaultNameRegistry get() {
                XmlDataTypeDefaultNameRegistry registry = new XmlDataTypeDefaultNameRegistry();
                registry.register(String.class, XmlSchemaBuiltInDataTypeNames.STRING_DATA_TYPE_NAME);
                registry.register(int.class, XmlSchemaBuiltInDataTypeNames.INT_DATA_TYPE_NAME);
                registry.register(Integer.class, XmlSchemaBuiltInDataTypeNames.INTEGER_DATA_TYPE_NAME);
                registry.register(long.class, XmlSchemaBuiltInDataTypeNames.LONG_DATA_TYPE_NAME);
                registry.register(Long.class, XmlSchemaBuiltInDataTypeNames.LONG_DATA_TYPE_NAME);
                registry.register(boolean.class, XmlSchemaBuiltInDataTypeNames.BOOLEAN_DATA_TYPE_NAME);
                registry.register(Boolean.class, XmlSchemaBuiltInDataTypeNames.BOOLEAN_DATA_TYPE_NAME);
                return registry;
            }
        };
    }

}
