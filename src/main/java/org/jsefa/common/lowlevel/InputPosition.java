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

package org.jsefa.common.lowlevel;

import java.io.Serializable;

/**
 * Encapsulates information about the current position within the input stream.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class InputPosition implements Serializable {
    private static final long serialVersionUID = 1L;
    private int lineNumber;
    private int columnNumber;
    
    /**
     * Constructs a new <code>InputPosition</code>.
     * @param lineNumber the line number
     * @param columnNumber the column number
     */
    public InputPosition(int lineNumber, int columnNumber) {
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    /**
     * Returns the number of the current line within the input stream (beginning with 1).
     * 
     * @return the current line number.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Returns the number of the current column within the current line within the input stream (beginning with 1).
     * 
     * @return the current column number.
     */
    public int getColumnNumber() {
        return this.columnNumber;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(Integer.toString(getLineNumber()));
        builder.append(',');
        builder.append(Integer.toString(getColumnNumber()));
        builder.append(']');
        return builder.toString();
    }
    
}
