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

/**
 * Tests to test implicit lists with <code>String</code> elements and elements of different depth.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class ImplicitListTypeTest extends TestCase {

    /**
     * Tests a list with <code>String</code> elements.
     */
    public void testWithStringElements() {
        ListWithStringElements list = new ListWithStringElements();
        list.list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.list.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    /**
     * Tests a list with <code>Depth0DTO</code> elements.
     */
    public void testWithDepth0Elements() {
        ListWithDepth0Elements list = new ListWithDepth0Elements();
        list.list = new ArrayList<Depth0DTO>();
        for (int i = 0; i < 10; i++) {
            list.list.add(createDepth0DTO());
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    /**
     * Tests a list with <code>Depth1DTO</code> elements.
     */
    public void testWithDepth1Elements() {
        ListWithDepth1Elements list = new ListWithDepth1Elements();
        list.list = new ArrayList<Depth1DTO>();
        for (int i = 0; i < 10; i++) {
            list.list.add(createDepth1DTO());
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    /**
     * Tests a list with <code>Depth2DTO</code> elements.
     */
    public void testWithDepth2Elements() {
        ListWithDepth2Elements list = new ListWithDepth2Elements();
        list.list = new ArrayList<Depth2DTO>();
        for (int i = 0; i < 10; i++) {
            list.list.add(createDepth2DTO());
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    private Depth2DTO createDepth2DTO() {
        Depth2DTO object = new Depth2DTO();
        object.fieldA = createDepth1DTO();
        object.fieldB = "valueB";
        return object;
    }

    private Depth1DTO createDepth1DTO() {
        Depth1DTO object = new Depth1DTO();
        object.fieldC = createDepth0DTO();
        object.fieldD = "valueD";
        return object;
    }

    private Depth0DTO createDepth0DTO() {
        Depth0DTO object = new Depth0DTO();
        object.fieldE = "valueE";
        object.fieldF = "valueF";
        return object;
    }

    @XmlDataType()
    static final class ListWithStringElements extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<String> list;
    }

    @XmlDataType()
    static final class ListWithDepth0Elements extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<Depth0DTO> list;
    }

    @XmlDataType()
    static final class ListWithDepth1Elements extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<Depth1DTO> list;
    }

    @XmlDataType()
    static final class ListWithDepth2Elements extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<Depth2DTO> list;
    }

    @XmlDataType()
    static final class Depth2DTO extends AbstractTestDTO {
        @XmlElement()
        Depth1DTO fieldA;

        @XmlElement()
        String fieldB;
    }

    @XmlDataType()
    static final class Depth1DTO extends AbstractTestDTO {
        @XmlElement()
        Depth0DTO fieldC;

        @XmlElement()
        String fieldD;
    }

    @XmlDataType()
    static class Depth0DTO extends AbstractTestDTO {
        @XmlElement()
        String fieldE;

        @XmlElement()
        String fieldF;
    }

}
