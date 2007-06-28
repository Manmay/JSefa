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

import org.jsefa.csv.lowlevel.CsvLowLevelConstants;
import org.jsefa.csv.lowlevel.EscapeMode;
import org.jsefa.csv.lowlevel.QuoteMode;

/**
 * A collection of constants concerning the CSV format type.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface CsvConstants {
    /**
     * Character for escaping.
     * @see CsvLowLevelConstants#ESCAPE_CHARACTER
     */
    char ESCAPE_CHARACTER = CsvLowLevelConstants.ESCAPE_CHARACTER;

    /**
     * The default field delimiter character used if none is explicitly given.
     * 
     * @see CsvLowLevelConstants#DEFAULT_FIELD_DELIMITER
     */
    char DEFAULT_FIELD_DELIMITER = CsvLowLevelConstants.DEFAULT_FIELD_DELIMITER;

    /**
     * The default quote character used if none is explicitly given.
     * @see CsvLowLevelConstants#DEFAULT_QUOTE_CHARACTER
     */
    char DEFAULT_QUOTE_CHARACTER = CsvLowLevelConstants.DEFAULT_QUOTE_CHARACTER;

    /**
     * The default line break used if none is explicitly given.
     * @see CsvLowLevelConstants#DEFAULT_LINE_BREAK
     */
    String DEFAULT_LINE_BREAK = CsvLowLevelConstants.DEFAULT_LINE_BREAK;

    /**
     * The default quote character escape mode used if none is explicitly given.
     * @see CsvLowLevelConstants#DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE
     */
    EscapeMode DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE = CsvLowLevelConstants.DEFAULT_QUOTE_CHARACTER_ESCAPE_MODE;

    /**
     * The default quote mode used if none is explicitly given.
     */
    QuoteMode DEFAULT_QUOTE_MODE = QuoteMode.NEVER;

}
