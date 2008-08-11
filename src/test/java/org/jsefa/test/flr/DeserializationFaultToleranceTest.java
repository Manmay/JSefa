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

import static org.jsefa.test.flr.DeserializationFaultToleranceTest.Mode.INVALID;
import static org.jsefa.test.flr.DeserializationFaultToleranceTest.Mode.VALID;

import java.io.StringReader;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
import org.jsefa.Deserializer;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.test.common.AbstractTestDTO;

/**
 * Tests the fault tolerance when deserializing a list of DTOs where some of
 * them cause an Exception as they are not valid. I. e. after an Exception
 * caused by invalid elements one should be able to read in the next DTO.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public class DeserializationFaultToleranceTest extends TestCase {
    enum Mode {
        VALID, INVALID
    }

    public void testWithInvalidElements() {
        assertEquals(countDTO(TestDTO.class, createFLR(INVALID, VALID)), 1);
        assertEquals(countDTO(TestDTO.class, createFLR(VALID, INVALID)), 1);
        assertEquals(countDTO(TestDTO.class, createFLR(INVALID, VALID, INVALID, VALID, VALID, INVALID)), 3);
        assertEquals(countDTO(TestDTO.class, createFLR(VALID, VALID, INVALID, VALID, VALID, INVALID, VALID)), 5);
    }

    private int countDTO(Class<?> objectType, String xml) {
        Deserializer deserializer = FlrIOFactory.createFactory(objectType).createDeserializer();
        StringReader reader = new StringReader(xml);
        deserializer.open(reader);
        int count = 0;
        while (deserializer.hasNext()) {
            try {
                deserializer.next();
                count++;
            } catch (DeserializationException e) {
                // not valid
            }
        }
        return count;
    }

    private String createFLR(Mode... modes) {
        StringBuffer result = new StringBuffer();
        for (Mode mode : modes) {
            switch (mode) {
            case VALID:
                result.append("3    text \n");
                break;
            case INVALID:
                result.append("text text \n");
                break;
            }
        }
        return result.toString();
    }

    @FlrDataType()
    static final class TestDTO extends AbstractTestDTO {
        @FlrField(pos = 1, length=5)
        int intField;

        @FlrField(pos = 2, length = 5)
        String textField;
    }
}
