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

import org.jsefa.common.config.Configuration;
import org.jsefa.common.lowlevel.config.LowLevelInitialConfigurationParameters;

/**
 * A collection of initial configuration parameters for low level CSV.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public interface CsvLowLevelInitialConfigurationParameters extends LowLevelInitialConfigurationParameters {

    /**
     * Configuration parameter for the CsvLowLevelIOFactory class to use.
     */
    String LOW_LEVEL_IO_FACTORY_CLASS = "jsefa:csv:lowlevel:ioFactoryClass";

    /**
     * Configuration parameter for the field delimiter to use.
     */
    String FIELD_DELIMITER = "jsefa:csv:lowlevel:fieldDelimiter";

    /**
     * Configuration parameter for the quote character to use.
     */
    String QUOTE_CHARACTER = "jsefa:csv:lowlevel:quoteCharacter";

    /**
     * Configuration parameter for the quote character escape mode to use.
     */
    String QUOTE_CHARACTER_ESCAPE_MODE = "jsefa:csv:lowlevel:quoteCharacterEscapeMode";

    /**
     * Configuration parameter for the flag 'useDelimiterAfterLastField'.
     */
    String USE_DELIMITER_AFTER_LAST_FIELD = "jsefa:csv:lowlevel:useDelimiterAfterLastField";

    /**
     * Configuration parameter for the escape character to use.
     */
    String ESCAPE_CHARACTER = "jsefa:csv:lowlevel:escapeCharacter";

}
