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
import org.jsefa.xml.lowlevel.XmlLowLevelDriver;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfig;
import org.jsefa.xml.lowlevel.stax.StaxDriver;

/**
 * A configuration object used when creating a {@link XmlIOFactory}.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlConfiguration extends Configuration {

    private XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry;

    private XmlLowLevelConfig lowLevelConfig;

    private XmlLowLevelDriver lowLevelDriver;

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
        getLowLevelConfig().setDataTypeAttributeName(other.getLowLevelConfig().getDataTypeAttributeName());
        getLowLevelConfig().setFormattingConfig(other.lowLevelConfig.getFormattingConfig());
        getLowLevelConfig().getNamespaceManager().registerAll(other.lowLevelConfig.getNamespaceManager());
        setLowLevelDriver(other.getLowLevelDriver());
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
    public XmlLowLevelConfig getLowLevelConfig() {
        if (this.lowLevelConfig == null) {
            this.lowLevelConfig = new XmlLowLevelConfig();
        }
        return this.lowLevelConfig;
    }

    /**
     * Sets the xml low level configuration object.
     * 
     * @param lowLevelConfig the xml low level configuration object.
     */
    public void setLowLevelConfig(XmlLowLevelConfig lowLevelConfig) {
        this.lowLevelConfig = lowLevelConfig;
    }

    /**
     * Returns the xml low level driver.
     * 
     * @return the xml low level driver
     */
    public XmlLowLevelDriver getLowLevelDriver() {
        if (this.lowLevelDriver == null) {
            this.lowLevelDriver = StaxDriver.getInstance();
        }
        return this.lowLevelDriver;
    }

    /**
     * Sets the xml low level driver.
     * 
     * @param lowLevelDriver the xml low level driver
     */
    public void setLowLevelDriver(XmlLowLevelDriver lowLevelDriver) {
        this.lowLevelDriver = lowLevelDriver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlConfiguration createCopy() {
        return new XmlConfiguration(this);
    }

}
