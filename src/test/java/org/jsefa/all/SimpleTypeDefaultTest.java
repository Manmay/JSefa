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

import static org.jsefa.JSefaTestUtil.FormatType.*;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.common.converter.DateConverter;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of different simple types
 * with no explicit format and converter annotation.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class SimpleTypeDefaultTest extends TestCase {

    enum EnumType {
        ENUM_VALUE1, ENUM_VALUE2, ENUM_VALUE3;
    }

    /**
     * Tests String (CSV).
     */
    public void testStringCSV() {
        StringDTO dto = new StringDTO();
        dto.stringField = "stringValue";
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("stringValue") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests String (FLR).
     */
    public void testStringFLR() {
        StringDTO dto = new StringDTO();
        dto.stringField = "stringValue";
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("stringValue") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests String (XML).
     */
    public void testStringXML() {
        StringDTO dto = new StringDTO();
        dto.stringField = "stringValue";
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("stringValue") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class StringDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        String stringField;
    }

    /**
     * Tests Integer (CSV).
     */
    public void testIntegerCSV() {
        IntegerDTO dto = new IntegerDTO();
        dto.integerField = new Integer(21689);
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("21689") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Integer (FLR).
     */
    public void testIntegerFLR() {
        IntegerDTO dto = new IntegerDTO();
        dto.integerField = new Integer(21689);
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("21689") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Integer (XML).
     */
    public void testIntegerXML() {
        IntegerDTO dto = new IntegerDTO();
        dto.integerField = new Integer(21689);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("21689") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class IntegerDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        Integer integerField;
    }

    /**
     * Tests Long (CSV).
     */
    public void testLongCSV() {
        LongDTO dto = new LongDTO();
        dto.longField = new Long(92837);
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("92837") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Long (FLR).
     */
    public void testLongFLR() {
        LongDTO dto = new LongDTO();
        dto.longField = new Long(92837);
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("92837") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Long (XML).
     */
    public void testLongXML() {
        LongDTO dto = new LongDTO();
        dto.longField = new Long(92837);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("92837") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class LongDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        Long longField;
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
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        BigDecimal bigDecimalField;
    }

    /**
     * Tests Date (CSV).
     */
    public void testDateCSV() {
        DateDTO dto = new DateDTO();
        dto.dateField = (Date) (new DateConverter()).fromString("28.03.2008");
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("28.03.2008") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Date (FLR).
     */
    public void testDateFLR() {
        DateDTO dto = new DateDTO();
        dto.dateField = (Date) (new DateConverter()).fromString("28.03.2008");
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("28.03.2008") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Date (XML).
     */
    public void testDateXML() {
        DateDTO dto = new DateDTO();
        dto.dateField = (Date) (new DateConverter()).fromString("28.03.2008");
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("28.03.2008") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class DateDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        Date dateField;
    }

    /**
     * Tests Boolean (CSV).
     */
    public void testBooleanCSV() {
        BooleanDTO dto = new BooleanDTO();
        dto.booleanField = new Boolean(true);
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("true") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests Boolean (FLR).
     */
    public void testBooleanFLR() {
        BooleanDTO dto = new BooleanDTO();
        dto.booleanField = new Boolean(true);
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("true") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests Boolean (XML).
     */
    public void testBooleanXML() {
        BooleanDTO dto = new BooleanDTO();
        dto.booleanField = new Boolean(true);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("true") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class BooleanDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement()
        Boolean booleanField;
    }

    /**
     * Tests enum (CSV).
     */
    public void testEnumCSV() {
        EnumDTO dto = new EnumDTO();
        dto.enumField = EnumType.ENUM_VALUE1;
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("ENUM_VALUE1") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests enum (FLR).
     */
    public void testEnumFLR() {
        EnumDTO dto = new EnumDTO();
        dto.enumField = EnumType.ENUM_VALUE1;
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("ENUM_VALUE1") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests enum (XML).
     */
    public void testEnumXML() {
        EnumDTO dto = new EnumDTO();
        dto.enumField = EnumType.ENUM_VALUE1;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("ENUM_VALUE1") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class EnumDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        EnumType enumField;
    }

    /**
     * Tests primitive boolean (CSV).
     */
    public void testPrimitiveBooleanCSV() {
        PrimitiveBooleanDTO dto = new PrimitiveBooleanDTO();
        dto.primitiveBooleanField = false;
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("false") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests primitive boolean (FLR).
     */
    public void testPrimitiveBooleanFLR() {
        PrimitiveBooleanDTO dto = new PrimitiveBooleanDTO();
        dto.primitiveBooleanField = false;
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("false") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests primitive boolean (XML).
     */
    public void testPrimitiveBooleanXML() {
        PrimitiveBooleanDTO dto = new PrimitiveBooleanDTO();
        dto.primitiveBooleanField = false;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("false") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class PrimitiveBooleanDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        boolean primitiveBooleanField;
    }

    /**
     * Tests primitive int (CSV).
     */
    public void testPrimitiveIntCSV() {
        PrimitiveIntDTO dto = new PrimitiveIntDTO();
        dto.primitiveIntField = 2106;
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("2106") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests primitive int (FLR).
     */
    public void testPrimitiveIntFLR() {
        PrimitiveIntDTO dto = new PrimitiveIntDTO();
        dto.primitiveIntField = 2106;
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("2106") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests primitive int (XML).
     */
    public void testPrimitiveIntXML() {
        PrimitiveIntDTO dto = new PrimitiveIntDTO();
        dto.primitiveIntField = 2106;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("2106") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class PrimitiveIntDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        int primitiveIntField;
    }

    /**
     * Tests primitive long (CSV).
     */
    public void testPrimitiveLongCSV() {
        PrimitiveLongDTO dto = new PrimitiveLongDTO();
        dto.primitiveLongField = 2189798;
        assertTrue(JSefaTestUtil.serialize(CSV, dto).indexOf("2189798") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests primitive long (FLR).
     */
    public void testPrimitiveLongFLR() {
        PrimitiveLongDTO dto = new PrimitiveLongDTO();
        dto.primitiveLongField = 2189798;
        assertTrue(JSefaTestUtil.serialize(FLR, dto).indexOf("2189798") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests primitive long (XML).
     */
    public void testPrimitiveLongXML() {
        PrimitiveLongDTO dto = new PrimitiveLongDTO();
        dto.primitiveLongField = 2189798;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("2189798") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class PrimitiveLongDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        @XmlElement()
        long primitiveLongField;
    }

}
