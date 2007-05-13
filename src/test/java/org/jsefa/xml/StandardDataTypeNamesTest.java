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

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.xml.annotation.Namespace;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlNamespaces;

/**
 * Tests to test the correct serialization/deserialization when a data type name
 * (denoting a simple type mapping) is specified for an annotated field.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class StandardDataTypeNamesTest extends TestCase {

    /**
     * Tests the standard data type <code>string</code>.
     */
    public void testString() {
        StringTestDTO dto = new StringTestDTO();
        dto.element = "test";
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the standard data type <code>int</code>.
     */
    @SuppressWarnings("unchecked")
    public void testInt() {
        IntTestDTO dto = new IntTestDTO();
        dto.element1 = 42;
        dto.element2 = 4711;
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        String inputString = serializationResult.replaceAll("42", "-42").replaceAll("4711", "+4711");
        IntTestDTO dto2 = JSefaTestUtil.deserialize(XML, inputString, IntTestDTO.class);
        assertTrue(dto2.element1 == -42);
        assertTrue(dto2.element2.equals(4711));
    }

    /**
     * Tests the standard data type <code>integer</code>.
     */
    @SuppressWarnings("unchecked")
    public void testInteger() {
        IntegerTestDTO dto = new IntegerTestDTO();
        dto.element1 = 42;
        dto.element2 = 4711;
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        String inputString = serializationResult.replaceAll("42", "-42").replaceAll("4711", "+4711");
        IntegerTestDTO dto2 = JSefaTestUtil.deserialize(XML, inputString, IntegerTestDTO.class);
        assertTrue(dto2.element1 == -42);
        assertTrue(dto2.element2.equals(4711));
    }

    /**
     * Tests the standard data type <code>long</code>.
     */
    @SuppressWarnings("unchecked")
    public void testLong() {
        LongTestDTO dto = new LongTestDTO();
        dto.element1 = 42;
        dto.element2 = 4711L;
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        String inputString = serializationResult.replaceAll("42", "-42").replaceAll("4711", "+4711");
        LongTestDTO dto2 = JSefaTestUtil.deserialize(XML, inputString, LongTestDTO.class);
        assertTrue(dto2.element1 == -42);
        assertTrue(dto2.element2.equals(4711L));
    }

    /**
     * Tests the standard data type <code>boolean</code>.
     */
    @SuppressWarnings("unchecked")
    public void testBoolean() {
        BooleanTestDTO dto = new BooleanTestDTO();
        dto.element1 = true;
        dto.element2 = false;
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @XmlDataType()
    @XmlNamespaces(@Namespace(prefix = "xs", uri = XmlConstants.XML_SCHEMA_URI))
    static final class StringTestDTO extends AbstractTestDTO {
        @XmlElement(dataTypeName = "xs:string")
        String element;
    }

    @XmlDataType()
    @XmlNamespaces(@Namespace(prefix = "xs", uri = XmlConstants.XML_SCHEMA_URI))
    static final class IntTestDTO extends AbstractTestDTO {
        @XmlElement(dataTypeName = "xs:int")
        int element1;

        @XmlElement(dataTypeName = "xs:int")
        Integer element2;
    }

    @XmlDataType()
    @XmlNamespaces(@Namespace(prefix = "xs", uri = XmlConstants.XML_SCHEMA_URI))
    static final class IntegerTestDTO extends AbstractTestDTO {
        @XmlElement(dataTypeName = "xs:integer")
        int element1;

        @XmlElement(dataTypeName = "xs:integer")
        Integer element2;
    }

    @XmlDataType()
    @XmlNamespaces(@Namespace(prefix = "xs", uri = XmlConstants.XML_SCHEMA_URI))
    static final class LongTestDTO extends AbstractTestDTO {
        @XmlElement(dataTypeName = "xs:long")
        long element1;

        @XmlElement(dataTypeName = "xs:long")
        Long element2;
    }


    @XmlDataType()
    @XmlNamespaces(@Namespace(prefix = "xs", uri = XmlConstants.XML_SCHEMA_URI))
    static final class BooleanTestDTO extends AbstractTestDTO {
        @XmlElement(dataTypeName = "xs:boolean")
        boolean element1;

        @XmlElement(dataTypeName = "xs:boolean")
        Boolean element2;
    }
}
