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

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
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
     * Tests the correctness of the input position for a low level error (document is not well formed).
     */
    @SuppressWarnings("unchecked")
    public void testInputPositionForLowLevelError() {
        String inputString = "a;3\nb;3\nerror\n";
        try {
            JSefaTestUtil.deserialize(FormatType.CSV, inputString, SimpleTestDTO.class);
            fail();
        } catch (DeserializationException e) {
            assertNotNull(e.getInputPosition());
            assertEquals(3, e.getInputPosition().getLineNumber());
        }
    }
    
    /**
     * Tests the correctness of the input position for a conversion error.
     */
    @SuppressWarnings("unchecked")
    public void testInputPositionForConversionException() {
        String inputString = "a;3\nb;3\nc;error\n";
        try {
            JSefaTestUtil.deserialize(FormatType.CSV, inputString, SimpleTestDTO.class);
            fail();
        } catch (DeserializationException e) {
            assertNotNull(e.getInputPosition());
            assertEquals(e.getInputPosition().getLineNumber(), 3);
        }
    }

    @CsvDataType()
    static final class SimpleTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        String b;

        @CsvField(pos = 1)
        int c;
    }
    

}
