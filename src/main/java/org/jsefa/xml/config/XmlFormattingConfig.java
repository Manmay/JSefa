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

/**
 * Configuration object used to determine some formatting aspects during xml
 * serialization.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author lahme
 * 
 */
public final class XmlFormattingConfig {
    private final String lineBreak;

    private final String lineIdenter;

    /**
     * Standard <code>XmlFormattingConfig</code> with a single line break and
     * two spaces as line identer.
     */
    public static final XmlFormattingConfig STANDARD = new XmlFormattingConfig("\n", "  ");

    /**
     * Constructs a new <code>XmlFormattingConfig</code>.
     * 
     * @param lineBreak the line break
     * @param lineIdenter the line identer
     */
    public XmlFormattingConfig(String lineBreak, String lineIdenter) {
        this.lineBreak = lineBreak;
        this.lineIdenter = lineIdenter;
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
     * Returns the line identer <code>String</code>.
     * 
     * @return the line identer <code>String</code>
     */
    public String getLineIdenter() {
        return this.lineIdenter;
    }

}
