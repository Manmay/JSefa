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

package org.jsefa.rbf.lowlevel;

import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.jsefa.common.lowlevel.InputPosition;
import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.common.lowlevel.filter.Line;
import org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration;

/**
 * Abstract implementation of {@link RbfLowLevelDeserializer}.
 * 
 * @param <C> the type of the RbfLowLevelConfiguration
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class RbfLowLevelDeserializerImpl<C extends RbfLowLevelConfiguration>
    implements RbfLowLevelDeserializer {

    private LineReader reader;
    
    private String currentLine;
    
    private int currentColumnIndex;
    
    private C config;
    
    /**
     * Constructs a new <code>RbfLowLevelDeserializerImpl</code>.
     * 
     * @param config the configuration object
     */
    public RbfLowLevelDeserializerImpl(C config) {
        this.config = config;
    }
    
    /**
     * {@inheritDoc}
     */
    public final void open(Reader reader) {
        if (this.config.getLineFilter() != null) {
            this.reader = new FilteringLineReader(reader, this.config.getLineFilter());
        } else {
            this.reader = new LineReader(reader);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean readNextRecord() {
        this.currentLine = this.reader.readLine();
        if (this.currentLine == null) {
            return false;
        } else {
            this.currentColumnIndex = 0;
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void unreadRecord() {
        this.currentColumnIndex = 0;
        this.reader.unreadRecord();
    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeReader) {
        if (closeReader) {
            this.reader.close();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final InputPosition getInputPosition() {
        if (this.currentLine != null) {
            return new InputPosition(this.reader.getLineNumber(), this.currentColumnIndex + 1);
        } else {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Line> getStoredLines() {
        try {
            return ((FilteringLineReader) this.reader).getStoredLines();
        } catch (ClassCastException e) {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Returns the configuration object.
     * @return the configuration object
     */
    protected final C getConfiguration() {
        return this.config;
    }

    /**
     * Returns true, if there is another character on the current line to read.
     * 
     * @return true, if there is another character on the current line to read; false otherwise.
     */
    protected final boolean hasNextChar() {
        return this.currentColumnIndex < this.currentLine.length();
    }

    /**
     * Returns the current character of the current line.
     * 
     * @return a character
     */
    protected final char peekChar() {
        try {
            return this.currentLine.charAt(this.currentColumnIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new LowLevelDeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next character of the current line and moves one character forward.
     * 
     * @return a character
     */
    protected final char nextChar() {
        try {
            return this.currentLine.charAt(this.currentColumnIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new LowLevelDeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next <code>String</code> with the given length of the current line and moves forward
     * accordingly.
     * 
     * @param length the length of the <code>String</code> to return
     * @return a <code>String</code>
     */
    protected final String nextString(int length) {
        try {
            String value = this.currentLine.substring(this.currentColumnIndex, this.currentColumnIndex
                    + length);
            this.currentColumnIndex += length;
            return value;
        } catch (IndexOutOfBoundsException e) {
            if (this.currentColumnIndex < this.currentLine.length()) {
                String value = this.currentLine.substring(this.currentColumnIndex);
                this.currentColumnIndex += value.length();
                return value;
            }
            return null;
        }
    }

    /**
     * Returns the number of remaining characters in the current line.
     * 
     * @return the number of remaining characters in the current line.
     */
    protected final int remainingLineLength() {
        return this.currentLine.length() - this.currentColumnIndex;
    }
    
}
