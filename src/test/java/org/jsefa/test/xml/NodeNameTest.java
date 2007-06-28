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


/**
 * Tests to test the element name, attribute name and element list name
 * generation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class NodeNameTest extends TestCase {

    /**
     * Tests the element name generation when it is not explicitly given.
     */
    public void testDefaultElementName() {
        DefaultElementNameTestDTO obj = new DefaultElementNameTestDTO();
        obj.element = "value";
        check(obj, "<element>");
    }

    /**
     * Tests the element name generation when it is explicitly given.
     */
    public void testExplicitElementName() {
        ExplicitElementNameTestDTO obj = new ExplicitElementNameTestDTO();
        obj.element = "value";
        check(obj, "<explicitElementName>");
    }

    /**
     * Tests the attribute name generation when it is not explicitly given.
     */
    public void testDefaultAttributeName() {
        DefaultAttributeNameTestDTO obj = new DefaultAttributeNameTestDTO();
        obj.attribute = "value";
        check(obj, "attribute=");
    }

    /**
     * Tests the attribute name generation when it is explicitly given.
     */
    public void testExplicitAttributeName() {
        ExplicitAttributeNameTestDTO obj = new ExplicitAttributeNameTestDTO();
        obj.attribute = "value";
        check(obj, "explicitAttributeName=");
    }

    /**
     * Tests the element list name generation when it is not explicitly given.
     */
    public void testDefaultListName() {
        DefaultListNameTestDTO obj = new DefaultListNameTestDTO();
        obj.elementList.add("value");
        check(obj, "<elementList>");
    }

    /**
     * Tests the element list name generation when it is explicitly given.
     */
    public void testExplicitListName() {
        ExplicitListNameTestDTO obj = new ExplicitListNameTestDTO();
        obj.elementList.add("value");
        check(obj, "<explicitListName>");
    }

    private void check(Object obj, String expectedContent) {
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(serializationResult.indexOf(expectedContent) >= 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    @XmlDataType()
    static final class DefaultElementNameTestDTO extends AbstractTestDTO {
        @XmlElement()
        String element;
    }

    @XmlDataType()
    static final class ExplicitElementNameTestDTO extends AbstractTestDTO {
        @XmlElement(name = "explicitElementName")
        String element;
    }

    @XmlDataType()
    static final class DefaultAttributeNameTestDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attribute;
    }

    @XmlDataType()
    static final class ExplicitAttributeNameTestDTO extends AbstractTestDTO {
        @XmlAttribute(name = "explicitAttributeName")
        String attribute;
    }

    @XmlDataType()
    static final class DefaultListNameTestDTO extends AbstractTestDTO {
        @XmlElementList(implicit = false, items = {@ListItem(name = "listContent", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();
    }

    @XmlDataType()
    static final class ExplicitListNameTestDTO extends AbstractTestDTO {
        @XmlElementList(name = "explicitListName", implicit = false,
                items = {@ListItem(name = "item", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();
    }

}
