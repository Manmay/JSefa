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

package org.jsefa.xml;

import static org.jsefa.JSefaTestUtil.FormatType.XML;
import junit.framework.TestCase;

import org.jsefa.JSefaTestUtil;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test elements with an xsd:type attribute denoting a standard data
 * type.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class DataTypeDefaultNameTest extends TestCase {
    /**
     * Tests it with 'xs:string'.
     */
    public void testStringType() {
        StringTestDTO testDTO = deserialize(StringTestDTO.class, "xs:string", "content");
        assertEquals("content", testDTO.element);
    }
    
    /**
     * Tests it with 'xs:int'.
     */
    public void testIntType() {
        IntTestDTO testDTO = deserialize(IntTestDTO.class, "xs:int", "-37");
        assertTrue(-37 == testDTO.element);
    }

    /**
     * Tests it with 'xs:Integer'.
     */
    public void testIntegerType() {
        IntegerTestDTO testDTO = deserialize(IntegerTestDTO.class, "xs:integer", "-37");
        assertTrue(-37 == testDTO.element);
    }

    /**
     * Tests it with 'xs:long'.
     */
    public void testLongType() {
        LongTestDTO testDTO = deserialize(LongTestDTO.class, "xs:long", "-37");
        assertTrue(-37 == testDTO.element);
    }

    /**
     * Tests it with 'xs:boolean'.
     */
    public void testBooleanType() {
        BooleanTestDTO testDTO = deserialize(BooleanTestDTO.class, "xs:boolean", "true");
        assertTrue(testDTO.element);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(Class<T> objectType, String dataTypeName, String fieldContent) {
        StringBuffer xmlString = new StringBuffer();
        xmlString.append("<");
        xmlString.append(objectType.getSimpleName());
        xmlString.append(">\n");
        xmlString.append("<element xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' ");
        xmlString.append("xmlns:xs='http://www.w3.org/2001/XMLSchema' xsi:type='");
        xmlString.append(dataTypeName);
        xmlString.append("'>");
        xmlString.append(fieldContent);
        xmlString.append("</element>\n");
        xmlString.append("</");
        xmlString.append(objectType.getSimpleName());
        xmlString.append(">");
        return JSefaTestUtil.deserialize(XML, xmlString.toString(), objectType);
    }

    @XmlDataType()
    static final class StringTestDTO {
        @XmlElement()
        String element;
    }

    @XmlDataType()
    static final class IntTestDTO {
        @XmlElement()
        int element;
    }

    @XmlDataType()
    static final class IntegerTestDTO {
        @XmlElement()
        Integer element;
    }

    @XmlDataType()
    static final class LongTestDTO {
        @XmlElement()
        // also works fine with long instead of Long
        Long element;
    }

    @XmlDataType()
    static final class BooleanTestDTO {
        @XmlElement()
        // also works fine with boolean instead of Boolean
        Boolean element;
    }
}
