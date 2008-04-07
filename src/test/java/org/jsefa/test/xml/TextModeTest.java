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

    private void assertEmptyElementWritten(String serializationResult, String elementName) {
        boolean shortVersionFound = serializationResult.replaceAll("\n", " ").matches(
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
