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

import org.jsefa.IOFactoryException;
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
     * Test with the following characteristics:<br>
     * 1. complex element which is top level (i. e. for which an entry point exists)<br>
     * 2. the type of the element is abstract (mapped to an interface) and has subtypes<p>
     * Expectations:<br>
     * The element instance gets an xsi:type attribute
     */
    @SuppressWarnings("unchecked")
    public void testElementWithAbstractType() {
        SuperDTOType dto = createSubDTOA();
        String serializationResult = JSefaTestUtil.serialize(XML, SuperDTOType.class, dto);
        assertTrue("Default preferred prefix xsi not found", serializationResult.indexOf("xsi") > -1);
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        assertEquals(dto, JSefaTestUtil.deserialize(XML, serializationResult, SuperDTOType.class));
    }
    
    /**
     * Test with the following characteristics:<br>
     * 1. complex element which is top level (i. e. for which an entry point exists)<br>
     * 2. the type of the element is concrete (mapped to a concrete class) and has subtypes<p>
     * 3. the type of the element instance is the same as type of the element
     * Expectations:<br>
     * The element instance gets no xsi:type attribute
     */
    @SuppressWarnings("unchecked")
    public void testElementForInstanceWithSameType() {
        SuperDTO dto = createSuperDTO();
        String serializationResult = JSefaTestUtil.serialize(XML, SuperDTO.class, dto);
        assertFalse("Default preferred prefix xsi found", serializationResult.indexOf("xsi") > -1);
        assertFalse(serializationResult.indexOf("SuperDTODataType") > -1);
        assertEquals(dto, JSefaTestUtil.deserialize(XML, serializationResult, SuperDTO.class));
    }
    
    /**
     * Test with the following characteristics:<br>
     * 1. complex element which is a sub element of a top level element<br>
     * 2. the type of the element is abstract (mapped to an interface) and has subtypes<p>
     * Expectations:<br>
     * The element instance gets an xsi:type attribute
     */
    public void testSingleSubelementWithAbstractType() {
        SingleSubelementWithAbstractTypeDTO dto = new SingleSubelementWithAbstractTypeDTO();
        dto.complexElement = createSubDTOA();
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue("Default preferred prefix xsi not found", serializationResult.indexOf("xsi") > -1);
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }
    
    /**
     * Test with the following characteristics:<br>
     * 1. complex element which is a sub element of a top level element<br>
     * 2. the type of the element is concrete (mapped to a concrete class) and has subtypes<p>
     * 3. the type of the element instance is the same as type of the element
     * Expectations:<br>
     * The element instance gets no xsi:type attribute
     */
    public void testSingleSubelementForInstanceWithSameType() {
        SingleSubelementWithConcreteTypeDTO dto = new SingleSubelementWithConcreteTypeDTO();
        dto.complexElement = createSuperDTO();
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertFalse("Default preferred prefix xsi found", serializationResult.indexOf("xsi") > -1);
        assertFalse(serializationResult.indexOf("SuperDTODataType") > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
        
    }

    /**
     * Test with the following characteristics:<br>
     * 1. multiple complex elements which are sub elements of a top level element<br>
     * 2. the different type of the elements do not have subtypes
     * 3. the elements have the same name
     * Expectations:<br>
     * Each element instance gets an xsi:type attribute
     */
    public void testMultipleSubelementsWithLeafTypes() {
        MultipleSubelementsWithDifferentLeafTypesDTO dto = new MultipleSubelementsWithDifferentLeafTypesDTO();
        dto.complexElementA = createSubDTOA();
        dto.complexElementB = createSubDTOB();
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue("Default preferred prefix xsi not found", serializationResult.indexOf("xsi") > -1);
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        assertTrue(serializationResult.indexOf("SubDTOBDataType") > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }
    
    /**
     * Test with the following characteristics:<br>
     * 1. multiple complex elements which are sub elements of a top level element<br>
     * 2. each element as the same type
     * 3. the elements have the same name
     * Expectations:<br>
     * An IOFactoryException is thrown as the name of an element together with its type is ambiguous
     */
    public void testExceptionOnMultipleSubelementsWithSameConcreteTypeDTO() {
        MultipleSubelementsWithSameConcreteTypeDTO dto = new MultipleSubelementsWithSameConcreteTypeDTO();
        dto.complexElementA = createSubDTOA();
        dto.complexElementB = createSubDTOB();
        try {
            JSefaTestUtil.serialize(XML, dto);
            fail("Expected IOFactoryException");
        } catch (IOFactoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test with the following characteristics:<br>
     * 1. multiple complex elements which are sub elements of a top level element<br>
     * 2. the type of each element is a subtype or supertype of the types of the other elements
     * 3. the elements have the same name
     * Expectations:<br>
     * An IOFactoryException is thrown as the name of an element together with its type is ambiguous
     */
    public void testExceptionOnMultipleSubelementsWithInterferingTypeDTO() {
        MultipleSubelementsWithInterferingTypesDTO dto = new MultipleSubelementsWithInterferingTypesDTO();
        dto.complexElement = createSubDTOA();
        dto.complexElementA = createSubDTOA();
        try {
            JSefaTestUtil.serialize(XML, dto);
            fail("Expected IOFactoryException");
        } catch (IOFactoryException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test with the following characteristics:<br>
     * 1. complex elements which are items of element list<br>
     * 2. the type of the elements is abstract (mapped to an interface) and has subtypes<p>
     * Expectations:<br>
     * Each element instance gets an xsi:type attribute
     */
    public void testListItemsWithAbstractType() {
        ListItemsWithAbstractTypeDTO dto = new ListItemsWithAbstractTypeDTO();
        dto.listElement = new ArrayList<SuperDTOType>();
        dto.listElement.add(createSubDTOA());
        dto.listElement.add(createSubDTOB());
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertTrue("Default preferred prefix xsi not found", serializationResult.indexOf("xsi") > -1);
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        assertTrue(serializationResult.indexOf("SubDTOBDataType") > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    /**
     * Test with the following characteristics:<br>
     * 1. complex elements which are items of element list<br>
     * 2. the type of the elements is concrete (mapped to a concrete class) and has subtypes<p>
     * 3. only one element instance has the same type as the element
     * Expectations:<br>
     * The one element instance gets no xsi:type attribute but the others do
     */
    public void testListItemsForFirstInstanceWithSameType() {
        ListItemsWithConcreteTypeDTO dto = new ListItemsWithConcreteTypeDTO();
        dto.listElement = new ArrayList<SuperDTO>();
        dto.listElement.add(createSuperDTO());
        dto.listElement.add(createSubDTOA());
        dto.listElement.add(createSubDTOB());
        String serializationResult = JSefaTestUtil.serialize(XML, dto);
        assertEquals(2, countOccurrences(serializationResult, "xsi:type"));
        assertTrue(serializationResult.indexOf("SubDTOADataType") > -1);
        assertTrue(serializationResult.indexOf("SubDTOBDataType") > -1);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }
    
    private int countOccurrences(String text, String subText) {
        int pos = -1;
        int counter = 0;
        do  {
            pos++;
            pos = text.indexOf(subText, pos);
            counter++;
        } while (pos >= 0);
        return counter - 1;
    }
    
    private SuperDTO createSuperDTO() {
        SuperDTO dto = new SuperDTO();
        dto.element = "SuperDTO.element";
        return dto;
    }

    private SubDTOA createSubDTOA() {
        SubDTOA dto = new SubDTOA();
        dto.element = "SubDTOA.element";
        dto.elementA = "SubDTOA.elementA";
        return dto;
    }

    private SubDTOB createSubDTOB() {
        SubDTOB dto = new SubDTOB();
        dto.element = "SubDTOA.element";
        dto.elementB = "SubDTOA.elementB";
        return dto;
    }
    
    @XmlDataType
    static class ListItemsWithConcreteTypeDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<SuperDTO> listElement;
    }

    @XmlDataType
    static class ListItemsWithAbstractTypeDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = {@ListItem(name = "item")})
        List<SuperDTOType> listElement;
    }

    @XmlDataType
    static class SingleSubelementWithConcreteTypeDTO extends AbstractTestDTO {
        @XmlElement
        SuperDTO complexElement;
    }

    @XmlDataType
    static class SingleSubelementWithAbstractTypeDTO extends AbstractTestDTO {
        @XmlElement
        SuperDTOType complexElement;
    }

    @XmlDataType
    static class MultipleSubelementsWithDifferentLeafTypesDTO extends AbstractTestDTO {
        @XmlElement(name = "complexElement")
        SubDTOA complexElementA;

        @XmlElement(name = "complexElement")
        SubDTOB complexElementB;
    }

    @XmlDataType
    static class MultipleSubelementsWithSameConcreteTypeDTO extends AbstractTestDTO {
        @XmlElement(name = "complexElement")
        SuperDTO complexElementA;

        @XmlElement(name = "complexElement")
        SuperDTO complexElementB;
    }

    @XmlDataType
    static class MultipleSubelementsWithInterferingTypesDTO extends AbstractTestDTO {
        @XmlElement(name = "complexElement")
        SuperDTO complexElement;

        @XmlElement(name = "complexElement")
        SubDTOA complexElementA;
    }

    @XmlDataType(subObjectTypes = {SuperDTO.class})
    interface SuperDTOType {

    }

    @XmlDataType(name = "SuperDTODataType", subObjectTypes = {SubDTOA.class, SubDTOB.class})
    static class SuperDTO extends AbstractTestDTO implements SuperDTOType {
        @XmlElement()
        String element;
    }

    @XmlDataType(name = "SubDTOADataType")
    static final class SubDTOA extends SuperDTO {
        @XmlElement()
        String elementA;
    }

    @XmlDataType(name = "SubDTOBDataType")
    static final class SubDTOB extends SuperDTO {
        @XmlElement()
        String elementB;
    }

}
