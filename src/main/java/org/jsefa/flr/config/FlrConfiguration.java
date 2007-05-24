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

import org.jsefa.Configuration;
import org.jsefa.flr.FlrConstants;
import org.jsefa.flr.FlrIOFactory;

/**
 * A configuration object used when creating a {@link FlrIOFactory}.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrConfiguration extends Configuration {

    private String lineBreak;

    /**
     * Constructs a new <code>FlrConfiguration</code>.
     */
    public FlrConfiguration() {
        this.lineBreak = FlrConstants.DEFAULT_LINE_BREAK;
    }

    private FlrConfiguration(FlrConfiguration other) {
        super(other);
        this.lineBreak = other.lineBreak;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public FlrConfiguration createCopy() {
        return new FlrConfiguration(this);
    }

    /**
     * Returns the line break <code>String</code>.
     * 
     * @return the line break <code>String</code>
     */
    public String getLineBreak() {
        return this.lineBreak;
    }
    
    /**
     * Sets the line break <code>String</code>.
     * @param lineBreak the line break <code>String</code>
     */
    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }    

}
