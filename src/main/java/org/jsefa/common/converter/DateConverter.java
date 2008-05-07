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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converter for <code>Date</code> objects.<br>
 * The format consists of one String describing the date format as required by {@link SimpleDateFormat},e. g.
 * ""dd.MM.yyyy".<br>
 * <p>
 * It is thread-safe (the access to the non-thread-safe {@link SimpleDateFormat} is synchronized).
 * 
 * @author Norman Lahme-Huetig
 */
public class DateConverter implements SimpleTypeConverter {
    /**
     * The default format which is used when no format is explicitly given.
     */
    private static final String DEFAULT_FORMAT = "dd.MM.yyyy";

    private final SimpleDateFormat dateFormat;

    /**
     * Constructs a <code>DateConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @return a date converter
     * @throws ConversionException if the given format is not valid.
     */
    public static DateConverter create(SimpleTypeConverterConfiguration configuration) {
        return new DateConverter(configuration);
    }

    /**
     * Constructs a new <code>DateConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * 
     * @param configuration the configuration
     * @throws ConversionException if the given format is not valid.
     */
    protected DateConverter(SimpleTypeConverterConfiguration configuration) {
        String format = getFormat(configuration);
        try {
            this.dateFormat = new SimpleDateFormat(format);
        } catch (Exception e) {
            throw new ConversionException("Could not create a " + this.getClass().getName() + "  with format "
                    + format, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized Date fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return this.dateFormat.parse(value);
        } catch (ParseException e) {
            throw new ConversionException("Wrong date format: " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized String toString(Object value) {
        if (value == null) {
            return null;
        }
        return this.dateFormat.format((Date) value);
    }

    /**
     * Returns the default format which is used when no format is given.
     * 
     * @return the default format.
     */
    protected String getDefaultFormat() {
        return DateConverter.DEFAULT_FORMAT;
    }

    private String getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return getDefaultFormat();
        }
        if (configuration.getFormat().length != 1) {
            throw new ConversionException("The format for a DateConverter must be a single String");
        }
        return configuration.getFormat()[0];
    }

}
