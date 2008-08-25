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


package org.jsefa.test.csv;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import junit.framework.TestCase;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;

/**
 * Tests to test the CSV deserialization of records with less or more fields than declared.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class UnmatchingFieldCountTest extends TestCase {
    
    public void testWithLessFields() {
        check("a;b;c", "a", "b", "c");
        check("a;b", "a", "b", null);
        check("a", "a", null, null);
    }
    
    public void testWithMoreFields() {
        check("a;b;c", "a", "b", "c");
        check("a;b;c;d", "a", "b", "c");
        check("a;b;c;d;e", "a", "b", "c");
    }

    @SuppressWarnings("unchecked")
    public void testEmptyRecord() {
        assertNull(JSefaTestUtil.deserialize(CSV, "", TestDTO.class));
    }
    
    @SuppressWarnings("unchecked")
    private void check(String input, String fieldA, String fieldB, String fieldC) {
        TestDTO dto = JSefaTestUtil.deserialize(CSV, input, TestDTO.class);
        if (fieldA == null) {
            assertNull(dto.fieldA);
        } else {
            assertTrue(fieldA.equals(dto.fieldA));
        }
        if (fieldB == null) {
            assertNull(dto.fieldB);
        } else {
            assertTrue(fieldB.equals(dto.fieldB));
        }
        if (fieldC == null) {
            assertNull(dto.fieldC);
        } else {
            assertTrue(fieldC.equals(dto.fieldC));
        }
    }
    
    @CsvDataType()
    private static final class TestDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        String fieldA;

        @CsvField(pos = 2)
        String fieldB;

        @CsvField(pos = 3)
        String fieldC;

    }    
}
