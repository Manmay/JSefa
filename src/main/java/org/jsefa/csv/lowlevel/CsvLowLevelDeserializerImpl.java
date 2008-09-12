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

package org.jsefa.csv.lowlevel;

import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration;
import org.jsefa.csv.lowlevel.config.EscapeMode;
import org.jsefa.csv.lowlevel.config.QuoteMode;
import org.jsefa.rbf.lowlevel.RbfLowLevelDeserializerImpl;

import static org.jsefa.common.lowlevel.io.LineSegment.Terminator.*;


/**
 * Implementation of {@link CsvLowLevelDeserializer} based on {@link RbfLowLevelDeserializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 */

public final class CsvLowLevelDeserializerImpl extends RbfLowLevelDeserializerImpl<CsvLowLevelConfiguration> implements
        CsvLowLevelDeserializer {

    private boolean lastFieldTerminatedWithDelimiter;

    /**
     * Constructs a new <code>CsvLowLevelDeserializerImpl</code>.
     * 
     * @param config the configuration
     */
    public CsvLowLevelDeserializerImpl(CsvLowLevelConfiguration config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    public String nextField(QuoteMode quoteMode) {
        if (!hasNextChar()) {
            return endOfLineField();
        }
        switch (quoteMode) {
        case ALWAYS:
            return readStringValueUsingQuotes();
        case ON_DEMAND:
            return readStringValueUsingQuotesOnDemand();
        case NEVER:
            return readStringValueUsingEscapeCharacter();
        default:
            throw new UnsupportedOperationException("The quote mode is not supported: " + quoteMode);
        }
    }

    private String endOfLineField() {
        if (getConfiguration().getUseDelimiterAfterLastField()) {
            return null;
        } else {
            if (this.lastFieldTerminatedWithDelimiter) {
                this.lastFieldTerminatedWithDelimiter = false;
                return "";
            } else {
                return null;
            }
        }
    }

    private String readStringValueUsingQuotes() {
        char quoteChar = getConfiguration().getQuoteCharacter();
        char startChar = nextChar();
        if (startChar == getConfiguration().getFieldDelimiter()) {
            this.lastFieldTerminatedWithDelimiter = true;
            return "";
        } else if (startChar != quoteChar) {
            throw new LowLevelDeserializationException("Expected quote char but got " + startChar);
        }
        char escapeCharacter = getConfiguration().getEscapeCharacter();
        if (getConfiguration().getQuoteCharacterEscapeMode().equals(EscapeMode.DOUBLING)) {
            escapeCharacter = quoteChar;
        }
        return readStringValueUsingQuotes(quoteChar, escapeCharacter, getConfiguration().getFieldDelimiter());
    }

    private String readStringValueUsingQuotes(char quoteChar, char escapeCharacter, char fieldDelimiter) {
        StringBuilder result = new StringBuilder(remainingLineLength());
        boolean encoded = false;
        while (true) {
            while (hasNextChar()) {
                char currentChar = nextChar();
                if (encoded) {
                    encoded = false;
                    result.append(currentChar);
                } else {
                    if (currentChar == quoteChar) {
                        if (!hasNextChar()) {
                            this.lastFieldTerminatedWithDelimiter = false;
                            return result.toString();
                        } else if (hasNextChar() && peekChar() == fieldDelimiter) {
                            nextChar();
                            this.lastFieldTerminatedWithDelimiter = true;
                            return result.toString();
                        }
                    }
                    if (currentChar == escapeCharacter) {
                        encoded = true;
                    } else {
                        result.append(currentChar);
                    }
                }
            }
            result.append(getCurrentSegmentTerminatorString());
            if (!readNextSegment()) {
                break;
            }
        }
        this.lastFieldTerminatedWithDelimiter = false;
        return result.toString();
    }

    private String readStringValueUsingQuotesOnDemand() {
        if (peekChar() == getConfiguration().getQuoteCharacter()) {
            return readStringValueUsingQuotes();
        } else {
            char fieldDelimiter = getConfiguration().getFieldDelimiter();
            StringBuilder result = new StringBuilder(remainingLineLength());
            while (hasNextChar()) {
                char currentChar = nextChar();
                if (currentChar == fieldDelimiter) {
                    this.lastFieldTerminatedWithDelimiter = true;
                    return result.toString();
                } else {
                    result.append(currentChar);
                }
            }
            this.lastFieldTerminatedWithDelimiter = false;
            return result.toString();
        }
    }

    private String readStringValueUsingEscapeCharacter() {
        char fieldDelimiter = getConfiguration().getFieldDelimiter();
        StringBuilder result = new StringBuilder(remainingLineLength());
        boolean escaped = false;
        while (true) {
            while (hasNextChar()) {
                char currentChar = nextChar();
                if (escaped) {
                    escaped = false;
                    if (currentChar == 'n') {
                        result.append("\n");
                    } else {
                        result.append(currentChar);
                    }
                } else {
                    if (currentChar == getConfiguration().getEscapeCharacter()) {
                        escaped = true;
                    } else if (currentChar == fieldDelimiter) {
                        this.lastFieldTerminatedWithDelimiter = true;
                        return result.toString();
                    } else {
                        result.append(currentChar);
                    }
                }
            }
            if (escaped && getCurrentSegmentTerminator() == SPECIAL_CHARACTER) {
                escaped = false;
                result.append(getCurrentSegmentTerminatorString());
                if (readNextSegment()) {
                    continue;
                }
            }
            break;
        }
        this.lastFieldTerminatedWithDelimiter = false;
        return result.toString();
    }

}
