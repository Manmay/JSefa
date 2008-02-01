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

import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;

/**
 * Tests to test the position of xml nodes (i. e. their ordering).
 * 
 * @author Marko Kovacevic
 * 
 */
public class NodePositionTest extends TestCase {

    /**
     * Test the node position when all nodes are declared with a position annotation (i. e. there is a total order
     * on the set of nodes).
     */
    public void testTotalOrder() {
        TotalOrderTestDTO obj = new TotalOrderTestDTO();
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertIsBefore("element1", "element2", serializationResult);
        assertIsBefore("element2", "elementList3", serializationResult);
        assertIsBefore("elementList3", "element4", serializationResult);
        assertIsBefore("element4", "elementList5", serializationResult);
    }

    /**
     * Test the node position when not all nodes are declared with a position annotation (i. e. there is a partial
     * order on the set of nodes).
     * 
     */
    public void testPartialOrder() {
        PartialOrderTestDTO obj = new PartialOrderTestDTO();
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertIsBefore("element1", "elementList2", serializationResult);
        assertIsBefore("elementList2", "element3", serializationResult);
        assertIsBefore("element3", "elementList4", serializationResult);
        assertIsBefore("elementList4", "element5", serializationResult);
    }

    private void assertIsBefore(String elementNameA, String elementNameB, String xmlDoc) {
        assertTrue(xmlDoc.indexOf(elementNameA) >= 0);
        assertTrue(xmlDoc.indexOf(elementNameB) >= 0);
        assertTrue(xmlDoc.indexOf(elementNameA) < xmlDoc.indexOf(elementNameB));
    }

    @XmlDataType()
    static final class TotalOrderTestDTO {
        @XmlElement(pos = 1)
        String element1 = "e1";

        @XmlElementList(pos = 65, implicit = false, items = {@ListItem(name = "item")})
        List<String> elementList5 = new ArrayList<String>();

        @XmlElement(pos = 40)
        String element4 = "e4";

        @XmlElement(pos = 2)
        String element2 = "e2";

        @XmlElementList(pos = 3, implicit = false, items = {@ListItem(name = "item")})
        List<String> elementList3 = new ArrayList<String>();
    }

    @XmlDataType()
    static final class PartialOrderTestDTO {
        @XmlElement(pos = 1)
        String element1 = "e1";

        @XmlElement(pos = 40)
        String element5 = "e5";

        String element6 = "e6";

        @XmlElementList(pos = 4, implicit = false, items = {@ListItem(name = "listContent")})
        List<String> elementList4 = new ArrayList<String>();

        @XmlElement(pos = 3)
        String element3 = "e3";

        @XmlElementList(pos = 2, implicit = false, items = {@ListItem(name = "listContent")})
        List<String> elementList2 = new ArrayList<String>();

        String element7 = "e7";
    }

}
