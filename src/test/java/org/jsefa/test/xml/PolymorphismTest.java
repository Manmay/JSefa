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
 * Test for testing the correct serialization/deserialization with equally named elements of different data types.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class PolymorphismTest extends TestCase {

    /**
     * Tests a polymorphic element, i. e. an element with different possible data types which are all descendants
     * of a single super type.
     */
    public void testPolymorphicElement() {
        PolymorphicElementDTO dto = new PolymorphicElementDTO();
        SubDTOA subDTO = new SubDTOA();
        subDTO.element1 = "e1";
        subDTO.element2 = "e2";
        dto.element = subDTO;
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue("Default preferred prefix xsi not found", serializationResult.indexOf("xsi") > -1);
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Tests polymorphic list items, i. e. equally named elements of a list with different possible data types
     * which are all descendants of a single super type.
     */
    public void testPolymorphicListItems() {
        PolymorphicListItemDTO dto = new PolymorphicListItemDTO();
        SubDTOA subDTO = new SubDTOA();
        subDTO.element1 = "e1";
        subDTO.element2 = "e2";
        dto.elementList.add(subDTO);
        SubDTOB subDTO2 = new SubDTOB();
        subDTO2.element1 = "e3";
        subDTO2.element3 = "e4";
        dto.elementList.add(subDTO2);
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        assertTrue(serializationResult.indexOf("SubDTOBDataType") > -1);
        assertTrue(serializationResult.indexOf("SubDTOADataType") < serializationResult.indexOf("SubDTOBDataType"));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @XmlDataType()
    static final class PolymorphicElementDTO extends AbstractTestDTO {
        @XmlElement()
        SuperDTOType element;
    }

    @XmlDataType()
    static final class PolymorphicListItemDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<SuperDTOType> elementList = new ArrayList<SuperDTOType>();
    }

    @XmlDataType(subObjectTypes = {SuperDTO.class})
    interface SuperDTOType {

    }

    @XmlDataType(subObjectTypes = {SubDTOA.class, SubDTOB.class})
    abstract static class SuperDTO extends AbstractTestDTO implements SuperDTOType {
        @XmlElement()
        String element1;
    }

    @XmlDataType(name = "SubDTOADataType")
    static final class SubDTOA extends SuperDTO {
        @XmlElement()
        String element2;
    }

    @XmlDataType(name = "SubDTOBDataType")
    static final class SubDTOB extends SuperDTO {
        @XmlElement()
        String element3;
    }

}
