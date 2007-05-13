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

package org.jsefa.csv.config;

import org.jsefa.Configuration;
import org.jsefa.csv.CsvConstants;
import org.jsefa.csv.CsvIOFactory;

/**
 * A configuration object used when creating a {@link CsvIOFactory}.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvConfiguration extends Configuration {
    private char fieldDelimiter;

    private char quoteCharacter;

    private EscapeMode quoteCharacterEscapeMode;

    private boolean useDelimiterAfterLastField;

    /**
     * Constructs a new <code>CsvConfiguration</code>.
     */
    public CsvConfiguration() {
        this.fieldDelimiter = CsvConstants.DEFAULT_FIELD_DELIMITER;
        this.quoteCharacter = CsvConstants.DEFAULT_QUOTE_CHARACTER;
        this.quoteCharacterEscapeMode = EscapeMode.DOUBLING;
        this.useDelimiterAfterLastField = false;
    }

    private CsvConfiguration(CsvConfiguration other) {
        this.fieldDelimiter = other.fieldDelimiter;
        this.useDelimiterAfterLastField = other.useDelimiterAfterLastField;
        this.quoteCharacter = other.getQuoteCharacter();
        this.quoteCharacterEscapeMode = other.getQuoteCharacterEscapeMode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CsvConfiguration createCopy() {
        return new CsvConfiguration(this);
    }

    /**
     * Returns the delimiter used to separate the CSV fields.
     * 
     * @return the delimiter
     */
    public char getFieldDelimiter() {
        return this.fieldDelimiter;
    }

    /**
     * Returns the quote used to surround a field.
     * 
     * @return the quote character
     */
    public char getQuoteCharacter() {
        return this.quoteCharacter;
    }

    /**
     * Returns the escape mode for the quote character.
     * 
     * @return an escape mode.
     */
    public EscapeMode getQuoteCharacterEscapeMode() {
        return quoteCharacterEscapeMode;
    }

    /**
     * Returns true if the last CSV field should end with a delimiter.
     * 
     * @return true, if the last CSV field should end with a delimiter;
     *         otherwise false
     */
    public boolean getUseDelimiterAfterLastField() {
        return this.useDelimiterAfterLastField;
    }

    /**
     * Sets the delimiter to be used to separate the CSV fields.
     * 
     * @param fieldDelimiter the delimiter
     */
    public void setFieldDelimiter(char fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    /**
     * Sets the quote character.
     * 
     * @param quoteCharacter the quote character
     */
    public void setQuoteCharacter(char quoteCharacter) {
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Specifies whether the last CSV field should end with a delimiter or not.
     * 
     * @param useDelimiterAfterLastField true, if the last CSV field should end
     *            with a delimiter; otherwise false.
     */
    public void setUseDelimiterAfterLastField(boolean useDelimiterAfterLastField) {
        this.useDelimiterAfterLastField = useDelimiterAfterLastField;
    }

    /**
     * Sets the escape mode for the quote character.
     * 
     * @param quoteCharacterEscapeMode the escape mode
     */
    public void setQuoteCharacterEscapeMode(EscapeMode quoteCharacterEscapeMode) {
        this.quoteCharacterEscapeMode = quoteCharacterEscapeMode;
    }

}
