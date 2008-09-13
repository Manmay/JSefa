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

import static org.jsefa.common.lowlevel.io.LineSegment.Terminator.EOS;
import static org.jsefa.common.lowlevel.io.LineSegment.Terminator.LINE_BREAK;
import static org.jsefa.common.lowlevel.io.LineSegment.Terminator.NONE;
import static org.jsefa.common.lowlevel.io.LineSegment.Terminator.SPECIAL_CHARACTER;

import java.io.IOException;
import java.io.Reader;

import org.jsefa.common.lowlevel.InputPosition;

/**
 * A reader for reading {@link LineSegment}s from a stream.
 * <p>
 * 
 * @author Norman Lahme-Huetig
 */
public class LineSegmentReader {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int DEFAULT_MIN_BUFFER_FILLING = 1024;
    private static final int DEFAULT_BUFFER_ENLARGEMENT = 1024;

    private Reader reader;
    private char[] buffer;
    private int bufferEnlargement;
    private int minBufferFilling;
    private int noCharsInBuffer;
    private int nextCharIndex;
    private int lineIndex;
    private int columnIndex;
    private boolean skipLF = false;
    private Marker marker = null;

    /**
     * Constructs a new <code>LineSegmentReader</code>.
     * 
     * @param reader the reader to access the stream with
     */
    public LineSegmentReader(Reader reader) {
        this(reader, DEFAULT_BUFFER_SIZE, DEFAULT_MIN_BUFFER_FILLING, DEFAULT_BUFFER_ENLARGEMENT);
    }

    /**
     * Constructs a new <code>LineSegmentReader</code>.
     * 
     * @param reader the reader to access the stream with
     * @param bufferSize the initial size of the character buffer
     * @param minBufferFilling the minimum number of characters to fill the buffer with. Only relevant when a mark is
     *        set.
     * @param bufferEnlargement the number of characters to enlarge the buffer if it gets too small. Only relevant when
     *        a mark is set.
     */
    public LineSegmentReader(Reader reader, int bufferSize, int minBufferFilling, int bufferEnlargement) {
        this.reader = reader;
        this.buffer = new char[bufferSize];
        this.minBufferFilling = minBufferFilling;
        this.bufferEnlargement = bufferEnlargement;
        this.nextCharIndex = 0;
        this.noCharsInBuffer = 0;
    }

    /**
     * Reads a <code>LineSegment</code> from the stream.
     * <p>
     * The segment is terminated either with a line break or end of stream.
     * 
     * @return a <code>LineSegment</code> or null if there are no characters left on the stream
     * @throws IOException if an I/O error occurs
     */
    public LineSegment read() throws IOException {
        return read(-1, -1);
    }

    /**
     * Reads a <code>LineSegment</code> from the stream with an upper bound for the length of its content.
     * <p>
     * The segment is either terminated (with a line break or end of stream) or not (if the limit is reached).
     * 
     * @param limit the upper bound for the size of the <code>LineSegment</code> (length of its content).
     * @return a <code>LineSegment</code> or null if there are no characters left on the stream
     * @throws IOException if an I/O error occurs
     */
    public LineSegment read(int limit) throws IOException {
        return read(-1, limit);
    }

    /**
     * Reads a <code>LineSegment</code> from the stream.
     * 
     * @param specialTerminator the character to use as a special terminator for the <code>LineSegement</code> or -1, if
     *        only the standard terminators shall be used.
     * @param limit the upper bound for the size of the <code>LineSegment</code> (length of its content).
     * @return a <code>LineSegment</code> or null if there are no characters left on the stream
     * @throws IOException if an I/O error occurs
     */
    public LineSegment read(int specialTerminator, int limit) throws IOException {
        int columnNumber = this.columnIndex + 1;
        StringBuilder contentBuilder = null;
        while (true) {
            if (this.nextCharIndex >= this.noCharsInBuffer) {
                if (!fill()) {
                    // end of stream
                    if (contentBuilder != null && contentBuilder.length() > 0) {
                        return new LineSegment(contentBuilder.toString(), this.lineIndex + 1, columnNumber, EOS);
                    } else {
                        return null;
                    }
                }
            }

            if (this.skipLF && (this.buffer[this.nextCharIndex] == '\n')) {
                this.nextCharIndex++;
                this.skipLF = false;
            }

            boolean eolFound = false;
            boolean specialCharFound = false;
            int index;
            int maxIndex = this.noCharsInBuffer - 1;
            if (limit > 0) {
                maxIndex = Math.min(this.noCharsInBuffer - 1, this.nextCharIndex + limit);
                if (contentBuilder != null) {
                    maxIndex -= contentBuilder.length();
                }
            }
            for (index = this.nextCharIndex; index <= maxIndex; index++) {
                char nextChar = this.buffer[index];
                if (nextChar == '\r' || nextChar == '\n') {
                    if (nextChar == '\r') {
                        this.skipLF = true;
                    }
                    eolFound = true;
                    break;
                }
                if (nextChar == specialTerminator) {
                    specialCharFound = true;
                    break;
                }
            }

            int length = index - this.nextCharIndex;
            if (eolFound || specialCharFound) {
                // do not include the terminator
                String content = new String(buffer, this.nextCharIndex, length);
                if (contentBuilder != null) {
                    contentBuilder.append(content);
                    content = contentBuilder.toString();
                }
                this.nextCharIndex += (length + 1); // skip the terminator
                if (eolFound) {
                    this.columnIndex = 0;
                    return new LineSegment(content, ++this.lineIndex, columnNumber, LINE_BREAK);
                } else {
                    this.columnIndex += (length + 1);
                    return new LineSegment(content, this.lineIndex + 1, columnNumber, SPECIAL_CHARACTER);
                }
            } else {
                if (limit > 0) {
                    int charLeft = (contentBuilder == null) ? limit : limit - contentBuilder.length();
                    length = Math.min(charLeft, length);
                }
                String content = new String(buffer, this.nextCharIndex, length);
                if (contentBuilder == null) {
                    contentBuilder = new StringBuilder();
                }
                contentBuilder.append(content);
                this.columnIndex += length;
                this.nextCharIndex += length;
                if (contentBuilder.length() == limit && index < this.noCharsInBuffer) {
                    return new LineSegment(contentBuilder.toString(), this.lineIndex + 1, columnNumber, NONE);
                }
            }
        }
    }

    /**
     * Skips the current line, i. e. moves to the character after the next line break.
     * 
     * @throws IOException if an I/O error occurs
     */
    public void skipLine() throws IOException {
        while (true) {
            if (this.nextCharIndex >= this.noCharsInBuffer) {
                if (!fill()) {
                    return;
                }
            }

            if (this.skipLF && (this.buffer[this.nextCharIndex] == '\n')) {
                this.nextCharIndex++;
                this.skipLF = false;
            }

            while (this.nextCharIndex < this.noCharsInBuffer) {
                char nextChar = this.buffer[nextCharIndex++];
                if (nextChar == '\r' || nextChar == '\n') {
                    if (nextChar == '\r') {
                        this.skipLF = true;
                    }
                    this.columnIndex = 0;
                    this.lineIndex++;
                    return;
                }
            }

        }
    }
    
    /**
     * Returns information about the current position within the input stream.
     * @return the input position
     */
    public InputPosition getInputPosition() {
        return new InputPosition(this.lineIndex + 1, this.columnIndex + 1);
    }

    /**
     * Marks the current position in the stream so that calling {@link #reset} will return to this position.
     * <p>
     * Note: You should remove the marker with {@link #removeMarker} if you do not need it any more.
     */
    public void mark() {
        this.marker = new Marker(this.nextCharIndex, this.skipLF, this.lineIndex, this.columnIndex);
    }

    /**
     * Moves back to the position marked with {@link #mark()} before.
     * 
     * @param removeMarker true, if the marker should be removed afterwards
     */
    public void reset(boolean removeMarker) {
        if (this.marker != null) {
            this.nextCharIndex = this.marker.nextCharIndex;
            this.skipLF = this.marker.skipLF;
            this.lineIndex = this.marker.lineIndex;
            this.columnIndex = this.marker.columnIndex;
            if (removeMarker) {
                removeMarker();
            }
        }
    }

    /**
     * Removes the marker.
     */
    public void removeMarker() {
        this.marker = null;
    }

    /**
     * Closes the stream.
     * 
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        if (this.reader == null) {
            return;
        }
        this.reader.close();
        this.reader = null;
        this.buffer = null;
    }

    private boolean fill() throws IOException {
        int destIndex;

        if (this.marker == null) {
            destIndex = 0;
        } else {
            destIndex = this.nextCharIndex - this.marker.nextCharIndex;
            if (this.marker.nextCharIndex >= this.minBufferFilling) {
                System.arraycopy(this.buffer, this.marker.nextCharIndex, this.buffer, 0, destIndex);
                this.marker.nextCharIndex = 0;
            } else {
                char[] newBuffer = new char[this.buffer.length + this.bufferEnlargement];
                System.arraycopy(this.buffer, this.marker.nextCharIndex, newBuffer, 0, destIndex);
                this.buffer = newBuffer;
                this.marker.nextCharIndex = 0;
            }
            this.nextCharIndex = destIndex;
            this.noCharsInBuffer = destIndex;
        }

        int noReadChars = 0;

        do {
            noReadChars = this.reader.read(this.buffer, destIndex, this.buffer.length - destIndex);
        } while (noReadChars == 0);

        if (noReadChars > 0) {
            this.noCharsInBuffer = destIndex + noReadChars;
            this.nextCharIndex = destIndex;
            return true;
        } else {
            return false;
        }
    }

    private static final class Marker {
        int nextCharIndex;
        boolean skipLF;
        int lineIndex;
        int columnIndex;

        Marker(int nextCharIndex, boolean skipLF, int lineIndex, int columnIndex) {
            this.nextCharIndex = nextCharIndex;
            this.skipLF = skipLF;
            this.lineIndex = lineIndex;
            this.columnIndex = columnIndex;
        }
    }
}
