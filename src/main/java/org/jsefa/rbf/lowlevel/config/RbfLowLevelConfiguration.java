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

package org.jsefa.rbf.lowlevel.config;

import static org.jsefa.rbf.lowlevel.config.RbfLowLevelInitialConfigurationParameters.LINE_FILTER_LIMIT;
import static org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration.Defaults.DEFAULT_LINE_FILTER_LIMIT;

import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.lowlevel.config.LowLevelConfiguration;
import org.jsefa.common.lowlevel.filter.LineFilter;

/**
 * The abstract superclass for RBF type low level configuration classes.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class RbfLowLevelConfiguration extends LowLevelConfiguration {

    private LineFilter lineFilter;

    private Character specialRecordDelimiter;

    private Integer lineFilterLimit;

    /**
     * Constructs a new <code>RbfLowLevelConfiguration</code>.
     */
    public RbfLowLevelConfiguration() {
    }

    /**
     * Constructs a new <code>RbfLowLevelConfiguration</code> as a copy of the given one.
     * 
     * @param other the other config
     */
    protected RbfLowLevelConfiguration(RbfLowLevelConfiguration other) {
        super(other);
        setLineFilter(other.getLineFilter());
        setLineFilterLimit(other.getLineFilterLimit());
        setSpecialRecordDelimiter(other.getSpecialRecordDelimiter());
    }

    /**
     * Returns the line filter used to filter lines from an input stream.
     * 
     * @return the line filter or null
     */
    public LineFilter getLineFilter() {
        return this.lineFilter;
    }

    /**
     * Sets the line filter to use for filtering lines from an input stream.
     * 
     * @param lineFilter the line filter
     */
    public void setLineFilter(LineFilter lineFilter) {
        this.lineFilter = lineFilter;
    }

    /**
     * @return the special record delimiter or null if none exists
     */
    public Character getSpecialRecordDelimiter() {
        return this.specialRecordDelimiter;
    }

    /**
     * Sets a special record delimiter.
     * 
     * @param delimiter the delimiter
     */
    public void setSpecialRecordDelimiter(Character delimiter) {
        this.specialRecordDelimiter = delimiter;
    }

    /**
     * Returns the maximum number of characters to read from a line for passing it to a {@link LineFilter}.
     * <p>
     * This limit is only used if a special record delimiter is set.
     * 
     * @return the maximum number of characters to read from a line for passing it to a <code>LineFilter</code>.
     */
    public Integer getLineFilterLimit() {
        if (this.lineFilterLimit == null) {
            this.lineFilterLimit = InitialConfiguration.get(LINE_FILTER_LIMIT, DEFAULT_LINE_FILTER_LIMIT);
        }    
        return this.lineFilterLimit;
    }

    /**
     * Sets the maximum number of characters to read from a line for passing it to a {@link LineFilter}.
     * <p>
     * This limit is only used if a special record delimiter is set.
     * 
     * @param lineFilterLimit the maximum number of characters to read
     */
    public void setLineFilterLimit(Integer lineFilterLimit) {
        this.lineFilterLimit = lineFilterLimit;
    }

    /**
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {

        /**
         * The default maximum number of characters to read from a line for passing it to a {@link LineFilter} if a
         * special record delimiter is set.
         */
        Integer DEFAULT_LINE_FILTER_LIMIT = 256;
    }

}
