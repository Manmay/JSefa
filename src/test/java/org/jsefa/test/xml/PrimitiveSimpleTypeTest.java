package org.jsefa.test.xml;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;
import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests to test the serialization/deserialization of a primitive simple type used in different ways.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class PrimitiveSimpleTypeTest extends TestCase {
    /**
     * Tests it for an attribute.
     */
    public void testAttribute() {
        AttributeDTO dto = new AttributeDTO();
        dto.intField = 21689;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("21689") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }
    
    /**
     * Tests it for an element.
     */
    public void testElement() {
        ElementDTO dto = new ElementDTO();
        dto.intField = 21689;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("21689") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests it for a text content.
     */
    public void testTextContent() {
        TextContentDTO dto = new TextContentDTO();
        dto.intField = 21689;
        assertTrue(JSefaTestUtil.serialize(XML, dto).indexOf("21689") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @XmlDataType()
    static final class AttributeDTO extends AbstractTestDTO {
        @XmlAttribute()
        int intField;
    }    

    @XmlDataType()
    static final class ElementDTO extends AbstractTestDTO {
        @XmlElement()
        int intField;
    }    

    @XmlDataType()
    static final class TextContentDTO extends AbstractTestDTO {
        @XmlTextContent()
        int intField;
    }    

}
