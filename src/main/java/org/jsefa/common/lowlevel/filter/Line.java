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
 * A non-empty line from a stream.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class Line {
    private String content;

    private int lineNumber;
    
    private boolean truncated;

    private boolean lastLine;

    /**
     * Constructs a new non-empty <code>Line</code>.
     * @param content the content of the line
     * @param lineNumber the line number (starting with 1)
     * @param truncated true if the line is incomplete, i. e. truncated; false otherwise
     * @param lastLine true if it is the last line of the stream; false otherwise
     */
    public Line(String content, int lineNumber, boolean truncated, boolean lastLine) {
        this.content = content;
        this.lineNumber = lineNumber;
        this.truncated = truncated;
        this.lastLine = lastLine;
    }

    /**
     * @return the content of the line
     */
    public String getContent() {
        return this.content;
    }

    /**
     * @return the line number (starting with 1)
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * @return true, if the line is incomplete, i. e. truncated; false otherwise.
     */
    public boolean isTruncated() {
        return this.truncated;
    }
    
    /**
     * @return true if this is the last non-empty line of the stream; false otherwise
     */
    public boolean isLastLine() {
        return this.lastLine;
    }
    
}
