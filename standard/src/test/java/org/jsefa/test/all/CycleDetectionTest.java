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

import org.jsefa.IOFactoryException;
import org.jsefa.SerializationException;
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
 * Tests to test if cycles in the object graph are detected during serialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CycleDetectionTest extends TestCase {
    /**
     * Tests cycle detection for XML serialization for a cycle in the graph of instances.
     */
    public void testXMLForInstanceGraphCycle() {
        try {
            JSefaTestUtil.serialize(XML, createCyclicDTO());
            fail("No SerializationException thrown");
        } catch (SerializationException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Tests cycle detection for XML for a cycle in the graph of types only (no cycle in the graph of
     * instances).
     */
    public void testXMLForTypeGraphCycle() {
        JSefaTestUtil.serialize(XML, createNonCyclicDTO());
    }

    /**
     * Tests cycle detection for CSV for a cycle in the graph of types only.
     */
    public void testCSV() {
        checkRbf(CSV);
    }

    /**
     * Tests cycle detection for FLR for a cycle in the graph of types only.
     */
    public void testFLR() {
        checkRbf(FLR);
    }

    private void checkRbf(FormatType formatType) {
        try {
            JSefaTestUtil.serialize(formatType, createCyclicDTO());
            fail("No IOFactoryException thrown");
        } catch (IOFactoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    private TestDTOA createCyclicDTO() {
        TestDTOA dto = new TestDTOA();
        dto.fieldB = new TestDTOB();
        dto.fieldB.fieldA = dto;
        return dto;
    }

    private TestDTOA createNonCyclicDTO() {
        TestDTOA dto = new TestDTOA();
        dto.fieldB = new TestDTOB();
        dto.fieldB.fieldA = null;
        return dto;
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class TestDTOA extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1)
        @XmlElement()
        TestDTOB fieldB;
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class TestDTOB extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1)
        @XmlElement()
        TestDTOA fieldA;
    }

}
