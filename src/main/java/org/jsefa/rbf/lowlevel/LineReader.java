package org.jsefa.rbf.lowlevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.jsefa.common.lowlevel.LowLevelDeserializationException;

class LineReader {
    private BufferedReader reader;
    protected InputLine currentLine;
    protected boolean prefetched;

    public LineReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        this.currentLine = new InputLine();
        this.prefetched = false;
    }

    public String readLine() {
        if (this.currentLine == null) {
            return null;
        }
        if (this.prefetched) {
            this.prefetched = false;
        } else {
            this.currentLine = readNextNonEmptyLine(currentLine);
            if (currentLine == null) {
                return null;
            }
        }
        return this.currentLine.content;
    }
    
    public final void unreadRecord() {
        this.prefetched = true;
    }

    /**
     * @return the number of the current line (starting with 1 for the first line). Returns 0 if none line is read so
     *         far.
     */
    public final int getLineNumber() {
        if (this.currentLine == null) {
            return 0;
        } else {
            return this.currentLine.lineNumber;
        }
    }
    
    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw new LowLevelDeserializationException("Error while closing the deserialization stream", e);
        }
    }
    
    protected final InputLine readNextNonEmptyLine(InputLine line) {
        try {
            line.content = this.reader.readLine();
            line.lineNumber++;
            while (line.content != null && line.content.trim().length() == 0) {
                line.content = this.reader.readLine();
                line.lineNumber++;
            }
            if (line.content != null) {
                return line;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new LowLevelDeserializationException(e);
        }
    }

    protected static final class InputLine {
        String content;
        int lineNumber;
        
        void copyFrom(InputLine other) {
            this.content = other.content;
            this.lineNumber = other.lineNumber;
        }        
    }
}
