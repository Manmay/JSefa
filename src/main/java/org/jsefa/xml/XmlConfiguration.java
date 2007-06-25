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

package org.jsefa.xml;

import org.jsefa.Configuration;
import org.jsefa.xml.lowlevel.XmlLowLevelConfiguration;
import org.jsefa.xml.lowlevel.XmlLowLevelIOFactory;
import org.jsefa.xml.lowlevel.stax.StaxBasedXmlLowLevelIOFactory;

/**
 * A configuration object used when creating a {@link XmlIOFactory}. It uses
 * lazy initialization for the data type default name registry and for the low
 * level configuration.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlConfiguration extends Configuration {

    private XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry;

    private XmlLowLevelConfiguration lowLevelConfig;

    private XmlLowLevelIOFactory lowLevelIOFactory;

    /**
     * Constructs a new <code>XmlConfiguration</code>.
     */
    public XmlConfiguration() {
        setLowLevelIOFactory(StaxBasedXmlLowLevelIOFactory.getInstance());
    }

    private XmlConfiguration(XmlConfiguration other) {
        super(other);
        this.dataTypeDefaultNameRegistry = new XmlDataTypeDefaultNameRegistry();
        getDataTypeDefaultNameRegistry().registerAll(other.getDataTypeDefaultNameRegistry());
        setLowLevelConfiguration(other.getLowLevelConfiguration().createCopy());
        setLowLevelIOFactory(other.getLowLevelIOFactory());
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
            this.dataTypeDefaultNameRegistry = new XmlDataTypeDefaultNameRegistry();
        }
        return this.dataTypeDefaultNameRegistry;
    }

    /**
     * Returns the low level configuration object.
     * 
     * @return the low level configuration object
     */
    public XmlLowLevelConfiguration getLowLevelConfiguration() {
        if (this.lowLevelConfig == null) {
            this.lowLevelConfig = new XmlLowLevelConfiguration();
        }
        return this.lowLevelConfig;
    }

    /**
     * Sets the xml low level configuration object.
     * 
     * @param lowLevelConfig the xml low level configuration object.
     */
    public void setLowLevelConfiguration(XmlLowLevelConfiguration lowLevelConfig) {
        this.lowLevelConfig = lowLevelConfig;
    }

    /**
     * Returns the xml low level IO factory.
     * 
     * @return the xml low level IO factory
     */
    public XmlLowLevelIOFactory getLowLevelIOFactory() {
        return this.lowLevelIOFactory;
    }

    /**
     * Sets the xml low level IO factory.
     * 
     * @param lowLevelIOFactory the xml low level IO factory
     */
    public void setLowLevelIOFactory(XmlLowLevelIOFactory lowLevelIOFactory) {
        this.lowLevelIOFactory = lowLevelIOFactory;
    }

    /**
     * Returns the <code>NamespaceManager</code> to be used for serialization
     * only.
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
     * Sets the namespace manager.
     * 
     * @param namespaceManager the namespace manager.
     * @see XmlLowLevelConfiguration#setNamespaceManager
     */
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        getLowLevelConfiguration().setNamespaceManager(namespaceManager);
    }

    /**
     * Sets the name of the attribute that denotes the data type of the
     * respective element.
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

}
