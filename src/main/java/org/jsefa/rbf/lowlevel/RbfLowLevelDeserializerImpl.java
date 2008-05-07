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

import static org.jsefa.common.lowlevel.filter.FilterResult.FAILED_BUT_STORE;
import static org.jsefa.common.lowlevel.filter.FilterResult.PASSED;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.jsefa.common.lowlevel.InputPosition;
import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.common.lowlevel.filter.FilterResult;
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

    private BufferedReader reader;
    
    private boolean linePrefetched;

    private InputLine currentLine;
    
    private InputLine nextLine;
    
    private int currentColumnIndex;
    
    private List<Line> storedLines;
    
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
        this.currentLine = new InputLine();
        this.nextLine = new InputLine();
        this.linePrefetched = false;
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        this.storedLines = new ArrayList<Line>();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean readNextRecord() {
        if (this.currentLine == null) {
            return false;
        }

        this.currentColumnIndex = 0;
        if (this.linePrefetched) {
            this.linePrefetched = false;
            return true;
        }
        
        if (this.config.getLineFilter() == null) {
            return moveForward(false);
        }

        while (moveForward(true)) {
            FilterResult filterResult = filterCurrentLine();
            if (filterResult == PASSED) {
                return true;
            } else if (filterResult == FAILED_BUT_STORE) {
                storeCurrentLine();
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public final void unreadRecord() {
        this.currentColumnIndex = 0;
        this.linePrefetched = true;
    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeReader) {
        if (closeReader) {
            try {
                this.reader.close();
            } catch (IOException e) {
                throw new LowLevelDeserializationException("Error while closing the deserialization stream", e);
            }
        }
        this.reader = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public final InputPosition getInputPosition() {
        if (this.reader != null && this.currentLine != null) {
            return new InputPosition(this.currentLine.lineNumber, this.currentColumnIndex + 1);
        } else {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Line> getStoredLines() {
        return this.storedLines;
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
        return this.currentColumnIndex < this.currentLine.content.length();
    }

    /**
     * Returns the current character of the current line.
     * 
     * @return a character
     */
    protected final char peekChar() {
        try {
            return this.currentLine.content.charAt(this.currentColumnIndex);
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
            return this.currentLine.content.charAt(this.currentColumnIndex++);
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
            String value = this.currentLine.content.substring(this.currentColumnIndex, this.currentColumnIndex
                    + length);
            this.currentColumnIndex += length;
            return value;
        } catch (IndexOutOfBoundsException e) {
            if (this.currentColumnIndex < this.currentLine.content.length()) {
                throw new LowLevelDeserializationException("Unexpected end of line reached");
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
        return this.currentLine.content.length() - this.currentColumnIndex;
    }
    
    private boolean moveForward(boolean readAhead) {
        if (!readAhead) {
            this.currentLine = readNextNonEmptyLine(this.currentLine);
        } else {
            if (this.nextLine == null) {
                this.currentLine = null;
                return false;
            }
    
            if (this.nextLine.after(this.currentLine)) {
                this.currentLine.copyFrom(this.nextLine);
            } else {
                this.currentLine = readNextNonEmptyLine(this.currentLine);
            }
    
            if (this.currentLine != null) {
                this.nextLine.copyFrom(this.currentLine);
                this.nextLine = readNextNonEmptyLine(this.nextLine);
            } else {
                this.nextLine = null;
            }
        }
        return this.currentLine != null;
    }
    
    private FilterResult filterCurrentLine() {
        return this.config.getLineFilter().filter(this.currentLine.content, this.currentLine.lineNumber,
                this.nextLine == null);
    }
    
    private void storeCurrentLine() {
        this.storedLines.add(new Line(this.currentLine.content, this.currentLine.lineNumber, this.nextLine == null));
    }
    
    private InputLine readNextNonEmptyLine(InputLine inputLine) {
        try {
            inputLine.content = this.reader.readLine();
            inputLine.lineNumber++;
            while (inputLine.content != null && inputLine.content.trim().length() == 0) {
                inputLine.content = this.reader.readLine();
                inputLine.lineNumber++;
            }
            if (inputLine.content != null) {
                return inputLine;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new LowLevelDeserializationException(e);
        }
    }
    
    private static final class InputLine {
        String content;
        int lineNumber;
        
        void copyFrom(InputLine other) {
            this.content = other.content;
            this.lineNumber = other.lineNumber;
        }
        
        boolean after(InputLine other) {
            return this.lineNumber > other.lineNumber;
            
        }
    }
}
