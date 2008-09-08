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
package org.jsefa.rbf.config;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.lowlevel.filter.LineFilter;
import org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * The abstract superclass for RBF type configuration classes.
 * 
 * @param <C> the type of the RbfLowLevelConfiguration
 * @author Norman Lahme-Huetig
 */
public abstract class RbfConfiguration<C extends RbfLowLevelConfiguration>
    extends Configuration<RbfTypeMappingRegistry, RbfEntryPoint> {
    
    private C lowLevelConfiguration;
    
    /**
     * Constructs a new <code>RbfConfiguration</code>.
     */
    public RbfConfiguration() {
    }

    /**
     * Constructs a new <code>RbfConfiguration</code> as a copy of the given one.
     * 
     * @param other the other config
     */
    @SuppressWarnings("unchecked")
    protected RbfConfiguration(RbfConfiguration<C> other) {
        super(other);
        setLowLevelConfiguration((C) other.getLowLevelConfiguration().createCopy());
    }
    
    /**
     * Returns the line filter used to filter lines from an input stream.
     * 
     * @return the line filter or null
     */
    public LineFilter getLineFilter() {
        return getLowLevelConfiguration().getLineFilter();
    }

    /**
     * Sets the line filter to use for filtering lines from an input stream.
     * 
     * @param lineFilter the line filter
     */
    public void setLineFilter(LineFilter lineFilter) {
        getLowLevelConfiguration().setLineFilter(lineFilter);
    }
    
    /**
     * @return the special record delimiter or null if none exists
     */
    public Character getSpecialRecordDelimiter() {
        return getLowLevelConfiguration().getSpecialRecordDelimiter();
    }
    
    /**
     * Sets a special record delimiter.
     * @param delimiter the delimiter
     */
    public void setSpecialRecordDelimiter(Character delimiter) {
        getLowLevelConfiguration().setSpecialRecordDelimiter(delimiter);
    }
    
    /**
     * Returns the maximum number of characters to read from a line for passing it to a {@link LineFilter}.
     * <p>
     * This limit is only used if a special record delimiter is set.
     * 
     * @return the maximum number of characters to read from a line for passing it to a <code>LineFilter</code>.
     */
    public Integer getLineFilterLimit() {
        return getLowLevelConfiguration().getLineFilterLimit();
    }

    /**
     * Sets the maximum number of characters to read from a line for passing it to a {@link LineFilter}.
     * <p>
     * This limit is only used if a special record delimiter is set.
     * 
     * @param lineFilterLimit the maximum number of characters to read
     */
    public void setLineFilterLimit(Integer lineFilterLimit) {
        getLowLevelConfiguration().setLineFilterLimit(lineFilterLimit);
    }    
    

    /**
     * Returns the low level configuration object.
     * 
     * @return the low level configuration object
     */
    public C getLowLevelConfiguration() {
        if (this.lowLevelConfiguration == null) {
            this.lowLevelConfiguration = createDefaultLowLevelConfiguration();
        }
        return this.lowLevelConfiguration;
    }
    
    /**
     * Sets the low level configuration object.
     * 
     * @param lowLevelConfig the low level configuration object.
     */
    public void setLowLevelConfiguration(C lowLevelConfig) {
        this.lowLevelConfiguration = lowLevelConfig;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected RbfTypeMappingRegistry createDefaultTypeMappingRegistry() {
        return new RbfTypeMappingRegistry();
    }

    /**
     * Creates the default low level configuration to be used if none is explicitly given.
     * 
     * @return the default low level configuration
     */
    protected abstract C createDefaultLowLevelConfiguration();

}
