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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 * Converter for <code>XMLGregorianCalendar</code> objects.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class XMLGregorianCalendarConverter implements SimpleTypeConverter {
    private final DatatypeFactory factory;

    /**
     * Constructs a <code>XMLGregorianCalendarConverter</code>.
     */
    public XMLGregorianCalendarConverter() {
        try {
            this.factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ConversionException("Could not configure XMLGregorianCalendarConverter");
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return this.factory.newXMLGregorianCalendar(value);
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
