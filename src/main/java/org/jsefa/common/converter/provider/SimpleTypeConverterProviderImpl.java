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

package org.jsefa.common.converter.provider;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jsefa.common.converter.BigDecimalConverter;
import org.jsefa.common.converter.BooleanConverter;
import org.jsefa.common.converter.DateConverter;
import org.jsefa.common.converter.EnumConverter;
import org.jsefa.common.converter.IntegerConverter;
import org.jsefa.common.converter.LongConverter;
import org.jsefa.common.converter.StringConverter;
import org.jsefa.common.converter.XMLGregorianCalendarConverter;

/**
 * Default implementation of {@link SimpleTypeConverterProvider}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class SimpleTypeConverterProviderImpl extends SimpleTypeConverterProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerStandardConverterClasses() {
        registerConverterClass(String.class, StringConverter.class);
        registerConverterClass(boolean.class, BooleanConverter.class);
        registerConverterClass(Boolean.class, BooleanConverter.class);
        registerConverterClass(long.class, LongConverter.class);
        registerConverterClass(Long.class, LongConverter.class);
        registerConverterClass(int.class, IntegerConverter.class);
        registerConverterClass(Integer.class, IntegerConverter.class);
        registerConverterClass(BigDecimal.class, BigDecimalConverter.class);
        registerConverterClass(Date.class, DateConverter.class);
        registerConverterClass(XMLGregorianCalendar.class, XMLGregorianCalendarConverter.class);
        registerConverterClass(Enum.class, EnumConverter.class);
    }

}
