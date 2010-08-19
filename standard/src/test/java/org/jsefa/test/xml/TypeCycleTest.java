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

import java.util.List;

import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;

/**
 * Tests to test the correct serialization/deserialization when a cycle in the type mapping graph exists.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class TypeCycleTest extends TestCase {

    /**
     * Tests a cycle in the type mapping graph where the revisited type has subtypes which have not been mapped at the
     * point of cycle detection.
     */
    public void testPolymorphicCycle() {
        TestDTO dto = new TestDTO();
        TypeADTO typeDTO = new TypeADTO();
        typeDTO.id = "id";
        typeDTO.label = "label";
        dto.type = typeDTO;
        System.out.println(JSefaTestUtil.serialize(XML, dto));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }

    @XmlDataType
    private static class TestDTO extends AbstractTestDTO {
        @XmlElement
        TypeDTO type;
    }

    @XmlDataType(subObjectTypes = { TypeADTO.class, TypeBDTO.class })
    private static class TypeDTO extends AbstractTestDTO {
        @XmlElement
        String id;
    }

    @XmlDataType(name = "TypeA")
    private static class TypeADTO extends TypeDTO {
        @XmlElement
        String label;

        @XmlElement
        TypeContainerDTO typeContainer;

    }

    @XmlDataType(name = "TypeB")
    private static class TypeBDTO extends TypeDTO {
        @XmlElement
        String label;

        @XmlElement
        TypeContainerDTO typeContainer;

    }

    @XmlDataType(name = "TypeContainer")
    private static class TypeContainerDTO extends AbstractTestDTO {
        @XmlElement
        TypeDTO type;
        @XmlElementList(items=@ListItem(name="list-type-entry"))
        List<TypeDTO> typeList;
    }

}
