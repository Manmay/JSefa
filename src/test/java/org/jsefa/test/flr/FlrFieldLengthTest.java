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
import org.jsefa.test.common.JSefaTestUtil;

/**
 * Tests to test the FLR serialization/deserialization with focus on the
 * length-handling.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class FlrFieldLengthTest extends TestCase {

    /**
     * Tests the case that the value has the same length as the FLR field.
     */
    public void testExpectedLength() {
        check("1234567", "1234567");
    }

    /**
     * Tests the case that the value is shorter than the length of the FLR
     * field.
     */
    public void testShorter() {
        check("123456", "123456#");
    }

    /**
     * Tests the case that the value is longer than the length of the FLR field.
     */
    public void testLonger() {
        check("12345678", "1234567");
    }

    private void check(String valueToSerialize, String expectedSerializedValue) {
        TestDTO dto = new TestDTO();
        dto.field = expectedSerializedValue;
        assertTrue(JSefaTestUtil.serialize(FLR, dto).equals(expectedSerializedValue + "\n"));
    }

    @FlrDataType()
    static final class TestDTO {
        @FlrField(pos = 1, length = 7, padCharacter = '#')
        String field;
    }

}
