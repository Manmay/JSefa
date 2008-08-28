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


package org.jsefa.test.flr;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.FLR;
import junit.framework.TestCase;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;

/**
 * Tests to test the FLR deserialization of records with less or more fields than declared.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class UnmatchingFieldCountTest extends TestCase {
    
    /**
     * Tests it with less fields than declard.
     */
    public void testWithLessFields() {
        check("a b c ", "a", "b", "c");
        check("a b c", "a", "b", "c");
        check("a b ", "a", "b", null);
        check("a b", "a", "b", null);
        check("a ", "a", null, null);
        check("a", "a", null, null);
    }
    
    /**
     * Tests it with more fields than declard.
     */
    public void testWithMoreFields() {
        check("a b c ", "a", "b", "c");
        check("a b c d ", "a", "b", "c");
        check("a b c d e ", "a", "b", "c");
    }

    /**
     * Tests it with an empty record.
     */
    @SuppressWarnings("unchecked")
    public void testEmptyRecord() {
        assertNull(JSefaTestUtil.deserialize(FLR, "", TestDTO.class));
    }
    
    @SuppressWarnings("unchecked")
    private void check(String input, String fieldA, String fieldB, String fieldC) {
        TestDTO dto = JSefaTestUtil.deserialize(FLR, input, TestDTO.class);
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
    
    @FlrDataType()
    private static final class TestDTO extends AbstractTestDTO {
        @FlrField(pos = 1, length = 2)
        String fieldA;

        @FlrField(pos = 2, length = 2)
        String fieldB;

        @FlrField(pos = 3, length = 2)
        String fieldC;

    }    
}
