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

package org.jsefa.flr.config;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.util.GeneralConstants;
import org.jsefa.flr.lowlevel.config.FlrLowLevelConfiguration;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

import static org.jsefa.flr.config.FlrInitialConfigurationParameters.DEFAUT_PAD_CHARACTER;

/**
 * A configuration object used when creating an FLR IO factory. It uses lazy
 * initialization for the low level configuration.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrConfiguration extends Configuration<RbfTypeMappingRegistry, RbfEntryPoint> {

    private FlrLowLevelConfiguration lowLevelConfiguration;

    private char defaultPadCharacter = GeneralConstants.NO_CHARACTER;

    /**
     * Constructs a new <code>FlrConfiguration</code>.
     */
    public FlrConfiguration() {
    }

    private FlrConfiguration(FlrConfiguration other) {
        super(other);
        setDefaultPadCharacter(other.getDefaultPadCharacter());
        setLowLevelConfiguration(other.getLowLevelConfiguration().createCopy());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlrConfiguration createCopy() {
        return new FlrConfiguration(this);
    }

    /**
     * Returns the default pad character.
     * 
     * @return a character
     */
    public char getDefaultPadCharacter() {
        if (this.defaultPadCharacter == GeneralConstants.NO_CHARACTER) {
            this.defaultPadCharacter = (Character) InitialConfiguration.get(DEFAUT_PAD_CHARACTER,
                    Defaults.DEFAULT_PAD_CHARACTER);
        }        
        return this.defaultPadCharacter;
    }

    /**
     * Sets the default pad character.
     * 
     * @param defaultPadCharacter the default pad character
     */
    public void setDefaultPadCharacter(char defaultPadCharacter) {
        if (defaultPadCharacter == GeneralConstants.NO_CHARACTER) {
            this.defaultPadCharacter = (Character) InitialConfiguration.get(DEFAUT_PAD_CHARACTER,
                    Defaults.DEFAULT_PAD_CHARACTER);
        } else {
            this.defaultPadCharacter = defaultPadCharacter;
        }
    }

    /**
     * Returns the low level configuration object.
     * 
     * @return the low level configuration object
     */
    public FlrLowLevelConfiguration getLowLevelConfiguration() {
        if (this.lowLevelConfiguration == null) {
            this.lowLevelConfiguration = new FlrLowLevelConfiguration();
        }
        return this.lowLevelConfiguration;
    }

    /**
     * Sets the FLR low level configuration object.
     * 
     * @param lowLevelConfig the FLR low level configuration object.
     */
    public void setLowLevelConfiguration(FlrLowLevelConfiguration lowLevelConfig) {
        this.lowLevelConfiguration = lowLevelConfig;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected RbfTypeMappingRegistry createDefaultTypeMappingRegistry() {
        return new RbfTypeMappingRegistry();
    }

    interface Defaults {
        /**
         * The default pad character.
         */
        char DEFAULT_PAD_CHARACTER = ' ';
    }    
}
