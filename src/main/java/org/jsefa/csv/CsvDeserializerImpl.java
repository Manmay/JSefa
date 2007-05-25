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

package org.jsefa.csv;

import java.util.Map;

import org.jsefa.DeserializationException;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.config.EscapeMode;
import org.jsefa.csv.config.QuoteMode;
import org.jsefa.csv.mapping.CsvSimpleTypeMapping;
import org.jsefa.rbf.RbfDeserializer;
import org.jsefa.rbf.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Default implementation of {@link CsvDeserializer} based on
 * {@link RbfDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvDeserializerImpl extends RbfDeserializer implements CsvDeserializer {
    private final CsvConfiguration config;

    CsvDeserializerImpl(CsvConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<String, RbfEntryPoint> entryPointsByPrefixes) {
        super(typeMappingRegistry, entryPointsByPrefixes);
        this.config = config;
    }

    CsvDeserializerImpl(CsvConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            RbfEntryPoint entryPoint) {
        super(typeMappingRegistry, entryPoint);
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    protected Object readSimpleValue(SimpleTypeMapping typeMapping) {
        String stringValue = readStringValue(((CsvSimpleTypeMapping) typeMapping).getQuoteMode());
        if (stringValue.length() > 0) {
            return typeMapping.getSimpleTypeConverter().fromString(stringValue);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String readPrefix() {
        return readStringValue(QuoteMode.NEVER);
    }

    private String readStringValue(QuoteMode quoteMode) {
        if (!hasNextChar() && !this.config.getUseDelimiterAfterLastField()) {
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
        char escapeCharacter = CsvConstants.ESCAPE_CHARACTER;
        char fieldDelimiter = this.config.getFieldDelimiter();
        if (this.config.getQuoteCharacterEscapeMode().equals(EscapeMode.DOUBLING)) {
            escapeCharacter = quoteChar;
        }

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
            if (readNextLine()) {
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
                if (currentChar == CsvConstants.ESCAPE_CHARACTER) {
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
