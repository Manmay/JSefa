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

import org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration;
import org.jsefa.csv.lowlevel.config.EscapeMode;
import org.jsefa.csv.lowlevel.config.QuoteMode;
import org.jsefa.rbf.lowlevel.RbfLowLevelSerializerImpl;

/**
 * Implementation of {@link CsvLowLevelSerializer} based on {@link RbfLowLevelSerializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 */
public class CsvLowLevelSerializerImpl extends RbfLowLevelSerializerImpl<CsvLowLevelConfiguration> implements
        CsvLowLevelSerializer {

    private int fieldCount;

    private int specialRecordDelimiter;

    /**
     * Constructs a new <code>CsvLowLevelSerializerImpl</code>.
     * 
     * @param config the configuration
     */
    public CsvLowLevelSerializerImpl(CsvLowLevelConfiguration config) {
        super(config);
        if (config.getSpecialRecordDelimiter() != null) {
            this.specialRecordDelimiter = config.getSpecialRecordDelimiter();
        } else {
            this.specialRecordDelimiter = -1;
        }
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
            writeChar(getConfiguration().getFieldDelimiter());
        }
        encodeAndWrite(value, quoteMode);
        fieldCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeFinishRecord() {
        if (getConfiguration().getUseDelimiterAfterLastField()) {
            writeChar(getConfiguration().getFieldDelimiter());
        }
        this.fieldCount = 0;
    }

    private void encodeAndWrite(String value, QuoteMode quoteMode) {
        if (value.length() == 0) {
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
        char quoteChar = getConfiguration().getQuoteCharacter();
        char escapeCharacter = getConfiguration().getEscapeCharacter();
        if (getConfiguration().getQuoteCharacterEscapeMode().equals(EscapeMode.DOUBLING)) {
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
        writeChar(getConfiguration().getQuoteCharacter());
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
            if (currentChar == getConfiguration().getEscapeCharacter()
                    || currentChar == getConfiguration().getFieldDelimiter()
                    || currentChar == this.specialRecordDelimiter) {
                writeChar(getConfiguration().getEscapeCharacter());
                writeChar(currentChar);
            } else if (currentChar == '\n') {
                writeChar(getConfiguration().getEscapeCharacter());
                writeChar('n');
            } else {
                writeChar(currentChar);
            }
        }
    }

    private boolean needsQuotes(String value) {
        if (value.charAt(0) == getConfiguration().getQuoteCharacter()) {
            return true;
        }
        for (int i = 0; i < value.length(); i++) {
            char currentChar = value.charAt(i);
            if (currentChar == getConfiguration().getEscapeCharacter()
                    || currentChar == getConfiguration().getFieldDelimiter()) {
                return true;
            }
        }
        if (value.contains(getConfiguration().getLineBreak())) {
            return true;
        }
        return false;
    }

}
