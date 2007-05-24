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

import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.config.EscapeMode;
import org.jsefa.csv.mapping.CsvSimpleTypeMapping;
import org.jsefa.csv.mapping.QuoteMode;
import org.jsefa.rbf.RbfEntryPoint;
import org.jsefa.rbf.RbfSerializer;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Default implementation of {@link CsvSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class CsvSerializerImpl extends RbfSerializer implements CsvSerializer {

    private final CsvConfiguration config;

    CsvSerializerImpl(CsvConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<Class, RbfEntryPoint> entryPoints) {
        super(typeMappingRegistry, entryPoints, config.getLineBreak());
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSimpleValue(Object object, SimpleTypeMapping mapping) {
        if (getColumnIndex() > 0) {
            writeChar(this.config.getFieldDelimiter());
        }
        String value = mapping.getSimpleTypeConverter().toString(object);
        encodeAndWrite(value, ((CsvSimpleTypeMapping) mapping).getQuoteMode());
    }

    /**
     * {@inheritDoc}
     */
    protected void beforeEOL() {
        if (this.config.getUseDelimiterAfterLastField()) {
            writeChar(this.config.getFieldDelimiter());
        }
    }

    private void encodeAndWrite(String value, QuoteMode quoteMode) {
        if (value == null) {
            return;
        }
        switch (quoteMode) {
        case ALWAYS:
            encodeAndWriteUsingQuotes(value);
            break;
        case ON_DEMAND:
            encodeAndWriteUsingQuotesOnDemand(value);
            break;
        case NEVER:
            encodeAndWriteUsingEscapeCharacter(value);
            break;
        default:
            throw new UnsupportedOperationException("The quote mode is not supported: " + quoteMode);
        }
    }

    private void encodeAndWriteUsingQuotes(String value) {
        char quoteChar = this.config.getQuoteCharacter();
        char escapeCharacter = CsvConstants.ESCAPE_CHARACTER;
        if (this.config.getQuoteCharacterEscapeMode().equals(EscapeMode.DOUBLING)) {
            escapeCharacter = quoteChar;
        }
        writeChar(quoteChar);
        int index = 0;
        while (index < value.length()) {
            char currentChar = value.charAt(index++);
            if (currentChar == quoteChar) {
                writeChar(escapeCharacter);
            }
            writeChar(currentChar);
        }
        writeChar(this.config.getQuoteCharacter());
    }

    private void encodeAndWriteUsingQuotesOnDemand(String value) {
        if (needsQuotes(value)) {
            encodeAndWriteUsingQuotes(value);
        } else {
            writeString(value);
        }
    }

    private boolean needsQuotes(String value) {
        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            if (currentChar == CsvConstants.ESCAPE_CHARACTER || currentChar == this.config.getFieldDelimiter()) {
                return true;
            }
        }
        return false;
    }

    private void encodeAndWriteUsingEscapeCharacter(String value) {
        int index = 0;
        while (index < value.length()) {
            char currentChar = value.charAt(index++);
            if (currentChar == CsvConstants.ESCAPE_CHARACTER || currentChar == this.config.getFieldDelimiter()) {
                writeChar(CsvConstants.ESCAPE_CHARACTER);
            }
            writeChar(currentChar);
        }
    }

}
