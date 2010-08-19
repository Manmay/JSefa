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

import static org.jsefa.test.common.JSefaTestUtil.FormatType.*;
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
 * Tests for testing the correct serialization when a subtype of an expected type is given (without polymorphism).
 * <p>
 * The expected result is that the known super type is determined and the fields of this supertype are serialized
 * (and not the additional fields of the subtype).
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class TypeReductionTest extends TestCase {

    /**
     * Test type reduction at entry point level (CSV).
     */
    public void testAtEntryPointLevelCSV() {
        checkAtEntryPointLevel(CSV);
    }

    /**
     * Test type reduction at entry point level (FLR).
     */
    public void testAtEntryPointLevelFLR() {
        checkAtEntryPointLevel(FLR);
    }

    /**
     * Test type reduction at entry point level (XML).
     */
    public void testAtEntryPointLevelXML() {
        checkAtEntryPointLevel(XML);
    }

    /**
     * Test type reduction at level 1 of the object hierarchy (CSV).
     */
    public void testAtLevel1CSV() {
        checkAtLevel1(CSV);
    }

    /**
     * Test type reduction at level 1 of the object hierarchy (FLR).
     */
    public void testAtLevel1FLR() {
        checkAtLevel1(FLR);
    }

    /**
     * Test type reduction at level 1 of the object hierarchy (XML).
     */
    public void testAtLevel1XML() {
        checkAtLevel1(XML);
    }

    @SuppressWarnings("unchecked")
    private void checkAtEntryPointLevel(FormatType formatType) {
        SubDTO dto = createSubDTO();
        String serializationResult = JSefaTestUtil.serialize(formatType, SuperDTO.class, dto);
        assertTrue(serializationResult.indexOf("SubDTODataType") < 0);
        SuperDTO dto2 = JSefaTestUtil.deserialize(formatType, serializationResult, SuperDTO.class);
        assertEquals(dto.fieldA, dto2.fieldA);
        assertEquals(dto.fieldB, dto2.fieldB);
    }

    @SuppressWarnings("unchecked")
    private void checkAtLevel1(FormatType formatType) {
        ParentDTO dto = createParentDTO();
        String serializationResult = JSefaTestUtil.serialize(formatType, dto);
        assertTrue(serializationResult.indexOf("SubDTODataType") < 0);
        ParentDTO dto2 = JSefaTestUtil.deserialize(formatType, serializationResult, ParentDTO.class);
        assertEquals(dto.fieldA, dto2.fieldA);
        assertEquals(dto.fieldB.fieldA, dto2.fieldB.fieldA);
        assertEquals(dto.fieldB.fieldB, dto2.fieldB.fieldB);
    }

    private SubDTO createSubDTO() {
        SubDTO object = new SubDTO();
        object.fieldA = "a";
        object.fieldB = "b";
        object.fieldC = "c";
        object.fieldD = "d";
        return object;
    }

    private ParentDTO createParentDTO() {
        ParentDTO object = new ParentDTO();
        object.fieldA = "a";
        object.fieldB = createSubDTO();
        return object;
    }

    @CsvDataType(defaultPrefix = "TYPE_PAR")
    @FlrDataType(defaultPrefix = "TYPE_PAR")
    @XmlDataType()
    static class ParentDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement()
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement()
        SuperDTO fieldB;
    }

    @CsvDataType(defaultPrefix = "TYPE_SUP")
    @FlrDataType(defaultPrefix = "TYPE_SUP")
    @XmlDataType()
    static class SuperDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement()
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement()
        String fieldB;
    }

    @CsvDataType(defaultPrefix = "TYPE_SUB")
    @FlrDataType(defaultPrefix = "TYPE_SUB")
    @XmlDataType(name = "SubDTODataType")
    static final class SubDTO extends SuperDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement()
        String fieldC;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement()
        String fieldD;
    }

}
