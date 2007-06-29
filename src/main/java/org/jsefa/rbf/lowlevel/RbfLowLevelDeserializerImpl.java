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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.jsefa.DeserializationException;


/**
 * Abstract implementation of {@link RbfLowLevelDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class RbfLowLevelDeserializerImpl implements RbfLowLevelDeserializer {
    
    private BufferedReader reader;

    private boolean linePrefetched;

    private String currentLine;

    private int currentIndex;
    
    /**
     * {@inheritDoc}
     */
    public final void open(Reader reader) {
        this.currentLine = null;
        this.linePrefetched = false;
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean readNextRecord() {
        this.currentIndex = 0;
        if (this.linePrefetched) {
            this.linePrefetched = false;
            return this.currentLine != null;
        }
        try {
            this.currentLine = this.reader.readLine();
            while (this.currentLine != null && this.currentLine.trim().length() == 0) {
                this.currentLine = this.reader.readLine();
            }
            return this.currentLine != null;
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void unreadRecord() {
        this.currentIndex = 0;
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
                throw new DeserializationException(e);
            }
        }
    }
    
    /**
     * Returns true, if there is another character on the current line to read.
     * 
     * @return true, if there is another character on the current line to read;
     *         false otherwise.
     */
    protected final boolean hasNextChar() {
        return this.currentIndex < this.currentLine.length();
    }

    /**
     * Returns the current character of the current line.
     * 
     * @return a character
     */
    protected final char peekChar() {
        try {
            return this.currentLine.charAt(this.currentIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new DeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next character of the current line and moves one character
     * forward.
     * 
     * @return a character
     */
    protected final char nextChar() {
        try {
            return this.currentLine.charAt(this.currentIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new DeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next <code>String</code> with the given length of the
     * current line and moves forward accordingly.
     * 
     * @param length the length of the <code>String</code> to return
     * @return a <code>String</code>
     */
    protected final String nextString(int length) {
        try {
            String value = this.currentLine.substring(this.currentIndex, this.currentIndex + length);
            this.currentIndex += length;
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new DeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the number of remaining characters in the current line.
     * 
     * @return the number of remaining characters in the current line.
     */
    protected final int remainingLineLength() {
        return this.currentLine.length() - this.currentIndex;
    }    
}