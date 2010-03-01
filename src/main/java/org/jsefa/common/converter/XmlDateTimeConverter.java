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

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Converter for <code>XmlDateTime</code> objects.<br>
 * The format is a single String describing the time zone, e. g. "GMT".
 * <p>
 * It is thread safe.
 * 
 * @author Norman Lahme-Huetig
 */
public class XmlDateTimeConverter implements SimpleTypeConverter {
    /**
     * The default format which is used when no format is explicitly given.
     */
    public static final String DEFAULT_FORMAT = "GMT";

    private static final Date PURE_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);

    private final DatatypeFactory factory;

    private final TimeZone timeZone;

    /**
     * Constructs a <code>XmlDateTimeConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @return a xml date time converter
     */
    public static XmlDateTimeConverter create(SimpleTypeConverterConfiguration configuration) {
        return new XmlDateTimeConverter(configuration);
    }

    /**
     * Constructs a <code>XmlDateTimeConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     */
    protected XmlDateTimeConverter(SimpleTypeConverterConfiguration configuration) {
        this.timeZone = TimeZone.getTimeZone(getFormat(configuration));
        try {
            this.factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ConversionException("Could not create an " + this.getClass().getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Date fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return toGregorianCalendar(this.factory.newXMLGregorianCalendar(value)).getTime();
        } catch (Exception e) {
            throw new ConversionException("Wrong date format: " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final String toString(Object value) {
        if (value == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar(this.timeZone);
        cal.setTime((Date) value);
        return this.factory.newXMLGregorianCalendar(cal).toString();
    }

    /**
     * Returns the default format which is used when no format is given.
     * 
     * @return the default format.
     */
    protected String getDefaultFormat() {
        return XmlDateTimeConverter.DEFAULT_FORMAT;
    }

    private String getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return getDefaultFormat();
        }
        if (configuration.getFormat().length != 1) {
            throw new ConversionException("The format for an XmlDateTimeConverter must be a single String");
        }
        return configuration.getFormat()[0];
    }

    /**
     * Creates a new <code>GregorianCalendar</code> from the given <code>XMLGregorianCalendar</code>. The
     * implementation is based on {@link XMLGregorianCalendar#toGregorianCalendar} and improved for better
     * performance (using caching for time zones).
     * 
     * @param cal the <code>XMLGregorianCalendar</code> to convert
     * @return the <code>GregorianCalendar</code>
     */
    private GregorianCalendar toGregorianCalendar(XMLGregorianCalendar cal) {
        TimeZone tz = TimeZoneProvider.getTimeZone(cal);
        Locale locale = java.util.Locale.getDefault();

        GregorianCalendar result = new GregorianCalendar(tz, locale);
        result.clear();
        result.setGregorianChange(PURE_GREGORIAN_CHANGE);

        BigInteger year = cal.getEonAndYear();
        if (year != null) {
            if (year.signum() == -1) {
                result.set(Calendar.ERA, GregorianCalendar.BC);
            } else {
                result.set(Calendar.ERA, GregorianCalendar.AD);

            }
            result.set(Calendar.YEAR, year.abs().intValue());
        }

        if (cal.getMonth() != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.MONTH, cal.getMonth() - 1);
        }

        if (cal.getDay() != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.DAY_OF_MONTH, cal.getDay());
        }

        if (cal.getHour() != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.HOUR_OF_DAY, cal.getHour());
        }

        if (cal.getMinute() != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.MINUTE, cal.getMinute());
        }

        if (cal.getSecond() != DatatypeConstants.FIELD_UNDEFINED) {
            result.set(Calendar.SECOND, cal.getSecond());
        }

        if (cal.getFractionalSecond() != null) {
            result.set(Calendar.MILLISECOND, cal.getMillisecond());
        }

        return result;
    }

    /**
     * Provider for <code>TimeZone</code>s which caches the objects for better performance.
     * <p>
     * It is thread-safe.
     * 
     * @author Norman Lahme-Huetig
     * 
     */
    private static final class TimeZoneProvider {
        private static final ConcurrentMap<Integer, TimeZone> TIME_ZONES = new ConcurrentHashMap<Integer, TimeZone>();

        public static TimeZone getTimeZone(XMLGregorianCalendar gregCal) {
            int timeZoneInt = gregCal.getTimezone();
            TimeZone timeZone = TIME_ZONES.get(timeZoneInt);
            if (timeZone == null) {
                synchronized (TimeZoneProvider.class) {
                    timeZone = gregCal.getTimeZone(DatatypeConstants.FIELD_UNDEFINED);
                    TIME_ZONES.put(timeZoneInt, timeZone);
                }
            }
            return timeZone;
        }

        private TimeZoneProvider() {
        }

    }

}
