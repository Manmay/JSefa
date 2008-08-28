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

import org.jsefa.common.lowlevel.LowLevelDeserializationException;

class LineReader {
    private BufferedReader reader;
    private String recentLine;
    private boolean prefetched;
    private int lineNumber;

    public LineReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        this.prefetched = false;
        this.lineNumber = 0;
    }

    public String readLine() {
        if (this.lineNumber == -1) {
            return null;
        }
        if (this.prefetched) {
            this.prefetched = false;
        } else {
            this.recentLine = readNextNonEmptyLine();
        }
        return this.recentLine;
    }
    
    /**
     * @return the number of the current line (starting with 1 for the first line). Returns 0 if none line is read so
     *         far. Returns -1 if the end of the stream was reached.
     */
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public final void unreadRecord() {
        this.prefetched = true;
    }

    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new LowLevelDeserializationException("Error while closing the deserialization stream", e);
        }
    }
    
    protected boolean isPrefetched() {
        return this.prefetched;
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
