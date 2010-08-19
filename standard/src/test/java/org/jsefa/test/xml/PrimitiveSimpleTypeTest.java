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
