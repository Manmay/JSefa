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

package org.jsefa.test.xml;

import static org.jsefa.test.xml.DeserializationFaultToleranceTest.Mode.INVALID;
import static org.jsefa.test.xml.DeserializationFaultToleranceTest.Mode.NOT_WELLFORMED;
import static org.jsefa.test.xml.DeserializationFaultToleranceTest.Mode.VALID;

import java.io.StringReader;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
import org.jsefa.Deserializer;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests the fault tolerance when deserializing a list of DTOs where some of them cause an Exception as they are not
 * valid (but well formed). I. e. after an Exception caused by invalid elements one should be able to read in the next
 * DTO.
 * <p>
 * Note: It is not possible to resume after encountering a not wellformed part of the document.
 * 
 * @author Norman Lahme-Huetig
 */

public class DeserializationFaultToleranceTest extends TestCase {
    enum Mode {
        VALID, INVALID, NOT_WELLFORMED
    }

    /**
     * Tests it with different positions of invalid elements.
     */
    public void testWithInvalidElements() {
        assertEquals(countDTO(TestDTO.class, createXML(INVALID, VALID)), 1);
        assertEquals(countDTO(TestDTO.class, createXML(VALID, INVALID)), 1);
        assertEquals(countDTO(TestDTO.class, createXML(INVALID, VALID, INVALID, VALID, VALID, INVALID)), 3);
        assertEquals(countDTO(TestDTO.class, createXML(VALID, VALID, INVALID, VALID, VALID, INVALID, VALID)), 5);
    }

    /**
     * Tests it with different positions of illformed elements.
     */
    public void testWithIllformedElements() {
        assertEquals(countDTO(TestDTO.class, createXML(NOT_WELLFORMED, VALID)), 0);
        assertEquals(countDTO(TestDTO.class, createXML(VALID, NOT_WELLFORMED)), 1);
    }

    private int countDTO(Class<?> objectType, String xml) {
        Deserializer deserializer = XmlIOFactory.createFactory(objectType).createDeserializer();
        StringReader reader = new StringReader(xml);
        deserializer.open(reader);
        int count = 0;
        try {
            while (deserializer.hasNext()) {
                try {
                    deserializer.next();
                    count++;
                } catch (DeserializationException e) {
                    continue;   // not valid
                }
            }
        } catch (DeserializationException e) {
            return count;   // not wellformed
        }
        return count;
    }

    private String createXML(Mode... modes) {
        StringBuffer result = new StringBuffer();
        result.append("<root>\n");
        for (Mode mode : modes) {
            result.append("<element>\n");
            switch (mode) {
            case VALID:
                result.append("<int-field>3</int-field>\n");
                result.append("<text-field>text</text-field>\n");
                break;
            case INVALID:
                result.append("<int-field>text</int-field>\n");
                result.append("<text-field>text</text-field>\n");
                break;
            case NOT_WELLFORMED:
                result.append("<int-field>text</text-field>\n");
                break;
            default:
                break;
            }
            result.append("</element>\n");
        }
        result.append("\n</root>");
        return result.toString();
    }

    @XmlDataType(defaultElementName = "element")
    static final class TestDTO extends AbstractTestDTO {
        @XmlElement(pos = 1, name = "int-field")
        int intField;

        @XmlElement(pos = 2, name = "text-field")
        String textField;
    }
}
