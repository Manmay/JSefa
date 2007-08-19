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

/**
 * Converter for <code>BigDecimal</code> objects.<br>
 * The format consists of two <code>String</code>s. The first denotes the
 * {@link Locale} and the second is a pattern as used by {@link DecimalFormat}.<br>
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

    /**
     * Constructs a <code>BigDecimalConverter</code>.<br>
     * If no format is given, the {@link #DEFAULT_FORMAT} is used.
     * 
     * @param configuration the configuration
     * @return a big decimal converter
     * @throws ConversionException if the given format is not valid.
     */
    public static BigDecimalConverter create(SimpleTypeConverterConfiguration configuration) {
        String[] format = getFormat(configuration);
        try {
            Locale locale = new Locale(format[0]);
            String pattern = format[1];
            return new BigDecimalConverter(new DecimalFormat(pattern, new DecimalFormatSymbols(locale)));
        } catch (Exception e) {
            throw new ConversionException("Could not create a BigDecimalConverter with format " + format[0] + ", "
                    + format[1], e);
        }
    }

    private static String[] getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return DEFAULT_FORMAT;
        }
        if (configuration.getFormat().length != 2) {
            throw new ConversionException("The format for a BigDecimalConverter must be an array with 2 entries");
        }
        return configuration.getFormat();
    }

    private BigDecimalConverter(DecimalFormat decimalFormat) {
        this.decimalFormat = decimalFormat;
    }

    /**
     * {@inheritDoc}
     */
    public BigDecimal fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return new BigDecimal(this.decimalFormat.parse(value).doubleValue()).setScale(this.decimalFormat
                    .getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
        } catch (ParseException e) {
            throw new ConversionException("Wrong BigDecimal format " + value);
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

}
