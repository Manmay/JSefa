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

package org.jsefa.common.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for <code>List</code> objects.<br>
 * The format consists of one <code>String</code> representing the delimiter for the list items.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public class SimpleListConverter implements SimpleTypeConverter {

    /**
     * Format <code>String</code> with "," as the list item delimiter.
     */
    private static final String[] DEFAULT_FORMAT = {","};

    private String delimiter;

    private SimpleTypeConverter itemTypeConverter;

    /**
     * Creates a <code>SimpleListConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @return a simple list converter
     * @throws ConversionException if the given format is not valid.
     */
    public static SimpleListConverter create(SimpleTypeConverterConfiguration configuration) {
        return new SimpleListConverter(configuration);
    }

    /**
     * Constructs a new <code>SimpleListConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @throws ConversionException if the given format is not valid.
     */
    protected SimpleListConverter(SimpleTypeConverterConfiguration configuration) {
        this.delimiter = getFormat(configuration)[0];
        this.itemTypeConverter = configuration.getItemTypeConverter();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object fromString(String value) {
        List result = new ArrayList();
        int lastIndex = 0;
        int index = value.indexOf(this.delimiter);
        while (index != -1) {
            result.add(this.itemTypeConverter.fromString(value.substring(lastIndex, index)));
            lastIndex = index + this.delimiter.length();
            index = value.indexOf(this.delimiter, lastIndex);
        }
        result.add(this.itemTypeConverter.fromString(value.substring(lastIndex)));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public String toString(Object value) {
        StringBuilder result = new StringBuilder();
        List list = (List) value;
        for (int i = 0; i < list.size(); i++) {
            result.append(this.itemTypeConverter.toString(list.get(i)));
            if (i < list.size() - 1) {
                result.append(this.delimiter);
            }
        }
        return result.toString();
    }

    /**
     * Returns the default format which is used when no format is given.
     * 
     * @return the default format.
     */
    protected String[] getDefaultFormat() {
        return SimpleListConverter.DEFAULT_FORMAT;
    }

    private String[] getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return getDefaultFormat();
        }
        if (configuration.getFormat().length != 1) {
            throw new ConversionException("The format for a SimpleListConverter must be an array with 1 entry");
        }
        return configuration.getFormat();
    }

}
