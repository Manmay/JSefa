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

/**
 * Converter for <code>Boolean</code> objects.<br>
 * The format consists of two Strings. The first token is the literal for true
 * and the second the literal for false.<br>
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class BooleanConverter implements SimpleTypeConverter {
    /**
     * Format <code>String</code> with "true" for <code>true</code> and
     * "false" for <code>false</code>.
     */
    public static final String[] FORMAT_TRUE_FALSE = {"true", "false"};

    /**
     * Format <code>String</code> with "yes" for <code>true</code> and "no"
     * for <code>false</code>.
     */
    public static final String[] FORMAT_YES_NO = {"yes", "no"};

    /**
     * Format <code>String</code> with "1" for <code>true</code> and "0" for
     * <code>false</code>.
     */
    public static final String[] FORMAT_BINARY = {"1", "0"};

    private final String trueLiteral;

    private final String falseLiteral;

    /**
     * Constructs a new <code>BooleanConverter</code>.<br>
     * If no format is given, the {@link #FORMAT_TRUE_FALSE} is used.
     * 
     * @param configuration the configuration.
     * @return a boolean converter
     * @throws ConversionException if the given format is not valid.
     */
    public static BooleanConverter create(SimpleTypeConverterConfiguration configuration) {
        String[] format = getFormat(configuration);
        return new BooleanConverter(format[0], format[1]);
    }

    private static String[] getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return FORMAT_TRUE_FALSE;
        }
        if (configuration.getFormat().length != 2) {
            throw new ConversionException("The format for a BooleanConverter must be an array with 2 entries");
        }
        if (configuration.getFormat()[0].equals(configuration.getFormat()[1])) {
            throw new ConversionException("Invalid format for a BooleanConverter: " + configuration.getFormat()[0]
                    + ", " + configuration.getFormat()[1]);
        }
        return configuration.getFormat();
    }

    private BooleanConverter(String trueLiteral, String falseLiteral) {
        this.trueLiteral = trueLiteral;
        this.falseLiteral = falseLiteral;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        if (this.trueLiteral.equals(value)) {
            return Boolean.TRUE;
        } else if (this.falseLiteral.equals(value)) {
            return Boolean.FALSE;
        } else {
            throw new ConversionException("Unknown boolean value: " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        if (((Boolean) value).booleanValue()) {
            return this.trueLiteral;
        } else {
            return this.falseLiteral;
        }
    }
}
