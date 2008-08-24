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

package org.jsefa.common.lowlevel.config;

import static org.jsefa.common.lowlevel.config.LowLevelConfiguration.Defaults.DEFAULT_LINE_BREAK;
import static org.jsefa.common.lowlevel.config.LowLevelInitialConfigurationParameters.LINE_BREAK;

import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.lowlevel.LowLevelIOFactory;

/**
 * The abstract superclass for low level configuration object classes. It uses lazy initialization.
 * <p>
 * A configuration object is used when creating a new {@link LowLevelIOFactory}. One configuration object can be
 * used for the creation of multiple factories as each new factory holds its own copy of the configuration object.
 * So the configuration object can be changed after creating a factory with it without affecting the configuration
 * of the factory.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class LowLevelConfiguration {

    private String lineBreak;

    /**
     * Constructs a new <code>LowLevelConfiguration</code>.
     */
    protected LowLevelConfiguration() {

    }

    /**
     * Constructs a new <code>LowLevelConfiguration</code> as a copy of the given one.
     * 
     * @param other the other config
     */
    protected LowLevelConfiguration(LowLevelConfiguration other) {
        setLineBreak(other.getLineBreak());
    }

    /**
     * Returns the line break <code>String</code>.
     * 
     * @return the line break <code>String</code>
     */
    public String getLineBreak() {
        if (this.lineBreak == null) {
            this.lineBreak = InitialConfiguration.get(LINE_BREAK, DEFAULT_LINE_BREAK);
        }
        return this.lineBreak;
    }

    /**
     * Sets the line break <code>String</code>.
     * 
     * @param lineBreak the line break <code>String</code>
     */
    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

    /**
     * Creates a copy of this <code>LowLevelConfiguration</code>.
     * 
     * @return a copy of this <code>LowLevelConfiguration</code>
     */
    public abstract LowLevelConfiguration createCopy();

    /**
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {
        /**
         * The default line break used if none is explicitly given.
         */
        String DEFAULT_LINE_BREAK = System.getProperty("line.separator");
    }
}
