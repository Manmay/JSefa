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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.jsefa.common.lowlevel.LowLevelDeserializationException;

/**
 * A reader for reading complete lines only.
 * <p>
 * Besides providing the number of the current line it also allows for unreading a previously read line so that the next
 * call of {@link #recentLine} will return it again. Furthermore it wraps {@link IOException}s with
 * {@link LowLevelDeserializationException}s.
 * <p>
 * Note: As this class is for reading complete lines only and as it wraps <code>IOException</code>s it does not extend
 * {@link Reader}.
 * 
 * @author Norman Lahme-Huetig
 */
public class LineReader {
    private BufferedReader reader;
    private String recentLine;
    private boolean lineUnread;
    private int lineNumber;

    /**
     * Constructs a new <code>LineReader</code> based on a given {@link Reader}.
     * 
     * @param reader the underlying reader.
     */
    public LineReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        this.lineUnread = false;
        this.lineNumber = 0;
    }

    /**
     * @return returns the next complete line or null if none exists.
     */
    public String readLine() {
        if (this.lineNumber == -1) {
            return null;
        }
        if (this.lineUnread) {
            this.lineUnread = false;
        } else {
            this.recentLine = readNextNonEmptyLine();
        }
        return this.recentLine;
    }

    /**
     * @return the number of the current line (starting with 1 for the first line). Returns 0 if no line is read so far.
     *         Returns -1 if the end of the stream was reached.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Unreads the previously read line so that {@link #readLine()} will return it again.
     */
    public final void unreadLine() {
        this.lineUnread = true;
    }

    /**
     * Closes the underlying reader.
     */
    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new LowLevelDeserializationException("Error while closing the deserialization stream", e);
        }
    }

    /**
     * @return true, if the previously read line is unread so that {@link #readLine()} will return it again.
     */
    protected boolean isLineUnread() {
        return this.lineUnread;
    }

    private String readNextNonEmptyLine() {
        try {
            String result = this.reader.readLine();
            this.lineNumber++;
            while (result != null && result.trim().length() == 0) {
                result = this.reader.readLine();
                this.lineNumber++;
            }
            if (result == null) {
                this.lineNumber = -1;
            }
            return result;
        } catch (IOException e) {
            throw new LowLevelDeserializationException(e);
        }
    }

}
