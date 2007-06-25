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

package org.jsefa.flr;

import org.jsefa.Configuration;
import org.jsefa.flr.lowlevel.FlrLowLevelConfiguration;
import org.jsefa.flr.lowlevel.FlrLowLevelIOFactory;
import org.jsefa.flr.lowlevel.FlrLowLevelIOFactoryImpl;

/**
 * A configuration object used when creating a {@link FlrIOFactory}. It uses
 * lazy initialization for the low level configuration.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrConfiguration extends Configuration {

    private FlrLowLevelConfiguration lowLevelConfig;

    private FlrLowLevelIOFactory lowLevelIOFactory;

    /**
     * Constructs a new <code>FlrConfiguration</code>.
     */
    public FlrConfiguration() {
        setLowLevelIOFactory(FlrLowLevelIOFactoryImpl.getInstance());
    }

    private FlrConfiguration(FlrConfiguration other) {
        super(other);
        setLowLevelConfiguration(other.getLowLevelConfiguration().createCopy());
        setLowLevelIOFactory(other.getLowLevelIOFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlrConfiguration createCopy() {
        return new FlrConfiguration(this);
    }

    /**
     * Returns the low level configuration object.
     * 
     * @return the low level configuration object
     */
    public FlrLowLevelConfiguration getLowLevelConfiguration() {
        if (this.lowLevelConfig == null) {
            this.lowLevelConfig = new FlrLowLevelConfiguration();
        }
        return this.lowLevelConfig;
    }

    /**
     * Returns the FLR low level IO factory.
     * 
     * @return the FLR low level IO factory
     */
    public FlrLowLevelIOFactory getLowLevelIOFactory() {
        return this.lowLevelIOFactory;
    }

    /**
     * Sets the FLR low level configuration object.
     * 
     * @param lowLevelConfig the FLR low level configuration object.
     */
    public void setLowLevelConfiguration(FlrLowLevelConfiguration lowLevelConfig) {
        this.lowLevelConfig = lowLevelConfig;
    }

    /**
     * Sets the FLR low level IO factory.
     * 
     * @param lowLevelIOFactory the FLR low level IO factory
     */
    public void setLowLevelIOFactory(FlrLowLevelIOFactory lowLevelIOFactory) {
        this.lowLevelIOFactory = lowLevelIOFactory;
    }

    /**
     * Returns the line break <code>String</code>.
     *
     * @return the line break <code>String</code>
     * @see FlrLowLevelConfiguration#getLineBreak
     */
    public String getLineBreak() {
        return getLowLevelConfiguration().getLineBreak();
    }

    /**
     * Sets the line break <code>String</code>.
     * 
     * @param lineBreak the line break <code>String</code>
     * @see FlrLowLevelConfiguration#setLineBreak
     */
    public void setLineBreak(String lineBreak) {
        getLowLevelConfiguration().setLineBreak(lineBreak);
    }

}
