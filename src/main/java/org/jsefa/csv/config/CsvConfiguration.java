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

import static org.jsefa.csv.config.CsvInitialConfigurationParameters.DEFAULT_NO_VALUE_STRING;
import static org.jsefa.csv.config.CsvInitialConfigurationParameters.DEFAUT_QUOTE_MODE;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.util.GeneralConstants;
import org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration;
import org.jsefa.csv.lowlevel.config.EscapeMode;
import org.jsefa.csv.lowlevel.config.QuoteMode;
import org.jsefa.rbf.config.RbfConfiguration;

/**
 * A configuration object used when creating a CSV IO factory. It uses lazy initialization.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvConfiguration extends RbfConfiguration<CsvLowLevelConfiguration> {

    private QuoteMode defaultQuoteMode;

    private String defaultNoValueString;

    /**
     * Constructs a new <code>CsvConfiguration</code>.
     */
    public CsvConfiguration() {
    }

    private CsvConfiguration(CsvConfiguration other) {
        super(other);
        setDefaultQuoteMode(other.getDefaultQuoteMode());
        setDefaultNoValueString(other.getDefaultNoValueString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CsvConfiguration createCopy() {
        return new CsvConfiguration(this);
    }

    /**
     * Returns the default quote mode.
     * 
     * @return a quote mode
     */
    public QuoteMode getDefaultQuoteMode() {
        if (this.defaultQuoteMode == null) {
            this.defaultQuoteMode = InitialConfiguration.get(DEFAUT_QUOTE_MODE, Defaults.DEFAULT_QUOTE_MODE);
        }
        return this.defaultQuoteMode;
    }

    /**
     * Returns the default no value string.
     * 
     * @return a no value string
     */
    public String getDefaultNoValueString() {
        if (this.defaultNoValueString == null) {
            this.defaultNoValueString = InitialConfiguration.get(DEFAULT_NO_VALUE_STRING,
                    Defaults.DEFAULT_NO_VALUE_STRING);
        }
        return this.defaultNoValueString;
    }

    /**
     * Sets the default quote mode.
     * 
     * @param defaultQuoteMode a quote mode
     */
    public void setDefaultQuoteMode(QuoteMode defaultQuoteMode) {
        if (defaultQuoteMode.equals(QuoteMode.DEFAULT)) {
            this.defaultQuoteMode = InitialConfiguration.get(DEFAUT_QUOTE_MODE, Defaults.DEFAULT_QUOTE_MODE);
        } else {
            this.defaultQuoteMode = defaultQuoteMode;
        }
    }

    /**
     * Sets the default no value string.
     * 
     * @param defaultNoValueString a no value string
     */
    public void setDefaultNoValueString(String defaultNoValueString) {
        if (defaultNoValueString.equals(GeneralConstants.DEFAULT_STRING)) {
            this.defaultNoValueString = InitialConfiguration.get(DEFAULT_NO_VALUE_STRING,
                    Defaults.DEFAULT_NO_VALUE_STRING);
        } else {
            this.defaultNoValueString = defaultNoValueString;
        }
    }

    /**
     * Returns the delimiter used to separate the CSV fields.
     * 
     * @return the delimiter
     * @see CsvLowLevelConfiguration#getFieldDelimiter
     */
    public char getFieldDelimiter() {
        return getLowLevelConfiguration().getFieldDelimiter();
    }

    /**
     * Returns the quote used to surround a field.
     * 
     * @return the quote character
     * @see CsvLowLevelConfiguration#getQuoteCharacter
     */
    public char getQuoteCharacter() {
        return getLowLevelConfiguration().getQuoteCharacter();
    }

    /**
     * Returns the escape mode for the quote character.
     * 
     * @return an escape mode
     * @see CsvLowLevelConfiguration#getQuoteCharacterEscapeMode
     */
    public EscapeMode getQuoteCharacterEscapeMode() {
        return getLowLevelConfiguration().getQuoteCharacterEscapeMode();
    }

    /**
     * Returns true if the last CSV field should end with a delimiter.
     * 
     * @return true, if the last CSV field should end with a delimiter; otherwise false
     * @see CsvLowLevelConfiguration#getUseDelimiterAfterLastField
     */
    public boolean getUseDelimiterAfterLastField() {
        return getLowLevelConfiguration().getUseDelimiterAfterLastField();
    }

    /**
     * Returns the line break <code>String</code>.
     * 
     * @return the line break <code>String</code>
     * @see CsvLowLevelConfiguration#getLineBreak
     */
    public String getLineBreak() {
        return getLowLevelConfiguration().getLineBreak();
    }

    /**
     * Sets the delimiter to be used to separate the CSV fields.
     * 
     * @param fieldDelimiter the delimiter
     * @see CsvLowLevelConfiguration#setFieldDelimiter
     */
    public void setFieldDelimiter(char fieldDelimiter) {
        getLowLevelConfiguration().setFieldDelimiter(fieldDelimiter);
    }

    /**
     * Sets the quote character.
     * 
     * @param quoteCharacter the quote character
     * @see CsvLowLevelConfiguration#setQuoteCharacter
     */
    public void setQuoteCharacter(char quoteCharacter) {
        getLowLevelConfiguration().setQuoteCharacter(quoteCharacter);
    }

    /**
     * Specifies whether the last CSV field should end with a delimiter or not.
     * 
     * @param useDelimiterAfterLastField true, if the last CSV field should end with a delimiter; otherwise false.
     * @see CsvLowLevelConfiguration#setUseDelimiterAfterLastField
     */
    public void setUseDelimiterAfterLastField(boolean useDelimiterAfterLastField) {
        getLowLevelConfiguration().setUseDelimiterAfterLastField(useDelimiterAfterLastField);
    }

    /**
     * Sets the escape mode for the quote character.
     * 
     * @param quoteCharacterEscapeMode the escape mode
     * @see CsvLowLevelConfiguration#setQuoteCharacterEscapeMode
     */
    public void setQuoteCharacterEscapeMode(EscapeMode quoteCharacterEscapeMode) {
        getLowLevelConfiguration().setQuoteCharacterEscapeMode(quoteCharacterEscapeMode);
    }

    /**
     * Sets the line break <code>String</code>.
     * 
     * @param lineBreak the line break <code>String</code>
     * @see CsvLowLevelConfiguration#setLineBreak
     */
    public void setLineBreak(String lineBreak) {
        getLowLevelConfiguration().setLineBreak(lineBreak);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CsvLowLevelConfiguration createDefaultLowLevelConfiguration() {
        return new CsvLowLevelConfiguration();
    }

    /**
     * Set of default configuration values.
     * 
     * @author Norman Lahme-Huetig
     */
    public interface Defaults {
        /**
         * The default quote mode used if none is explicitly given.
         */
        QuoteMode DEFAULT_QUOTE_MODE = QuoteMode.ON_DEMAND;

        /**
         * The default no value string used if none is explicitly given.
         */
        String DEFAULT_NO_VALUE_STRING = "";
    }

}
