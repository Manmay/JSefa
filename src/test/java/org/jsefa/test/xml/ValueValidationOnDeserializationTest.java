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
import static org.jsefa.test.xml.ValueValidationOnDeserializationTest.Mode.INVALID;
import static org.jsefa.test.xml.ValueValidationOnDeserializationTest.Mode.VALID;
import static org.jsefa.test.xml.ValueValidationOnDeserializationTest.Mode.VALIDATION_OFF;

import java.util.List;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
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
 * Tests for testing deserialization with value validation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class ValueValidationOnDeserializationTest extends TestCase {
    enum Mode {
        VALIDATION_OFF, VALID, INVALID,
    }

    /**
     * Tests validation for an attribute value.
     */
    public void testAttribute() {
        check("<a b='valid'>content</a>", AttributeTestDTO.class, VALID);
        check("<a b='not valid'>content</a>", AttributeTestDTO.class, INVALID);
        check("<a b='not valid'>content</a>", AttributeTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple element value.
     */
    public void testSimpleElement() {
        check("<a><b>valid</b></a>", SimpleElementTestDTO.class, VALID);
        check("<a><b>not valid</b></a>", SimpleElementTestDTO.class, INVALID);
        check("<a><b>not valid</b></a>", SimpleElementTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple element value with list type.
     */
    public void testSimpleElementWithListType() {
        check("<a><b>valid</b></a>", SimpleElementWithListTypeTestDTO.class, VALID);
        check("<a><b>not valid</b></a>", SimpleElementWithListTypeTestDTO.class, INVALID);
        check("<a><b>not valid</b></a>", SimpleElementWithListTypeTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a text content of an element.
     */
    public void testTextContent() {
        check("<a>valid</a>", TextContentTestDTO.class, VALID);
        check("<a>not valid</a>", TextContentTestDTO.class, INVALID);
        check("<a>not valid</a>", TextContentTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple list item value.
     */
    public void testExplicitSimpleListItem() {
        check("<a><b><i>valid</i></b></a>", ExplicitSimpleListItemTestDTO.class, VALID);
        check("<a><b><i>not valid</i></b></a>", ExplicitSimpleListItemTestDTO.class, INVALID);
        check("<a><b><i>not valid</i></b></a>", ExplicitSimpleListItemTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a explicit simple list item value.
     */
    public void testImplicitSimpleListItem() {
        check("<a><i>valid</i></a>", ImplicitSimpleListItemTestDTO.class, VALID);
        check("<a><i>not valid</i></a>", ImplicitSimpleListItemTestDTO.class, INVALID);
        check("<a><i>not valid</i></a>", ImplicitSimpleListItemTestDTO.class, VALIDATION_OFF);
    }
    
    /**
     * Tests validation for a complex element value.
     */
    public void testComplexElement() {
        check("<a><b><f1>valid</f1></b></a>", ComplexElementTestDTO.class, VALID);
        check("<a><b><f2>valid</f2></b></a>", ComplexElementTestDTO.class, VALID);
        check("<a><b>not valid</b></a>", ComplexElementTestDTO.class, INVALID);
        check("<a><b>not valid</b></a>", ComplexElementTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a top level complex element value.
     */
    public void testTopLevelComplexElement() {
        check("<a><f1>valid</f1></a>", ComplexElementDTO.class, VALID);
        check("<a><f2>valid</f2></a>", ComplexElementDTO.class, VALID);
        check("<a>not valid</a>", ComplexElementDTO.class, INVALID);
        check("<a>not valid</a>", ComplexElementDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex element value.
     */
    public void testImplicitComplexListItem() {
        check("<a><i><f1>valid</f1></i></a>", ImplicitComplexListItemTestDTO.class, VALID);
        check("<a><i><f2>valid</f2></i></a>", ImplicitComplexListItemTestDTO.class, VALID);
        check("<a><i>not valid</i></a>", ImplicitComplexListItemTestDTO.class, INVALID);
        check("<a><i>not valid</i></a>", ImplicitComplexListItemTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex element value.
     */
    public void testExplicitComplexListItem() {
        check("<a><b><i><f1>valid</f1></i></b></a>", ExplicitComplexListItemTestDTO.class, VALID);
        check("<a><b><i><f2>valid</f2></i></b></a>", ExplicitComplexListItemTestDTO.class, VALID);
        check("<a><b><i>not valid</i></b></a>", ExplicitComplexListItemTestDTO.class, INVALID);
        check("<a><b><i>not valid</i></b></a>", ExplicitComplexListItemTestDTO.class, VALIDATION_OFF);
    }

    @SuppressWarnings("unchecked")
    private void check(String inputString, Class<?> objectType, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(XML);
        if (VALIDATION_OFF == mode) {
            config.setValidationMode(ValidationMode.NONE);
        } else {
            config.getValidatorProvider().registerValidatorType(ComplexElementDTO.class,
                    ComplexElementDTOValidator.class);
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
