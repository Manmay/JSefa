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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import org.jsefa.ConfigurationException;
import org.jsefa.DeserializationException;

/**
 * Converter for <code>BigDecimal</code> objects.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class BigDecimalConverter implements SimpleTypeConverter {
    /**
     * The default format which is used when no format is explicitly given.
     */
    public static final String[] DEFAULT_FORMAT = {"en", "#0.00"};

    private final DecimalFormat decimalFormat;

    private final int scale;

    /**
     * Constructs a <code>BigDecimalConverter</code> with the default format
     * {@link #DEFAULT_FORMAT}.
     */
    public BigDecimalConverter() {
        this(DEFAULT_FORMAT);
    }

    /**
     * Constructs a <code>BigDecimalConverter</code> with the given format.
     * The format is an array with two <code>String</code>s. The first
     * denotes the {@link Locale} and the second is a pattern as used by
     * {@link DecimalFormat}. The scale is extracted from the pattern.
     * 
     * @param format the format to use.
     * @throws ConfigurationException if the given format is not valid.
     */
    public BigDecimalConverter(String... format) {
        try {
            Locale locale = extractLocale(format[0]);
            String pattern = format[1];
            this.decimalFormat = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
            this.scale = extractScale(pattern);
        } catch (Exception e) {
            throw new ConfigurationException("Could not create a BigDecimalConverter with format " + format, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return new BigDecimal(this.decimalFormat.parse(value).doubleValue()).setScale(this.scale,
                    BigDecimal.ROUND_HALF_UP);
        } catch (ParseException e) {
            throw new DeserializationException("Wrong BigDecimal format " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        return this.decimalFormat.format(((BigDecimal) value).doubleValue());
    }

    private Locale extractLocale(String format) {
        return new Locale(format);
    }

    private int extractScale(String pattern) {
        int detectedScale = 0;
        int pos = pattern.indexOf('.') + 1;
        if (pos != -1) {
            while (pos < pattern.length()) {
                if (pattern.charAt(pos) == '0') {
                    detectedScale++;
                } else {
                    break;
                }
                pos++;
            }
        }
        return detectedScale;
    }

}
