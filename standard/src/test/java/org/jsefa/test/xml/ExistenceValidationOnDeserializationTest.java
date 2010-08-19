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
import static org.jsefa.test.xml.ExistenceValidationOnDeserializationTest.Mode.INVALID;
import static org.jsefa.test.xml.ExistenceValidationOnDeserializationTest.Mode.VALID;
import static org.jsefa.test.xml.ExistenceValidationOnDeserializationTest.Mode.VALIDATION_OFF;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.MapKey;
import org.jsefa.xml.annotation.MapValue;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlElementMap;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests for testing deserialization with existence validation.
 * 
 * @author Norman Lahme-Huetig
 */
public class ExistenceValidationOnDeserializationTest extends TestCase {
    enum Mode {
        VALIDATION_OFF, VALID, INVALID,
    }

    /**
     * Tests validation for an attribute value.
     */
    public void testAttribute() {
        check("<a b='valid'>content</a>", AttributeTestDTO.class, VALID);
        check("<a>not valid</a>", AttributeTestDTO.class, INVALID);
        check("<a>not valid</a>", AttributeTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple element value.
     */
    public void testSimpleElement() {
        check("<a><b>valid</b></a>", SimpleElementTestDTO.class, VALID);
        check("<a>not valid</a>", SimpleElementTestDTO.class, INVALID);
        check("<a>not valid</a>", SimpleElementTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a text content of an element.
     */
    public void testTextContent() {
        check("<a>1</a>", TextContentTestDTO.class, VALID);
        check("<a></a>", TextContentTestDTO.class, INVALID);
        check("<a></a>", TextContentTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex element value.
     */
    public void testComplexElement() {
        check("<a><b><f1>valid</f1></b></a>", ComplexElementTestDTO.class, VALID);
        check("<a><b><f2>valid</f2></b></a>", ComplexElementTestDTO.class, VALID);
        check("<a></a>", ComplexElementTestDTO.class, INVALID);
        check("<a></a>", ComplexElementTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple list item value.
     */
    public void testExplicitSimpleListItem() {
        check("<a><b><i>valid</i></b></a>", ExplicitSimpleListItemTestDTO.class, VALID);
        check("<a>not valid</a>", ExplicitSimpleListItemTestDTO.class, INVALID);
        check("<a>not valid</a>", ExplicitSimpleListItemTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a implicit simple list item value.
     */
    public void testImplicitSimpleListItem() {
        check("<a><i>valid</i></a>", ImplicitSimpleListItemTestDTO.class, VALID);
        check("<a>not valid</a>", ImplicitSimpleListItemTestDTO.class, INVALID);
        check("<a>not valid</a>", ImplicitSimpleListItemTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for an implicit list of complex elements.
     */
    public void testImplicitComplexListItem() {
        check("<a><i><f1>valid</f1></i></a>", ImplicitComplexListItemTestDTO.class, VALID);
        check("<a><i><f2>valid</f2></i></a>", ImplicitComplexListItemTestDTO.class, VALID);
        check("<a></a>", ImplicitComplexListItemTestDTO.class, INVALID);
        check("<a></a>", ImplicitComplexListItemTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for an explicit list of complex elements.
     */
    public void testExplicitComplexListItem() {
        check("<a><b><i><f1>valid</f1></i></b></a>", ExplicitComplexListItemTestDTO.class, VALID);
        check("<a><b><i><f2>valid</f2></i></b></a>", ExplicitComplexListItemTestDTO.class, VALID);
        check("<a></a>", ExplicitComplexListItemTestDTO.class, INVALID);
        check("<a></a>", ExplicitComplexListItemTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple map.
     */
    public void testExplicitSimpleMap() {
        check("<a><b><v k='x'>valid</v></b></a>", ExplicitSimpleMapTestDTO.class, VALID);
        check("<a>not valid</a>", ExplicitSimpleMapTestDTO.class, INVALID);
        check("<a>not valid</a>", ExplicitSimpleMapTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple map.
     */
    public void testImplicitSimpleMap() {
        check("<a><v k='x'>valid</v></a>", ImplicitSimpleMapTestDTO.class, VALID);
        check("<a>not valid</a>", ImplicitSimpleMapTestDTO.class, INVALID);
        check("<a>not valid</a>", ImplicitSimpleMapTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for an implicit map of complex elements.
     */
    public void testImplicitComplexMap() {
        check("<a><v k='1'><f1>valid</f1></v></a>", ImplicitComplexMapTestDTO.class, VALID);
        check("<a><v k='1'><f2>valid</f2></v></a>", ImplicitComplexMapTestDTO.class, VALID);
        check("<a></a>", ImplicitComplexMapTestDTO.class, INVALID);
        check("<a></a>", ImplicitComplexMapTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for an explicit map of complex elements.
     */
    public void testExplicitComplexMap() {
        check("<a><b><v k='1'><f1>valid</f1></v></b></a>", ExplicitComplexMapTestDTO.class, VALID);
        check("<a><b><v k='1'><f2>valid</f2></v></b></a>", ExplicitComplexMapTestDTO.class, VALID);
        check("<a></a>", ExplicitComplexMapTestDTO.class, INVALID);
        check("<a></a>", ExplicitComplexMapTestDTO.class, VALIDATION_OFF);
    }

    @SuppressWarnings("unchecked")
    private void check(String inputString, Class<?> objectType, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(XML);
        if (VALIDATION_OFF == mode) {
            config.setValidationMode(ValidationMode.NONE);
        }
        try {
            JSefaTestUtil.deserialize(XML, config, inputString, objectType);
            assertFalse(mode == INVALID);
        } catch (DeserializationException e) {
            assertTrue(mode == INVALID);
        }
    }

    @XmlDataType(defaultElementName = "a")
    static final class AttributeTestDTO extends AbstractTestDTO {
        @XmlAttribute(name = "b", required = true)
        String stringField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class SimpleElementTestDTO extends AbstractTestDTO {
        @XmlElement(name = "b", required = true)
        String stringField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class TextContentTestDTO extends AbstractTestDTO {
        @XmlTextContent(required = true)
        Long longField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ExplicitSimpleListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(name = "b", implicit = false, items = @ListItem(name = "i"), required = true)
        List<String> stringList;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ImplicitSimpleListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = @ListItem(name = "i"), required = true)
        List<String> stringList;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ExplicitSimpleMapTestDTO extends AbstractTestDTO {
        @XmlElementMap(name = "b", implicit = false, key = @MapKey(name = "k"), values = @MapValue(name = "v"),
                required = true)
        Map<String, String> stringMap;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ImplicitSimpleMapTestDTO extends AbstractTestDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "k"), values = @MapValue(name = "v"), required = true)
        Map<String, String> stringMap;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ComplexElementTestDTO extends AbstractTestDTO {
        @XmlElement(name = "b", required = true)
        ComplexElementDTO complexField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ComplexElementDTO extends AbstractTestDTO {
        @XmlElement(name = "f1")
        String stringField1;

        @XmlElement(name = "f2")
        String stringField2;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ExplicitComplexListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(name = "b", implicit = false, items = @ListItem(name = "i"), required = true)
        List<ComplexElementDTO> listField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ImplicitComplexListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = @ListItem(name = "i"), required = true)
        List<ComplexElementDTO> listField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ExplicitComplexMapTestDTO extends AbstractTestDTO {
        @XmlElementMap(name = "b", implicit = false, key = @MapKey(name = "k"), values = @MapValue(name = "v"),
                required = true)
        Map<String, ComplexElementDTO> mapField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ImplicitComplexMapTestDTO extends AbstractTestDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "k"), values = @MapValue(name = "v"), required = true)
        Map<String, ComplexElementDTO> mapField;
    }

}
