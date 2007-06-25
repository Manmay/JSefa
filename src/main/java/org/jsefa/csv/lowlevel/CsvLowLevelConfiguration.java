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

import org.jsefa.csv.CsvConstants;
import org.jsefa.csv.EscapeMode;

/**
 * Configuration object for creating a {@link CsvLowLevelSerializer} or
 * {@link CsvLowLevelDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvLowLevelConfiguration {
    private char fieldDelimiter;

    private char quoteCharacter;

    private EscapeMode quoteCharacterEscapeMode;

    private boolean useDelimiterAfterLastField;

    private String lineBreak;

    /**
     * Constructs a new <code>CsvLowLevelConfiguration</code>.
     */
    public CsvLowLevelConfiguration() {
        setFieldDelimiter(CsvConstants.DEFAULT_FIELD_DELIMITER);
        setQuoteCharacter(CsvConstants.DEFAULT_QUOTE_CHARACTER);
        setQuoteCharacterEscapeMode(CsvConstants.DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE);
        setUseDelimiterAfterLastField(false);
        setLineBreak(CsvConstants.DEFAULT_LINE_BREAK);
    }

    private CsvLowLevelConfiguration(CsvLowLevelConfiguration other) {
        setFieldDelimiter(other.getFieldDelimiter());
        setUseDelimiterAfterLastField(other.getUseDelimiterAfterLastField());
        setQuoteCharacter(other.getQuoteCharacter());
        setQuoteCharacterEscapeMode(other.getQuoteCharacterEscapeMode());
        setLineBreak(other.getLineBreak());
    }

    /**
     * {@inheritDoc}
     */
    public CsvLowLevelConfiguration createCopy() {
        return new CsvLowLevelConfiguration(this);
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
     * @return an escape mode
     */
    public EscapeMode getQuoteCharacterEscapeMode() {
        return this.quoteCharacterEscapeMode;
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
     * Returns the line break <code>String</code>.
     * 
     * @return the line break <code>String</code>
     */
    public String getLineBreak() {
        return this.lineBreak;
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

    /**
     * Sets the line break <code>String</code>.
     * 
     * @param lineBreak the line break <code>String</code>
     */
    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

}
