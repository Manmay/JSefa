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

package org.jsefa.test.all;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.FLR;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;
import junit.framework.TestCase;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of DTOs with absent complex values.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class NoComplexValueTest extends TestCase {

    /**
     * Test with null value element (XML).
     */
    public void testElementXML() {
        XmlParentDTO dto = new XmlParentDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("field") < 0);
    }

    /**
     * Test with null value at start (CSV).
     */
    public void testNullValueAtStartCSV() {
        doCheck(CSV, null, createChildDTO("b"), createChildDTO("c"));
    }

    /**
     * Test with null value at start (FLR).
     */
    public void testNullValueAtStartFLR() {
        doCheck(FLR, null, createChildDTO("b"), createChildDTO("c"));
    }

    /**
     * Test with null value at center (CSV).
     */
    public void testNullValueAtCenterCSV() {
        doCheck(CSV, createChildDTO("a"), null, createChildDTO("c"));
    }

    /**
     * Test with null value at center (FLR).
     */
    public void testNullValueAtCenterFLR() {
        doCheck(FLR, createChildDTO("a"), null, createChildDTO("c"));
    }

    /**
     * Test with null value at end (CSV).
     */
    public void testNullValueAtEndCSV() {
        doCheck(CSV, createChildDTO("a"), createChildDTO("b"), null);
    }

    /**
     * Test with null value at end (FLR).
     */
    public void testNullValueAtEndFLR() {
        doCheck(FLR, createChildDTO("a"), createChildDTO("b"), null);
    }

    private void doCheck(FormatType formatType, ChildDTO fieldA, ChildDTO fieldB, ChildDTO fieldC) {
        RbfParentDTO dto = createRbfParentDTO(fieldA, fieldB, fieldC);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(formatType, dto);
    }

    private RbfParentDTO createRbfParentDTO(ChildDTO fieldA, ChildDTO fieldB, ChildDTO fieldC) {
        RbfParentDTO dto = new RbfParentDTO();
        dto.fieldA = fieldA;
        dto.fieldB = fieldB;
        dto.fieldC = fieldC;
        return dto;
    }

    private ChildDTO createChildDTO(String fieldValue) {
        ChildDTO dto = new ChildDTO();
        dto.fieldD = fieldValue;
        return dto;
    }

    @CsvDataType()
    @FlrDataType()
    static final class RbfParentDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1)
        ChildDTO fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2)
        ChildDTO fieldB;

        @CsvField(pos = 3)
        @FlrField(pos = 3)
        ChildDTO fieldC;
    }

    @XmlDataType()
    static final class XmlParentDTO extends AbstractTestDTO {
        @XmlElement()
        ChildDTO field;
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class ChildDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement()
        String fieldD;
    }

}
