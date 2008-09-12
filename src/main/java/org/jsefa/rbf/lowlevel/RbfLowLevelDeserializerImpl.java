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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.jsefa.common.lowlevel.InputPosition;
import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.common.lowlevel.filter.FilterResult;
import org.jsefa.common.lowlevel.filter.Line;
import org.jsefa.common.lowlevel.io.LineSegment;
import org.jsefa.common.lowlevel.io.LineSegmentReader;
import org.jsefa.common.lowlevel.io.LineSegment.Terminator;
import org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration;

/**
 * Abstract implementation of {@link RbfLowLevelDeserializer}.
 * 
 * @param <C> the type of the RbfLowLevelConfiguration
 * @author Norman Lahme-Huetig
 */
public abstract class RbfLowLevelDeserializerImpl<C extends RbfLowLevelConfiguration> implements
        RbfLowLevelDeserializer {

    private LineSegmentReader reader;

    private LineSegment currentSegment;

    private int currentColumnIndex;

    private C config;

    private List<Line> storedLines;

    private int lineFilterLimit;

    private int specialTerminator;

    /**
     * Constructs a new <code>RbfLowLevelDeserializerImpl</code>.
     * 
     * @param config the configuration object
     */
    public RbfLowLevelDeserializerImpl(C config) {
        this.config = config;
        if (this.config.getSpecialRecordDelimiter() != null) {
            this.specialTerminator = this.config.getSpecialRecordDelimiter();
            this.lineFilterLimit = this.config.getLineFilterLimit();
        } else {
            this.lineFilterLimit = -1;
            this.specialTerminator = -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void open(Reader reader) {
        this.reader = new LineSegmentReader(reader);
        this.storedLines = new ArrayList<Line>();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean readNextRecord() {
        if (withLineFilter() && !applyFilter()) {
            return false;
        }
        do {
            this.reader.mark();
            if (!readNextSegment()) {
                return false;
            }
        } while (this.currentSegment.getContent().length() == 0);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public final void unreadRecord() {
        this.currentColumnIndex = 0;
        this.reader.reset(true);
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
        if (this.currentSegment != null) {
            return new InputPosition(this.currentSegment.getLineNumber(), this.currentSegment.getColumnNumber()
                    + this.currentColumnIndex);
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
     * 
     * @return the configuration object
     */
    protected final C getConfiguration() {
        return this.config;
    }

    /**
     * Returns true, if there is another character on the current line segment to read.
     * 
     * @return true, if there is another character on the current line segment to read; false otherwise.
     */
    protected final boolean hasNextChar() {
        return this.currentColumnIndex < this.currentSegment.getContent().length();
    }

    /**
     * Returns the current character of the current line segment.
     * 
     * @return a character
     */
    protected final char peekChar() {
        try {
            return this.currentSegment.getContent().charAt(this.currentColumnIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new LowLevelDeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next character of the current line segment and moves one character forward.
     * 
     * @return a character
     */
    protected final char nextChar() {
        try {
            return this.currentSegment.getContent().charAt(this.currentColumnIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new LowLevelDeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next <code>String</code> with the given length of the current line segment and moves forward
     * accordingly.
     * 
     * @param length the length of the <code>String</code> to return
     * @return a <code>String</code>
     */
    protected final String nextString(int length) {
        try {
            String value = this.currentSegment.getContent().substring(this.currentColumnIndex,
                    this.currentColumnIndex + length);
            this.currentColumnIndex += length;
            return value;
        } catch (IndexOutOfBoundsException e) {
            if (this.currentColumnIndex < this.currentSegment.getContent().length()) {
                String value = this.currentSegment.getContent().substring(this.currentColumnIndex);
                this.currentColumnIndex += value.length();
                return value;
            }
            return null;
        }
    }

    /**
     * @return the number of remaining characters in the current line segment.
     */
    protected final int remainingLineLength() {
        return this.currentSegment.getContent().length() - this.currentColumnIndex;
    }

    /**
     * Reads the next segment from the stream.
     * 
     * @return true, if a segment could be read; false otherwise
     */
    protected boolean readNextSegment() {
        try {
            this.currentSegment = reader.read(this.specialTerminator, -1);
            this.currentColumnIndex = 0;
            return this.currentSegment != null;
        } catch (IOException e) {
            throw new LowLevelDeserializationException(e);
        }
    }

    /**
     * @return the string terminating the current segment.
     */
    protected String getCurrentSegmentTerminatorString() {
        if (this.currentSegment == null) {
            return "";
        }
        switch (this.currentSegment.getTerminator()) {
        case LINE_BREAK:
            return "\n";
        case SPECIAL_CHARACTER:
            return new StringBuffer().append(this.config.getSpecialRecordDelimiter().charValue()).toString();
        default:
            return "";
        }
    }
    
    /**
     * @return the terminator of the current segment.
     */
    protected Terminator getCurrentSegmentTerminator() {
        if (this.currentSegment == null) {
            return null;
        }
        return this.currentSegment.getTerminator();
        
    }

    private boolean withLineFilter() {
        return this.config.getLineFilter() != null;
    }

    private boolean applyFilter() {
        try {
            while (true) {
                this.reader.mark();
                LineSegment lineToFilter = reader.read(this.lineFilterLimit);
                if (lineToFilter == null) {
                    return false;
                }
                if (lineToFilter.getContent().trim().length() == 0) {
                    continue;
                }
                if (lineToFilter.getColumnNumber() > 1) {
                    this.reader.reset(false);
                    return true;
                }
                boolean isLastLine;
                int skipCounter = 1;
                if (lineToFilter.getTerminator() == Terminator.NONE) {
                    isLastLine = false;
                } else if (lineToFilter.getTerminator() == Terminator.EOS) {
                    isLastLine = true;
                } else {
                    LineSegment nextLine = this.reader.read(this.lineFilterLimit);
                    while (nextLine != null && nextLine.getContent().trim().length() == 0) {
                        nextLine = reader.read(this.lineFilterLimit);
                        skipCounter++;
                    }
                    isLastLine = (nextLine == null);
                }
                this.reader.reset(false);
                if (passesFilter(lineToFilter, isLastLine)) {
                    break;
                } else if (isLastLine) {
                    return false;
                } else {
                    for (int i = 0; i < skipCounter; i++) {
                        this.reader.skipLine();
                    }
                }
            }
            return true;
        } catch (IOException e) {
            throw new LowLevelDeserializationException(e);
        }
    }

    private boolean passesFilter(LineSegment line, boolean isLastLine) {
        boolean truncated = line.getTerminator() == Terminator.NONE;
        FilterResult result = this.config.getLineFilter().filter(line.getContent(), line.getLineNumber(), truncated,
                isLastLine);
        if (result == PASSED) {
            return true;
        }
        if (result == FAILED_BUT_STORE) {
            this.storedLines.add(new Line(line.getContent(), line.getLineNumber(), truncated, isLastLine));
        }
        return false;
    }

}
