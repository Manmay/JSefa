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

import org.jsefa.DeserializationException;
import org.jsefa.rbf.lowlevel.RbfLowLevelDeserializerImpl;

/**
 * Implementation of {@link CsvLowLevelDeserializer} based on
 * {@link RbfLowLevelDeserializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class CsvLowLevelDeserializerImpl extends RbfLowLevelDeserializerImpl implements
        CsvLowLevelDeserializer {

    private CsvLowLevelConfiguration config;

    /**
     * Constructs a new <code>CsvLowLevelDeserializerImpl</code>.
     * 
     * @param config the configuration
     */
    public CsvLowLevelDeserializerImpl(CsvLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public String nextField(QuoteMode quoteMode) {
        if (!hasNextChar()) {
            return "";
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

    private String readStringValueUsingQuotes() {
        char quoteChar = this.config.getQuoteCharacter();
        char startChar = nextChar();
        if (startChar == this.config.getFieldDelimiter()) {
            return "";
        } else if (startChar != quoteChar) {
            throw new DeserializationException("Expected quote char but got " + startChar);
        }
        char escapeCharacter = CsvLowLevelConstants.ESCAPE_CHARACTER;
        if (this.config.getQuoteCharacterEscapeMode().equals(EscapeMode.DOUBLING)) {
            escapeCharacter = quoteChar;
        }
        return readStringValueUsingQuotes(quoteChar, escapeCharacter, this.config.getFieldDelimiter());
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
                            return result.toString();
                        } else if (hasNextChar() && peekChar() == fieldDelimiter) {
                            nextChar();
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
            if (readNextRecord()) {
                result.append("\n");
            } else {
                break;
            }
        }
        return result.toString();
    }

    private String readStringValueUsingQuotesOnDemand() {
        if (peekChar() == this.config.getQuoteCharacter()) {
            return readStringValueUsingQuotes();
        } else {
            char fieldDelimiter = this.config.getFieldDelimiter();
            StringBuilder result = new StringBuilder(remainingLineLength());
            while (hasNextChar()) {
                char currentChar = nextChar();
                if (currentChar == fieldDelimiter) {
                    return result.toString();
                } else {
                    result.append(currentChar);
                }
            }
            return result.toString();
        }
    }

    private String readStringValueUsingEscapeCharacter() {
        char fieldDelimiter = this.config.getFieldDelimiter();
        StringBuilder result = new StringBuilder(remainingLineLength());
        boolean encoded = false;
        while (hasNextChar()) {
            char currentChar = nextChar();
            if (encoded) {
                encoded = false;
                result.append(currentChar);
            } else {
                if (currentChar == CsvLowLevelConstants.ESCAPE_CHARACTER) {
                    encoded = true;
                } else if (currentChar == fieldDelimiter) {
                    return result.toString();
                } else {
                    result.append(currentChar);
                }
            }
        }
        return result.toString();
    }

}
