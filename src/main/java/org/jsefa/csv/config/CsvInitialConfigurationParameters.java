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

import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.InitialConfigurationParameters;
import org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters;

/**
 * A collection of initial configuration parameters for CSV.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public interface CsvInitialConfigurationParameters extends InitialConfigurationParameters {
    /**
     * Configuration parameter for the CsvIOFactory class to use.
     */
    String IO_FACTORY_CLASS = "jsefa:csv:ioFactoryClass";

    /**
     * Configuration parameter for the default quote mode to use.
     */
    String DEFAUT_QUOTE_MODE = "jsefa:csv:defaultQuoteMode";
    
    /**
     * Configuration parameter for the default no value string to use.
     */
    String DEFAULT_NO_VALUE_STRING = "jsefa:csv:defaultNoValueString";

    /**
     * Configuration parameter for the CsvLowLevelIOFactory class to use.
     */
    String LOW_LEVEL_IO_FACTORY_CLASS = CsvLowLevelInitialConfigurationParameters.LOW_LEVEL_IO_FACTORY_CLASS;

    /**
     * Configuration parameter for the field delimiter to use.
     */
    String FIELD_DELIMITER = CsvLowLevelInitialConfigurationParameters.FIELD_DELIMITER;

    /**
     * Configuration parameter for the quote character to use.
     */
    String QUOTE_CHARACTER = CsvLowLevelInitialConfigurationParameters.QUOTE_CHARACTER;

    /**
     * Configuration parameter for the quote character escape mode to use.
     */
    String QUOTE_CHARACTER_ESCAPE_MODE = CsvLowLevelInitialConfigurationParameters.QUOTE_CHARACTER_ESCAPE_MODE;

    /**
     * Configuration parameter for the flag 'useDelimiterAfterLastField'.
     */
    String USE_DELIMITER_AFTER_LAST_FIELD = CsvLowLevelInitialConfigurationParameters.USE_DELIMITER_AFTER_LAST_FIELD;

    /**
     * Configuration parameter for the escape character to use.
     */
    String ESCAPE_CHARACTER = CsvLowLevelInitialConfigurationParameters.ESCAPE_CHARACTER;
}
