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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests the correct serialization/deserialization of empty DTOs mapped to complex elements, empty lists and empty
 * <code>String</code> fields.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class EmptyValueTest extends TestCase {

    /**
     * Tests the correct serialization/deserialization of a DTO with an empty <code>String</code> field.
     */
    public void testSimpleElement() {
        SimpleElementTestDTO dto = new SimpleElementTestDTO();
        dto.element = "";
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "element");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to a element with an empty text content.
     */
    public void testTextContentElement() {
        TextContentElementTestDTO dto = new TextContentElementTestDTO();
        dto.content = "";
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "TextContentElementTestDTO");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to a complex element with an empty field.
     */
    public void testComplexElement() {
        ComplexElementTestDTO dto = new ComplexElementTestDTO();
        dto.element = null;
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "ComplexElementTestDTO");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to an empty element list.
     */
    public void testElementList() {
        ElementListTestDTO dto = new ElementListTestDTO();
        dto.elementList = new ArrayList<String>();
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "elementList");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    private void assertEmptyElementWritten(String serializationResult, String elementName) {
        boolean shortVersionFound = serializationResult.replaceAll("\n", " ").matches(
                ".*<" + elementName + "\\s*/>.*");
        boolean longVersionFound = serializationResult.indexOf("<" + elementName + "></" + elementName + ">") > 0;
        assertTrue(shortVersionFound || longVersionFound);
    }

    @XmlDataType()
    static final class SimpleElementTestDTO extends AbstractTestDTO {
        @XmlElement()
        String element;
    }

    @XmlDataType()
    static final class TextContentElementTestDTO extends AbstractTestDTO {
        @XmlTextContent()
        String content;
    }

    @XmlDataType()
    static final class ComplexElementTestDTO extends AbstractTestDTO {
        @XmlElement()
        SimpleElementTestDTO element;
    }

    @XmlDataType()
    static final class ElementListTestDTO extends AbstractTestDTO {
        @XmlElementList(items = {@ListItem(name = "item")})
        List<String> elementList;
    }

}
