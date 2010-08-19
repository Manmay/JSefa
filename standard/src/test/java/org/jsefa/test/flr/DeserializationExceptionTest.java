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

import java.util.List;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
import org.jsefa.ObjectPathElement;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.lowlevel.Align;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;

/**
 * Tests the correctness of <code>DeserializationException</code>s.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class DeserializationExceptionTest extends TestCase {

    /**
     * Tests the correctness of the input position for a conversion error.
     */
    @SuppressWarnings("unchecked")
    public void testInputPositionForConversionException() {
        String inputString = "ok       1" + "\n" + "ok   error";
        try {
            JSefaTestUtil.deserialize(FormatType.FLR, inputString, SimpleTestDTO.class);
            fail();
        } catch (DeserializationException e) {
            assertNotNull(e.getInputPosition());
            assertEquals(e.getInputPosition().getLineNumber(), 2);
        }
    }

    /**
     * Tests the correctness of the object path for a conversion error.
     */
    @SuppressWarnings("unchecked")
    public void testObjectPathForConversionException() {
        String inputString = "ok   error";
        try {
            JSefaTestUtil.deserialize(FormatType.FLR, inputString, ComplexTestDTO.class);
            fail();
        } catch (DeserializationException e) {
            List<ObjectPathElement> path = e.getObjectPath();
            assertEquals(2, path.size());
            assertEquals(ComplexTestDTO.class, path.get(0).getObjectType());
            assertEquals("complexField", path.get(0).getFieldName());
            assertEquals(SimpleTestDTO.class, path.get(1).getObjectType());
            assertEquals("intField", path.get(1).getFieldName());
        }
    }

    @FlrDataType()
    static final class SimpleTestDTO extends AbstractTestDTO {
        @FlrField(pos = 0, length = 5)
        String stringField;

        @FlrField(pos = 1, length = 5, align = Align.RIGHT)
        int intField;
    }

    @FlrDataType()
    static final class ComplexTestDTO extends AbstractTestDTO {
        @FlrField(pos = 0)
        SimpleTestDTO complexField;
    }

}
