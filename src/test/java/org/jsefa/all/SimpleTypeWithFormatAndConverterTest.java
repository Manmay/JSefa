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

package org.jsefa.all;

import static org.jsefa.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.JSefaTestUtil.FormatType.FLR;
import static org.jsefa.JSefaTestUtil.FormatType.XML;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.common.converter.BigDecimalConverter;
import org.jsefa.common.converter.BooleanConverter;
import org.jsefa.common.converter.EnumConverter;
import org.jsefa.common.converter.XmlDateTimeConverter;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of different simple types
 * with an explicit format and an explicit converter annotation.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class SimpleTypeWithFormatAndConverterTest extends TestCase {
    enum EnumType {
        ENUM_VALUE1, ENUM_VALUE2, ENUM_VALUE3;
    }

    /**
     * Tests Date (CSV).
     */
    public void testDateCSV() {
        DateDTO dto = createDateDTO();
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("2007-02-28T15:21:27.000Z") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Date (FLR).
     */
    public void testDateFLR() {
        DateDTO dto = createDateDTO();
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("2007-02-28T15:21:27.000Z") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Date (XML).
     */
    public void testDateXML() {
        DateDTO dto = createDateDTO();
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("2007-02-28T15:21:27.000Z") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    private DateDTO createDateDTO() {
        DateDTO dto = new DateDTO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(XmlDateTimeConverter.DEFAULT_FORMAT));
        try {
            dto.dateField = dateFormat.parse("28.02.2007:15:21:27");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dto;

    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class DateDTO extends AbstractTestDTO {
        @CsvField(pos = 1, format = "GMT", converterClass = XmlDateTimeConverter.class)
        @FlrField(pos = 1, length = 25, format = "GMT", converterClass = XmlDateTimeConverter.class)
        @XmlElement(format = "GMT", converterClass = XmlDateTimeConverter.class)
        Date dateField;
    }

    /**
     * Tests BigDecimal (CSV).
     */
    public void testBigDecimalCSV() {
        BigDecimalDTO dto = new BigDecimalDTO();
        dto.bigDecimalField = new BigDecimal(32323455.00).setScale(2);
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("32323455.00") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests BigDecimal (FLR).
     */
    public void testBigDecimalFLR() {
        BigDecimalDTO dto = new BigDecimalDTO();
        dto.bigDecimalField = new BigDecimal(32323455.00).setScale(2);
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("32323455.00") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests BigDecimal (XML).
     */
    public void testBigDecimalXML() {
        BigDecimalDTO dto = new BigDecimalDTO();
        dto.bigDecimalField = new BigDecimal(32323455.00).setScale(2);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("32323455.00") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class BigDecimalDTO extends AbstractTestDTO {
        @CsvField(pos = 1, format = {"en", "#0.00"}, converterClass = BigDecimalConverter.class)
        @FlrField(pos = 1, length = 15, format = {"en", "#0.00"}, converterClass = BigDecimalConverter.class)
        @XmlElement(format = {"en", "#0.00"}, converterClass = BigDecimalConverter.class)
        BigDecimal bigDecimalField;
    }

    /**
     * Tests Enum (CSV).
     */
    public void testEnumCSV() {
        EnumDTO dto = new EnumDTO();
        dto.enumField = EnumType.ENUM_VALUE3;
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("enum value 3") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Enum (FLR).
     */
    public void testEnumFLR() {
        EnumDTO dto = new EnumDTO();
        dto.enumField = EnumType.ENUM_VALUE3;
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("enum value 3") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Enum (XML).
     */
    public void testEnumXML() {
        EnumDTO dto = new EnumDTO();
        dto.enumField = EnumType.ENUM_VALUE3;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("enum value 3") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class EnumDTO extends AbstractTestDTO {
        @CsvField(pos = 1, format = {"ENUM_VALUE1=enum value 1", "ENUM_VALUE2=enum value 2",
                "ENUM_VALUE3=enum value 3"}, converterClass = EnumConverter.class)
        @FlrField(pos = 1, length = 15, format = {"ENUM_VALUE1=enum value 1", "ENUM_VALUE2=enum value 2",
                "ENUM_VALUE3=enum value 3"}, converterClass = EnumConverter.class)
        @XmlElement(format = {"ENUM_VALUE1=enum value 1", "ENUM_VALUE2=enum value 2", "ENUM_VALUE3=enum value 3"}, 
                converterClass = EnumConverter.class)
        EnumType enumField;
    }

    /**
     * Tests Boolean (CSV).
     */
    public void testBooleanCSV() {
        BooleanDTO dto = new BooleanDTO();
        dto.booleanField = new Boolean(false);
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("0") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Boolean (FLR).
     */
    public void testBooleanFLR() {
        BooleanDTO dto = new BooleanDTO();
        dto.booleanField = new Boolean(false);
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("0") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Boolean (XML).
     */
    public void testBooleanXML() {
        BooleanDTO dto = new BooleanDTO();
        dto.booleanField = new Boolean(false);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("0") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class BooleanDTO extends AbstractTestDTO {
        @CsvField(pos = 1, format = {"1", "0"}, converterClass = BooleanConverter.class)
        @FlrField(pos = 1, length = 15, format = {"1", "0"}, converterClass = BooleanConverter.class)
        @XmlElement(format = {"1", "0"}, converterClass = BooleanConverter.class)
        Boolean booleanField;
    }
}
