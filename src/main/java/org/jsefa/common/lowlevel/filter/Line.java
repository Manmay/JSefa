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

package org.jsefa.common.lowlevel.filter;

/**
 * A line of a stream.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class Line {
    private String content;

    private int lineNumber;

    private boolean isLastLine;

    /**
     * Constructs a new <code>Line</code>.
     * @param content the content of the line
     * @param lineNumber the line number (starting with 1)
     * @param isLastLine true if it is the last line of the stream; false otherwise
     */
    public Line(String content, int lineNumber, boolean isLastLine) {
        this.content = content;
        this.lineNumber = lineNumber;
        this.isLastLine = isLastLine;
    }

    /**
     * Returns the content of the line.
     * @return the line content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Returns the number of the line (starting with 1).
     * @return the line number
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Returns true if this is the last line of the stream; false otherwise.
     * @return true if this is the last line of the stream; false otherwise
     */
    public boolean isLastLine() {
        return this.isLastLine;
    }
    
}
