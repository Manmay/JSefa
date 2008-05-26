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
import java.text.FieldPosition;
import java.text.ParseException;
import java.util.Locale;

/**
 * Converter for <code>BigDecimal</code> objects.<br>
 * The format consists of two <code>String</code>s. The first denotes the {@link Locale} and the second is a
 * pattern as used by {@link DecimalFormat}.<br>
 * <p>
 * It is thread-safe (the access to the non-thread-safe {@link DecimalFormat} is synchronized).
 * 
 * @author Norman Lahme-Huetig
 */
public class BigDecimalConverter implements SimpleTypeConverter {
    /**
     * The default format which is used when no format is explicitly given.
     */
    private static final String[] DEFAULT_FORMAT = {"en", "#0.00"};

    private static final FieldPosition FIELD_POSITION = new FieldPosition(0);

    private final DecimalFormat decimalFormat;

    /**
     * Creates a <code>BigDecimalConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @return a big decimal converter
     * @throws ConversionException if the given format is not valid.
     */
    public static BigDecimalConverter create(SimpleTypeConverterConfiguration configuration) {
        return new BigDecimalConverter(configuration);
    }

    /**
     * Constructs a new <code>BigDecimalConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @throws ConversionException if the given format is not valid.
     */
    protected BigDecimalConverter(SimpleTypeConverterConfiguration configuration) {
        String[] format = getFormat(configuration);
        try {
            Locale locale = new Locale(format[0]);
            String pattern = format[1];
            this.decimalFormat = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
            this.decimalFormat.setParseBigDecimal(true);
        } catch (Exception e) {
            throw new ConversionException("Could not create a " + this.getClass().getName() + " with format "
                    + format[0] + ", " + format[1], e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized BigDecimal fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            Object result = this.decimalFormat.parseObject(value);
            if (result instanceof BigDecimal) {
                return (BigDecimal) result;
            } else {
                return new BigDecimal(((Double) result).doubleValue()).setScale(this.decimalFormat
                        .getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
            }
        } catch (ParseException e) {
            throw new ConversionException("Wrong BigDecimal format " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized String toString(Object value) {
        if (value == null) {
            return null;
        }
        return this.decimalFormat.format(value, new StringBuffer(), FIELD_POSITION).toString();
    }

    /**
     * Returns the default format which is used when no format is given.
     * 
     * @return the default format.
     */
    protected String[] getDefaultFormat() {
        return BigDecimalConverter.DEFAULT_FORMAT;
    }

    private String[] getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return getDefaultFormat();
        }
        if (configuration.getFormat().length != 2) {
            throw new ConversionException("The format for a BigDecimalConverter must be an array with 2 entries");
        }
        return configuration.getFormat();
    }

}
