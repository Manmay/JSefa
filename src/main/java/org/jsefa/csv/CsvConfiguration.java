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

import org.jsefa.Configuration;
import org.jsefa.csv.lowlevel.CsvLowLevelConfiguration;
import org.jsefa.csv.lowlevel.CsvLowLevelIOFactory;
import org.jsefa.csv.lowlevel.CsvLowLevelIOFactoryImpl;

/**
 * A configuration object used when creating a {@link CsvIOFactory}. It uses
 * lazy initialization for the low level configuration.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvConfiguration extends Configuration {

    private QuoteMode defaultQuoteMode;

    private CsvLowLevelConfiguration lowLevelConfig;

    private CsvLowLevelIOFactory lowLevelIOFactory;

    /**
     * Constructs a new <code>CsvConfiguration</code>.
     */
    public CsvConfiguration() {
        setDefaultQuoteMode(CsvConstants.DEFAULT_QUOTE_MODE);
        setLowLevelIOFactory(CsvLowLevelIOFactoryImpl.getInstance());
    }

    private CsvConfiguration(CsvConfiguration other) {
        setDefaultQuoteMode(other.getDefaultQuoteMode());
        setLowLevelConfiguration(other.getLowLevelConfiguration().createCopy());
        setLowLevelIOFactory(other.getLowLevelIOFactory());
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
        return this.defaultQuoteMode;
    }

    /**
     * Returns the low level configuration object.
     * 
     * @return the low level configuration object
     */
    public CsvLowLevelConfiguration getLowLevelConfiguration() {
        if (this.lowLevelConfig == null) {
            this.lowLevelConfig = new CsvLowLevelConfiguration();
        }
        return this.lowLevelConfig;
    }

    /**
     * Returns the CSV low level IO factory.
     * 
     * @return the CSV low level IO factory
     */
    public CsvLowLevelIOFactory getLowLevelIOFactory() {
        return this.lowLevelIOFactory;
    }

    /**
     * Sets the default quote mode. It must be different from
     * {@link QuoteMode#DEFAULT}.
     * 
     * @param defaultQuoteMode a quote mode
     */
    public void setDefaultQuoteMode(QuoteMode defaultQuoteMode) {
        if (!defaultQuoteMode.equals(QuoteMode.DEFAULT)) {
            this.defaultQuoteMode = defaultQuoteMode;
        }
    }

    /**
     * Sets the CSV low level configuration object.
     * 
     * @param lowLevelConfig the CSV low level configuration object.
     */
    public void setLowLevelConfiguration(CsvLowLevelConfiguration lowLevelConfig) {
        this.lowLevelConfig = lowLevelConfig;
    }

    /**
     * Sets the CSV low level IO factory.
     * 
     * @param lowLevelIOFactory the CSV low level IO factory
     */
    public void setLowLevelIOFactory(CsvLowLevelIOFactory lowLevelIOFactory) {
        this.lowLevelIOFactory = lowLevelIOFactory;
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
     * @return true, if the last CSV field should end with a delimiter;
     *         otherwise false
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
     * @param useDelimiterAfterLastField true, if the last CSV field should end
     *            with a delimiter; otherwise false.
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

}
