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

import java.util.List;

import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests to test the serialization/deserialization of DTOs with absent values.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class NoValueTest extends TestCase {

    /**
     * Tests serialization/deserialization with a null value for an element
     * list.
     */
    public void testElementList() {
        Object dto = new ElementListTestDTO();
        assertEmptyElementWritten(JSefaTestUtil.serialize(XML, dto), "ElementListTestDTO");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests serialization/deserialization with a null value for the text
     * content of an element.
     * <p>
     * Note that a round trip check is not possible as the deserialization will
     * return an empty <code>String</code> for the text content.
     */
    @SuppressWarnings("unchecked")
    public void testTextContent() {
        TextContentTestDTO dto = new TextContentTestDTO();
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "TextContentTestDTO");
        TextContentTestDTO dto2 = JSefaTestUtil.deserialize(XML, serializationResult, TextContentTestDTO.class);
        assertNotNull(dto2);
        assertNotNull(dto2.content);
        assertTrue(dto2.content.length() == 0);
    }

    private void assertEmptyElementWritten(String serializationResult, String elementName) {
        boolean shortVersionFound = serializationResult.replaceAll("\n", " ").matches(
                ".*<" + elementName + "\\s*/>.*");
        boolean longVersionFound = serializationResult.indexOf("<" + elementName + "></" + elementName + ">") > 0;
        assertTrue(shortVersionFound || longVersionFound);
    }

    @XmlDataType()
    static class ElementListTestDTO extends AbstractTestDTO {
        @XmlElementList(items = {@ListItem(name = "item", objectType = String.class)})
        List<String> elementList;
    }

    @XmlDataType()
    static class TextContentTestDTO extends AbstractTestDTO {
        @XmlTextContent()
        String content;
    }

}
