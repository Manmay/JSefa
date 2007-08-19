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

package org.jsefa.flr.lowlevel.config;

import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.flr.lowlevel.FlrLowLevelDeserializer;
import org.jsefa.flr.lowlevel.FlrLowLevelSerializer;

import static org.jsefa.flr.lowlevel.config.FlrLowLevelConfiguration.Defaults.DEFAULT_LINE_BREAK;
import static org.jsefa.flr.lowlevel.config.FlrLowLevelInitialConfigurationParameters.LINE_BREAK;

/**
 * Configuration object for creating a {@link FlrLowLevelSerializer} or
 * {@link FlrLowLevelDeserializer}. It uses lazy initialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class FlrLowLevelConfiguration {

    private String lineBreak;

    /**
     * Constructs a new <code>FlrLowLevelConfiguration</code>.
     */
    public FlrLowLevelConfiguration() {
    }

    private FlrLowLevelConfiguration(FlrLowLevelConfiguration other) {
        setLineBreak(other.getLineBreak());
    }

    /**
     * {@inheritDoc}
     */
    public FlrLowLevelConfiguration createCopy() {
        return new FlrLowLevelConfiguration(this);
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

    interface Defaults {
        /**
         * The default line break used if none is explicitly given.
         */
        String DEFAULT_LINE_BREAK = "\n";
    }
}
