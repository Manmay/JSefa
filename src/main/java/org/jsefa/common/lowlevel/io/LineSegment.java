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

package org.jsefa.common.lowlevel.io;

/**
 * A segment of a line of a stream.<p>
 * A <code>LineSegement</code> consists of a content, a line and column number within the stream (regarding
 * the start of the content) and a terminator.<p>
 * Note: The line number and the column number starts both with 1.
 * 
 * @author Norman Lahme-Huetig
 */
public final class LineSegment {
    private String content;
    private int lineNumber;
    private int columnNumber;
    private Terminator terminator;

    /**
     * Different types of content terminators.
     * 
     * @author Norman Lahme-Huetig
     */
    public enum Terminator {
        
        /**
         * Indicates that the line segment was terminated by a line break. 
         */
        LINE_BREAK,
        
        /**
         * Indicates that the line segment was terminated by a special termination character.
         */
        SPECIAL_CHARACTER,
        
        /**
         * Indicates that the line segment was terminated by the end of the stream.
         */
        EOS,
        
        /**
         * Indicates that the line segment is not terminated at all - thus it is incomplete (truncated).
         */
        NONE
    }

    /**
     * Constructs a new <code>LineSegment</code>.
     * @param content the content
     * @param lineNumber the line number
     * @param columnNumber the column number
     * @param terminator the terminator
     */
    public LineSegment(String content, int lineNumber, int columnNumber, Terminator terminator) {
        super();
        this.content = content;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.terminator = terminator;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the line number (starting with 1)
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @return the column number (starting with 1)
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return the terminator
     */
    public Terminator getTerminator() {
        return terminator;
    }

}
