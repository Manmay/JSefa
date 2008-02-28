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

package org.jsefa.csv.lowlevel.config;

import static org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration.Defaults.DEFAULT_ESCAPE_CHARACTER;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration.Defaults.DEFAULT_FIELD_DELIMITER;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration.Defaults.DEFAULT_QUOTE_CHARACTER;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration.Defaults.DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration.Defaults.DEFAULT_USE_DELIMITER_AFTER_LAST_FIELD;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters.ESCAPE_CHARACTER;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters.FIELD_DELIMITER;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters.QUOTE_CHARACTER;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters.QUOTE_CHARACTER_ESCAPE_MODE;
import static org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters.USE_DELIMITER_AFTER_LAST_FIELD;

import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.csv.lowlevel.CsvLowLevelDeserializer;
import org.jsefa.csv.lowlevel.CsvLowLevelSerializer;
import org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration;

/**
 * Configuration object for creating a {@link CsvLowLevelSerializer} or {@link CsvLowLevelDeserializer}. It uses
 * lazy initialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvLowLevelConfiguration extends RbfLowLevelConfiguration {
    private Character fieldDelimiter;

    private Character quoteCharacter;

    private EscapeMode quoteCharacterEscapeMode;

    private Boolean useDelimiterAfterLastField;

    private Character escapeCharacter;

    /**
     * Constructs a new <code>CsvLowLevelConfiguration</code>.
     */
    public CsvLowLevelConfiguration() {
    }

    private CsvLowLevelConfiguration(CsvLowLevelConfiguration other) {
        super(other);
        setFieldDelimiter(other.getFieldDelimiter());
        setUseDelimiterAfterLastField(other.getUseDelimiterAfterLastField());
        setQuoteCharacter(other.getQuoteCharacter());
        setQuoteCharacterEscapeMode(other.getQuoteCharacterEscapeMode());
        setEscapeCharacter(other.getEscapeCharacter());
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
        if (this.fieldDelimiter == null) {
            this.fieldDelimiter = InitialConfiguration.get(FIELD_DELIMITER, DEFAULT_FIELD_DELIMITER);
        }
        return this.fieldDelimiter;
    }

    /**
     * Returns the quote used to surround a field.
     * 
     * @return the quote character
     */
    public char getQuoteCharacter() {
        if (this.quoteCharacter == null) {
            this.quoteCharacter = InitialConfiguration.get(QUOTE_CHARACTER, DEFAULT_QUOTE_CHARACTER);
        }
        return this.quoteCharacter;
    }

    /**
     * Returns the escape mode for the quote character.
     * 
     * @return an escape mode
     */
    public EscapeMode getQuoteCharacterEscapeMode() {
        if (this.quoteCharacterEscapeMode == null) {
            this.quoteCharacterEscapeMode = InitialConfiguration.get(QUOTE_CHARACTER_ESCAPE_MODE,
                    DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE);
        }
        return this.quoteCharacterEscapeMode;
    }

    /**
     * Returns true if the last CSV field should end with a delimiter.
     * 
     * @return true, if the last CSV field should end with a delimiter; otherwise false
     */
    public boolean getUseDelimiterAfterLastField() {
        if (this.useDelimiterAfterLastField == null) {
            this.useDelimiterAfterLastField = InitialConfiguration.get(USE_DELIMITER_AFTER_LAST_FIELD,
                    DEFAULT_USE_DELIMITER_AFTER_LAST_FIELD);
        }
        return this.useDelimiterAfterLastField;
    }

    /**
     * Returns the escape character.
     * 
     * @return the escape character.
     */
    public char getEscapeCharacter() {
        if (this.escapeCharacter == null) {
            this.escapeCharacter = InitialConfiguration.get(ESCAPE_CHARACTER, DEFAULT_ESCAPE_CHARACTER);
        }
        return this.escapeCharacter;
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
     * Sets the escape character.
     * 
     * @param escapeCharacter the escape character
     */
    public void setEscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    /**
     * Specifies whether the last CSV field should end with a delimiter or not.
     * 
     * @param useDelimiterAfterLastField true, if the last CSV field should end with a delimiter; otherwise false.
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
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {

        /**
         * The default character for escaping.
         */
        char DEFAULT_ESCAPE_CHARACTER = '\\';

        /**
         * The default field delimiter character used if none is explicitly given.
         */
        char DEFAULT_FIELD_DELIMITER = ';';

        /**
         * The default quote character used if none is explicitly given.
         */
        char DEFAULT_QUOTE_CHARACTER = '\"';

        /**
         * The default quote character escape mode used if none is explicitly given.
         */
        EscapeMode DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE = EscapeMode.DOUBLING;

        /**
         * The default value whether to use a delimiter after the line or not if none is explicitly given.
         */
        boolean DEFAULT_USE_DELIMITER_AFTER_LAST_FIELD = false;

    }

}
