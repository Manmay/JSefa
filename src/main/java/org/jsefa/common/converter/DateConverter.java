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

import org.jsefa.ConfigurationException;
import org.jsefa.DeserializationException;

/**
 * Converter for <code>Date</code> objects.
 * <p>
 * It is thread-safe (the access to the non-thread-safe {@link SimpleDateFormat}
 * is synchronized).
 * 
 * @author Norman Lahme-Huetig
 */
public final class DateConverter implements SimpleTypeConverter {
    /**
     * The default format which is used when no format is explicitly given.
     */
    public static final String DEFAULT_FORMAT = "dd.MM.yyyy";

    private final SimpleDateFormat dateFormat;

    /**
     * Constructs a <code>DateConverter</code> with the default format:
     * "dd.MM.yyyy".
     */
    public DateConverter() {
        this(DEFAULT_FORMAT);
    }

    /**
     * Constructs a <code>DateConverter</code> with the given format.
     * 
     * @param format the format to use.
     * @throws ConfigurationException if the given format is not valid.
     */
    public DateConverter(String... format) {
        try {
            this.dateFormat = new SimpleDateFormat(format[0]);
        } catch (Exception e) {
            throw new ConfigurationException("Could not create a DateConverter with format " + format, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Object fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return this.dateFormat.parse(value);
        } catch (ParseException e) {
            throw new DeserializationException("Wrong date format: " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized String toString(Object value) {
        if (value == null) {
            return null;
        }
        return this.dateFormat.format((Date) value);
    }

}
