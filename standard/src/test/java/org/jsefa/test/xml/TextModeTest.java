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

import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlTextContent;
import org.jsefa.xml.lowlevel.TextMode;

/**
 * Tests the correct serialization/deserialization of Elements annotated with different textModes.
 * 
 * @author Matthias Derer
 * 
 */

public class TextModeTest extends TestCase {
    
    private static final String LINE_BREAK = System.getProperty("line.separator");

    /**
     * Tests the correct serialization/deserialization of a DTO with an empty <code>String</code> field.
     */
    public void testEmptyElement() {
        SimpleElementTestDTO dto = new SimpleElementTestDTO();
        dto.element = "";
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "element");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO with a <code>String</code> field.
     */
    public void testStringElement() {
        SimpleElementTestDTO dto = new SimpleElementTestDTO();
        dto.element = "554546";
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue(serializationResult.indexOf("<![CDATA[554546]]>") != -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to a element with an empty text content.
     */
    public void testEmptyTextContentElement() {
        TextContentElementTestDTO dto = new TextContentElementTestDTO();
        dto.content = "";
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "TextContentElementTestDTO");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to a element with text content.
     */
    public void testTextContentElement() {
        TextContentElementTestDTO dto = new TextContentElementTestDTO();
        dto.content = "554546";
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue(serializationResult.indexOf("<![CDATA[554546]]>") != -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to an empty element list.
     */
    public void testEmptyElementList() {
        ElementListTestDTO dto = new ElementListTestDTO();
        dto.elementList = new ArrayList<String>();
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEmptyElementWritten(serializationResult, "elementList");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests the correct serialization/deserialization of a DTO mapped to an element list.
     */
    public void testElementList() {
        ElementListTestDTO dto = new ElementListTestDTO();
        dto.elementList = new ArrayList<String>();
        dto.elementList.add("432432");
        dto.elementList.add("551212");
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue(serializationResult.indexOf("<![CDATA[432432]]>") != -1);
        assertTrue(serializationResult.indexOf("<![CDATA[551212]]>") != -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }
    
    /**
     * Tests the correct serialization of a text node including the string ']]>' .
     */
    public void testCDATASequence() {
        TextContentElementTestDTO dtoMiddle = new TextContentElementTestDTO();
        dtoMiddle.content = "1234]]>5678";
        String dtoMiddleExpected = "<![CDATA[1234]]]]><![CDATA[>5678]]>";
        String dtoMiddleResult = JSefaTestUtil.serialize(XML, dtoMiddle);
        assertTrue(dtoMiddleResult.indexOf(dtoMiddleExpected) > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dtoMiddle);
        
        TextContentElementTestDTO dtoBeginning = new TextContentElementTestDTO();
        dtoBeginning.content = "]]>12345678";
        String dtoBeginningExpected = "<![CDATA[]]]]><![CDATA[>12345678]]>";
        String dtoBeginningResult = JSefaTestUtil.serialize(XML, dtoBeginning);
        assertTrue(dtoBeginningResult.indexOf(dtoBeginningExpected) > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dtoBeginning);
        
        TextContentElementTestDTO dtoEnd = new TextContentElementTestDTO();
        dtoEnd.content = "12345678]]>";
        String dtoEndExpected = "<![CDATA[12345678]]]]><![CDATA[>]]>";
        String dtoEndResult = JSefaTestUtil.serialize(XML, dtoEnd);
        assertTrue(dtoEndResult.indexOf(dtoEndExpected) > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dtoEnd);
        
        TextContentElementTestDTO dtoAll = new TextContentElementTestDTO();
        dtoAll.content = "]]>1234]]>5678]]>";
        String dtoAllExpected = "<![CDATA[]]]]><![CDATA[>1234]]]]><![CDATA[>5678]]]]><![CDATA[>]]>";
        String dtoAllResult = JSefaTestUtil.serialize(XML, dtoAll);
        assertTrue(dtoAllResult.indexOf(dtoAllExpected) > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dtoAll);
    }

    private void assertEmptyElementWritten(String serializationResult, String elementName) {
        boolean shortVersionFound = serializationResult.replaceAll(LINE_BREAK, " ").matches(
                ".*<" + elementName + "\\s*/>.*");
        boolean longVersionFound = serializationResult.indexOf("<" + elementName + "></" + elementName + ">") > 0;
        assertTrue(shortVersionFound || longVersionFound);
    }

    @XmlDataType()
    static final class SimpleElementTestDTO extends AbstractTestDTO {
        @XmlElement(textMode = TextMode.CDATA)
        String element;
    }

    @XmlDataType()
    static final class TextContentElementTestDTO extends AbstractTestDTO {
        @XmlTextContent(textMode = TextMode.CDATA)
        String content;
    }

    @XmlDataType()
    static final class ElementListTestDTO extends AbstractTestDTO {
        @XmlElementList(items = {@ListItem(name = "item", textMode = TextMode.CDATA)})
        List<String> elementList;
    }

}
