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

import java.io.StringReader;

import junit.framework.TestCase;

import org.jsefa.Deserializer;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.*;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;
import org.jsefa.xml.annotation.XmlDataType;

/**
 * Test full cycle (open, hasNext, next, close) of the deserializers.
 * 
 * @author Norman Lahme-Huetig
 * @author Guido Vrankar
 * 
 */
public class FullCycleTest extends TestCase {

    /**
     * Tests full cycle for XML.
     */
    public void testXML() {
        check(XML, "<root></root>", 0, TestDTOA.class);
        check(XML, "<TestDTOA><fieldA>a</fieldA><fieldB>b</fieldB></TestDTOA>", 1, TestDTOA.class);
    }

    /**
     * Tests full cycle for CSV for the case when no prefix is used.
     */
    public void testWithoutPrefixCSV() {
        check(CSV, "", 0, TestDTOA.class);
        check(CSV, "01234567890123456789", 1, TestDTOA.class);
    }

    /**
     * Tests full cycle for FLR for the case when no prefix is used.
     */
    public void testWithoutPrefixFLR() {
        check(FLR, "", 0, TestDTOA.class);
        check(FLR, "01234567890123456789", 1, TestDTOA.class);
    }

    /**
     * Tests full cycle for CSV for the case when prefixes are used.
     */
    public void testWithPrefixesCSV() {
        check(CSV, "", 0, TestDTOB.class, TestDTOC.class);
        check(CSV, "TYPE_B;01234567890123456789", 1, TestDTOB.class, TestDTOC.class);
    }

    /**
     * Tests full cycle for FLR for the case when prefixes are used.
     */
    public void testWithPrefixesFLR() {
        check(FLR, "", 0, TestDTOB.class, TestDTOC.class);
        check(FLR, "TYPE_B01234567890123456789", 1, TestDTOB.class, TestDTOC.class);
    }

    private void check(FormatType formatType, String inputString, int expectedElementCount,
            Class<?>... objectTypes) {
        Deserializer deserializer = JSefaTestUtil.createIOFactory(formatType, objectTypes).createDeserializer();
        deserializer.open(new StringReader(inputString));
        for (int i = 0; i < expectedElementCount; i++) {
            assertTrue(deserializer.hasNext());
            deserializer.next();
        }
        assertFalse(deserializer.hasNext());
        assertNull(deserializer.next());
        assertFalse(deserializer.hasNext());
        assertNull(deserializer.next());
        deserializer.close(true);
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class TestDTOA extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;
    }

    @CsvDataType(defaultPrefix = "TYPE_B")
    @FlrDataType(defaultPrefix = "TYPE_B")
    static final class TestDTOB extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;
    }

    @CsvDataType(defaultPrefix = "TYPE_C")
    @FlrDataType(defaultPrefix = "TYPE_C")
    static final class TestDTOC extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;
    }

}
