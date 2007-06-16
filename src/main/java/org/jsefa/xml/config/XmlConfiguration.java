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

import org.jsefa.Configuration;
import org.jsefa.xml.XmlDataTypeDefaultNameRegistry;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.lowlevel.XmlLowLevelIOFactory;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.lowlevel.stax.StaxBasedXmlLowLevelIOFactory;

/**
 * A configuration object used when creating a {@link XmlIOFactory}.
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
        this.dataTypeDefaultNameRegistry = new XmlDataTypeDefaultNameRegistry();
    }

    private XmlConfiguration(XmlConfiguration other) {
        super(other);
        this.dataTypeDefaultNameRegistry = new XmlDataTypeDefaultNameRegistry();
        getDataTypeDefaultNameRegistry().registerAll(other.getDataTypeDefaultNameRegistry());
        setLowLevelConfiguration(other.getLowLevelConfiguration().createCopy());
        setLowLevelIOFactory(other.getLowLevelIOFactory());
    }

    /**
     * Returns the data type default name registry to be used.
     * 
     * @return the data type default name registry.
     */
    public XmlDataTypeDefaultNameRegistry getDataTypeDefaultNameRegistry() {
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
        if (this.lowLevelIOFactory == null) {
            this.lowLevelIOFactory = StaxBasedXmlLowLevelIOFactory.getInstance();
        }
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
     * {@inheritDoc}
     */
    @Override
    public XmlConfiguration createCopy() {
        return new XmlConfiguration(this);
    }

}
