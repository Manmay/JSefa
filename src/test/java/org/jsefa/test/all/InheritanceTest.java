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
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of DTOs with inheritance.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 */
public class InheritanceTest extends TestCase {

    /**
     * Test inheritance at entry point level (CSV).
     */
    public void testAtEntryPointLevelCSV() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, createSubDTO());
    }

    /**
     * Test inheritance at entry point level (FLR).
     */
    public void testAtEntryPointLevelFLR() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, createSubDTO());
    }

    /**
     * Test inheritance at entry point level (XML).
     */
    public void testAtEntryPointLevelXML() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, createSubDTO());
    }

    /**
     * Test inheritance at level 1 of the object hierarchy (CSV).
     */
    public void testAtLevel1CSV() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, createParentDTO());
    }

    /**
     * Test inheritance at level 1 of the object hierarchy (FLR).
     */
    public void testAtLevel1FLR() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, createParentDTO());
    }

    /**
     * Test inheritance at level 1 of the object hierarchy (XML).
     */
    public void testAtLevel1XML() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, createParentDTO());
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
        SubDTO fieldB;
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
    @XmlDataType()
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
