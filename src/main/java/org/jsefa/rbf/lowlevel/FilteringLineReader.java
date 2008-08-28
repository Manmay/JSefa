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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.jsefa.common.lowlevel.filter.FilterResult;
import org.jsefa.common.lowlevel.filter.Line;
import org.jsefa.common.lowlevel.filter.LineFilter;

final class FilteringLineReader extends LineReader {

    private InputLine currentLine;
    private InputLine nextLine;
    private LineFilter lineFilter;
    private List<Line> storedLines;

    public FilteringLineReader(Reader reader, LineFilter lineFilter) {
        super(reader);
        this.lineFilter = lineFilter;
        this.currentLine = new InputLine();
        this.nextLine = new InputLine();
        this.storedLines = new ArrayList<Line>();
    }
    
    @Override
    public String readLine() {
        while (moveForward()) {
            FilterResult filterResult = filterCurrentLine();
            if (filterResult == PASSED) {
                return this.currentLine.content;
            } else if (filterResult == FAILED_BUT_STORE) {
                storeCurrentLine();
            }
        }
        return null;
    }
    
    @Override
    public int getLineNumber() {
        if (this.currentLine != null) {
            return this.currentLine.lineNumber;
        }
        return -1;
    }

    public List<Line> getStoredLines() {
        return this.storedLines;
    }

    private boolean moveForward() {
        if (this.currentLine == null) {
            return false;
        }
        if (this.nextLine == null) {
            this.currentLine = null;
            return false;
        }
        if (isPrefetched()) {
            super.readLine();
            return true;
        }

        if (this.nextLine.lineNumber <= this.currentLine.lineNumber) {
            this.currentLine = readNextNonEmptyLine(currentLine);
        } else {
            this.currentLine.copyFrom(this.nextLine);
        }

        if (this.currentLine != null) {
            this.nextLine.lineNumber = this.currentLine.lineNumber;
            this.nextLine = readNextNonEmptyLine(this.nextLine);
            return true;
        } else {
            this.nextLine = null;
            return false;
        }
    }
    
    private InputLine readNextNonEmptyLine(InputLine inputLine) {
        String line = super.readLine();
        if (line == null) {
            return null;
        }
        inputLine.content = line;
        inputLine.lineNumber = super.getLineNumber();
        return inputLine;
    }

    private FilterResult filterCurrentLine() {
        return this.lineFilter.filter(this.currentLine.content, this.currentLine.lineNumber, this.nextLine == null);
    }

    private void storeCurrentLine() {
        this.storedLines.add(new Line(this.currentLine.content, this.currentLine.lineNumber, this.nextLine == null));
    }
    
    private static final class InputLine {
        String content;
        int lineNumber;
        
        void copyFrom(InputLine other) {
            this.content = other.content;
            this.lineNumber = other.lineNumber;
        }        
    }

}
