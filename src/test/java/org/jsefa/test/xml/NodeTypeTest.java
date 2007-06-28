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
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlTextContent;


/**
 * Tests to test the node type annotation.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public final class NodeTypeTest extends TestCase {

    /**
     * Test the element type.
     */
    public void testElement() {
        ElementTestDTO obj = new ElementTestDTO();
        obj.element = "ElementValue";
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(serializationResult.indexOf("<element>") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Test the attribute type.
     */
    public void testAttribute() {
        AttributeTestDTO obj = new AttributeTestDTO();
        obj.attribute = "attributeValue";
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(serializationResult.indexOf("attribute=") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Test the list type.
     */
    public void testElementList() {
        ElementListItemDTO obj = new ElementListItemDTO();
        obj.elementList = new ArrayList<String>();
        obj.elementList.add("elementListItemValue");
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(serializationResult.indexOf("<elementList>") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Test the text content.
     */
    public void testTextContent() {
        TextContentTestDTO obj = new TextContentTestDTO();
        obj.textContent = "testValue";
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(serializationResult.indexOf(">testValue<") >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    @XmlDataType()
    static final class ElementTestDTO extends AbstractTestDTO {
        @XmlElement()
        String element;
    }

    @XmlDataType()
    static final class AttributeTestDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attribute;
    }

    @XmlDataType()
    static final class TextContentTestDTO extends AbstractTestDTO {
        @XmlTextContent()
        String textContent;
    }

    @XmlDataType()
    static final class ElementListItemDTO extends AbstractTestDTO {
        @XmlElementList(items = {@ListItem(name = "item", objectType = String.class)})
        List<String> elementList;
    }

}
