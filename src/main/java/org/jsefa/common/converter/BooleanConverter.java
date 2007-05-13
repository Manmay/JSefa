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

import org.jsefa.ConfigurationException;
import org.jsefa.DeserializationException;

/**
 * Converter for <code>Boolean</code> objects.
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

    private final String trueString;

    private final String falseString;

    /**
     * Constructs a new <code>BooleanConverter</code> with the default format
     * {@link #FORMAT_TRUE_FALSE}.
     */
    public BooleanConverter() {
        this(FORMAT_TRUE_FALSE);
    }

    /**
     * Constructs a new <code>BooleanConverter</code>.
     * 
     * @param format the format consisting of two Strings. The first token is
     *            the literal for true and the second the literal for false.
     * @throws ConfigurationException if the given format is not valid.
     */
    public BooleanConverter(String... format) {
        if (format.length != 2 || format[0] == null || format[1] == null || format[0].equals(format[1])) {
            throw new ConfigurationException("Invalid format: " + format);
        }
        this.trueString = format[0];
        this.falseString = format[1];
    }

    /**
     * {@inheritDoc}
     */
    public Object fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        if (this.trueString.equals(value)) {
            return Boolean.TRUE;
        } else if (this.falseString.equals(value)) {
            return Boolean.FALSE;
        } else {
            throw new DeserializationException("Unknown boolean value: " + value);
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
            return this.trueString;
        } else {
            return this.falseString;
        }
    }
}
