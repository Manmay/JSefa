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

package org.jsefa.csv.mapping;

import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.csv.lowlevel.config.QuoteMode;

/**
 * A mapping between a java object type and a simple CSV data type.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvSimpleTypeMapping extends SimpleTypeMapping<String> {

    private final QuoteMode quoteMode;
    
    private final String noValueString;

    /**
     * Constructs a new <code>CsvSimpleTypeMapping</code> from the given arguments.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param simpleTypeConverter the simple type converter
     * @param quoteMode the quote mode
     * @param noValueString the string to use if no value is given
     */
    public CsvSimpleTypeMapping(Class<?> objectType, String dataTypeName, SimpleTypeConverter simpleTypeConverter,
            QuoteMode quoteMode, String noValueString) {
        super(objectType, dataTypeName, simpleTypeConverter);
        this.quoteMode = quoteMode;
        this.noValueString = noValueString;
    }

    /**
     * Returns the quote mode.
     * 
     * @return the quote mode
     */
    public QuoteMode getQuoteMode() {
        return this.quoteMode;
    }
    
    /**
     * Returns the <code>String</code> to use if no value is given.
     * @return the <code>String</code> to use if no value is given
     */
    public String getNoValueString() {
        return this.noValueString;
    }

}
