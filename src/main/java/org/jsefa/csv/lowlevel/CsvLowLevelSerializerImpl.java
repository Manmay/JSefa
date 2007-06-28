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

import org.jsefa.rbf.lowlevel.RbfLowLevelSerializerImpl;

/**
 * Implementation of {@link CsvLowLevelSerializer} based on
 * {@link RbfLowLevelSerializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CsvLowLevelSerializerImpl extends RbfLowLevelSerializerImpl implements CsvLowLevelSerializer {

    private CsvLowLevelConfiguration config;

    private int fieldCount;

    /**
     * Constructs a new <code>CsvLowLevelSerializerImpl</code>.
     * 
     * @param config the configuration
     */
    public CsvLowLevelSerializerImpl(CsvLowLevelConfiguration config) {
        super(config.getLineBreak());
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterOpen() {
        this.fieldCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void writeField(String value, QuoteMode quoteMode) {
        if (this.fieldCount > 0) {
            writeChar(this.config.getFieldDelimiter());
        }
        encodeAndWrite(value, quoteMode);
        fieldCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeFinishRecord() {
        if (this.config.getUseDelimiterAfterLastField()) {
            writeChar(this.config.getFieldDelimiter());
        }
        this.fieldCount = 0;
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
        char escapeCharacter = CsvLowLevelConstants.ESCAPE_CHARACTER;
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

    private void encodeAndWriteUsingEscapeCharacter(String value) {
        int index = 0;
        while (index < value.length()) {
            char currentChar = value.charAt(index++);
            if (currentChar == CsvLowLevelConstants.ESCAPE_CHARACTER
                    || currentChar == this.config.getFieldDelimiter()) {
                writeChar(CsvLowLevelConstants.ESCAPE_CHARACTER);
            }
            writeChar(currentChar);
        }
    }

    private boolean needsQuotes(String value) {
        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            if (currentChar == CsvLowLevelConstants.ESCAPE_CHARACTER
                    || currentChar == this.config.getFieldDelimiter()) {
                return true;
            }
        }
        return false;
    }

}
