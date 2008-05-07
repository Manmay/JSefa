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
import static org.jsefa.test.xml.ValueValidationOnSerializationTest.Mode.INVALID;
import static org.jsefa.test.xml.ValueValidationOnSerializationTest.Mode.VALID;
import static org.jsefa.test.xml.ValueValidationOnSerializationTest.Mode.VALIDATION_OFF;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.SerializationException;
import org.jsefa.common.annotation.SimpleListItem;
import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests for testing serialization with value validation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class ValueValidationOnSerializationTest extends TestCase {
    enum Mode {
        VALIDATION_OFF, VALID, INVALID,
    }

    /**
     * Tests validation for an attribute value.
     */
    public void testAttribute() {
        AttributeTestDTO dto = new AttributeTestDTO();
        dto.stringField = "valid";
        check(dto, VALID);
        dto.stringField = "not valid";
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple element value.
     */
    public void testSimpleElement() {
        SimpleElementTestDTO dto = new SimpleElementTestDTO();
        dto.stringField = "valid";
        check(dto, VALID);
        dto.stringField = "not valid";
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple element value with list type.
     */
    public void testSimpleElementWithListType() {
        SimpleElementWithListTypeTestDTO dto = new SimpleElementWithListTypeTestDTO();
        dto.stringList = new ArrayList<String>();
        dto.stringList.add("valid");
        check(dto, VALID);
        dto.stringList.add("not valid");
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a text content of an element.
     */
    public void testTextContent() {
        TextContentTestDTO dto = new TextContentTestDTO();
        dto.stringField = "valid";
        check(dto, VALID);
        dto.stringField = "not valid";
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple list item value.
     */
    public void testExplicitSimpleListItem() {
        ExplicitSimpleListItemTestDTO dto = new ExplicitSimpleListItemTestDTO();
        dto.stringList = new ArrayList<String>();
        dto.stringList.add("valid");
        check(dto, VALID);
        dto.stringList.add("not valid");
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple list item value.
     */
    public void testImplicitSimpleListItem() {
        ImplicitSimpleListItemTestDTO dto = new ImplicitSimpleListItemTestDTO();
        dto.stringList = new ArrayList<String>();
        dto.stringList.add("valid");
        check(dto, VALID);
        dto.stringList.add("not valid");
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex element value.
     */
    public void testComplexElement() {
        ComplexElementTestDTO dto = new ComplexElementTestDTO();
        dto.complexField = new ComplexElementDTO();
        dto.complexField.stringField1 = "valid";
        check(dto, VALID);
        dto.complexField.stringField1 = null;
        dto.complexField.stringField2 = "valid";
        check(dto, VALID);
        dto.complexField.stringField2 = null;
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a top level complex element value.
     */
    public void testTopLevelComplexElement() {
        ComplexElementDTO dto = new ComplexElementDTO();
        dto.stringField1 = "valid";
        check(dto, VALID);
        dto.stringField1 = null;
        dto.stringField2 = "valid";
        check(dto, VALID);
        dto.stringField2 = null;
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex element value.
     */
    public void testImplicitComplexListItem() {
        ImplicitComplexListItemTestDTO dto = new ImplicitComplexListItemTestDTO();
        dto.listField = new ArrayList<ComplexElementDTO>();
        ComplexElementDTO itemDTO = new ComplexElementDTO();
        itemDTO.stringField1 = "valid";
        dto.listField.add(itemDTO);
        check(dto, VALID);
        itemDTO.stringField1 = null;
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex element value.
     */
    public void testExplicitComplexListItem() {
        ExplicitComplexListItemTestDTO dto = new ExplicitComplexListItemTestDTO();
        dto.listField = new ArrayList<ComplexElementDTO>();
        ComplexElementDTO itemDTO = new ComplexElementDTO();
        itemDTO.stringField1 = "valid";
        dto.listField.add(itemDTO);
        check(dto, VALID);
        itemDTO.stringField1 = null;
        check(dto, INVALID);
        check(dto, VALIDATION_OFF);
    }

    @SuppressWarnings("unchecked")
    private void check(Object value, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(XML);
        if (VALIDATION_OFF == mode) {
            config.setValidationMode(ValidationMode.NONE);
        } else {
            config.getValidatorProvider().registerValidatorType(ComplexElementDTO.class,
                    ComplexElementDTOValidator.class);
        }
        try {
            JSefaTestUtil.serialize(XML, config, value);
            assertFalse(mode == INVALID);
        } catch (SerializationException e) {
            assertTrue(mode == INVALID);
        }
    }

    @XmlDataType(defaultElementName = "a")
    static final class AttributeTestDTO extends AbstractTestDTO {
        @XmlAttribute(name = "b", constraints = "pattern=\\w*")
        String stringField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class SimpleElementTestDTO extends AbstractTestDTO {
        @XmlElement(name = "b", constraints = "pattern=\\w*")
        String stringField;
    }
    
    @XmlDataType(defaultElementName = "a")
    static final class SimpleElementWithListTypeTestDTO extends AbstractTestDTO {
        @XmlElement(name = "b", listItem = @SimpleListItem(constraints = "pattern=\\w*"))
        List<String> stringList;
    }

    @XmlDataType(defaultElementName = "a")
    static final class TextContentTestDTO extends AbstractTestDTO {
        @XmlTextContent(constraints = "pattern=\\w*")
        String stringField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ExplicitSimpleListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(name = "b", implicit = false, items = @ListItem(name = "i", constraints = "pattern=\\w*"))
        List<String> stringList;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ImplicitSimpleListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = @ListItem(name = "i", constraints = "pattern=\\w*"))
        List<String> stringList;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ComplexElementTestDTO extends AbstractTestDTO {
        @XmlElement(name = "b")
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
        @XmlElementList(name = "b", implicit = false, items = @ListItem(name = "i"))
        List<ComplexElementDTO> listField;
    }

    @XmlDataType(defaultElementName = "a")
    static final class ImplicitComplexListItemTestDTO extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = @ListItem(name = "i"))
        List<ComplexElementDTO> listField;
    }

    private static final class ComplexElementDTOValidator implements Validator {

        static ComplexElementDTOValidator create() {
            return new ComplexElementDTOValidator();
        }

        public ValidationResult validate(Object value) {
            ComplexElementDTO dto = (ComplexElementDTO) value;
            if (dto.stringField1 == null && dto.stringField2 == null) {
                return ValidationResult.create(ValidationError.create("foo", "Both fields are null"));
            } else {
                return ValidationResult.VALID;
            }
        }

    }

}
