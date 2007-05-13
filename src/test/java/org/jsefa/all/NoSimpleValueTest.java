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
import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.JSefaTestUtil.FormatType;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of DTOs with absent simple
 * values.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class NoSimpleValueTest extends TestCase {

    /**
     * Test with a null value for an element (XML).
     */
    public void testElementXML() {
        Object dto = new XmlElementTestDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("element") < 0);
    }

    /**
     * Test with a null value for an attribute (XML).
     */
    public void testAttributeXML() {
        Object dto = new XmlAttributeTestDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("attribute") < 0);
    }
    
    /**
     * Test with null value at start (CSV).
     */
    public void testNullValueAtStartCSV() {
        doRbfCheck(CSV, null, "b", "c");
    }

    /**
     * Test with null value at start (FLR).
     */
    public void testNullValueAtStartFLR() {
        doRbfCheck(FLR, null, "b", "c");
    }

    /**
     * Test with null value at center (CSV).
     */
    public void testNullValueAtCenterCSV() {
        doRbfCheck(CSV, "a", null, "c");
    }

    /**
     * Test with null value at center (FLR).
     */
    public void testNullValueAtCenterFLR() {
        doRbfCheck(FLR, "a", null, "c");
    }

    /**
     * Test with null value at end (CSV).
     */
    public void testNullValueAtEndCSV() {
        doRbfCheck(CSV, "a", "b", null);
    }

    /**
     * Test with null value at end (FLR).
     */
    public void testNullValueAtEndFLR() {
        doRbfCheck(FLR, "a", "b", null);
    }

    private void doRbfCheck(FormatType formatType, String fieldAValue, String fieldBValue, String fieldCValue) {
        RbfTestDTO dto = new RbfTestDTO();
        dto.fieldA = fieldAValue;
        dto.fieldB = fieldBValue;
        dto.fieldC = fieldCValue;
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(formatType, dto);
    }

    @CsvDataType()
    @FlrDataType()
    static class RbfTestDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;

        @CsvField(pos = 3)
        @FlrField(pos = 3, length = 10)
        String fieldC;
    }

    @XmlDataType()
    static class XmlElementTestDTO extends AbstractTestDTO {
        @XmlElement()
        String element;
    }

    @XmlDataType()
    static class XmlAttributeTestDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attribute;
    }

}
