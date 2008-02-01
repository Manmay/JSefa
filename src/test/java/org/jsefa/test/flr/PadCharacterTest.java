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
import org.jsefa.flr.lowlevel.Align;
import org.jsefa.test.common.JSefaTestUtil;

/**
 * Tests to test the FLR serialization/deserialization with focus on pad character.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class PadCharacterTest extends TestCase {

    /**
     * Test to test the FLR serialization/deserialization with focus on the pad character.
     */
    public void testPadCharacterDTO() {
        TestDTO dto = createPadCharacterDTO("value", "value2");
        assertTrue(JSefaTestUtil.serialize(FLR, dto).toString().equals("value##########$$value2\n"));
    }

    private TestDTO createPadCharacterDTO(String fieldAValue, String fieldBValue) {
        TestDTO object = new TestDTO();
        object.fieldA = fieldAValue;
        object.fieldB = fieldBValue;
        return object;
    }

    @FlrDataType()
    static final class TestDTO {
        @FlrField(pos = 1, length = 15, padCharacter = '#')
        String fieldA;

        @FlrField(pos = 2, length = 8, padCharacter = '$', align = Align.RIGHT)
        String fieldB;
    }
}
