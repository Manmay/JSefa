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

package org.jsefa.test.rbf;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.FLR;
import static org.jsefa.test.rbf.ValueValidationOnDeserializationTest.Mode.INVALID;
import static org.jsefa.test.rbf.ValueValidationOnDeserializationTest.Mode.VALID;
import static org.jsefa.test.rbf.ValueValidationOnDeserializationTest.Mode.VALIDATION_OFF;

import java.util.List;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
import org.jsefa.common.annotation.SimpleListItem;
import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.annotation.CsvSubRecord;
import org.jsefa.csv.annotation.CsvSubRecordList;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.annotation.FlrSubRecord;
import org.jsefa.flr.annotation.FlrSubRecordList;
import org.jsefa.rbf.annotation.Record;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;

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
     * Tests validation for a simple field value (CSV).
     */
    public void testSimpleFieldCSV() {
        check(CSV, "valid", SimpleFieldTestDTO.class, VALID);
        check(CSV, "not valid", SimpleFieldTestDTO.class, INVALID);
        check(CSV, "not valid", SimpleFieldTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple field value (FLR).
     */
    public void testSimpleFieldFLR() {
        check(FLR, "valid     ", SimpleFieldTestDTO.class, VALID);
        check(FLR, "not valid ", SimpleFieldTestDTO.class, INVALID);
        check(FLR, "not valid ", SimpleFieldTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple element value with list type (CSV).
     */
    public void testSimpleFieldWithListTypeCSV() {
        check(CSV, "valid", SimpleFieldWithListTypeTestDTO.class, VALID);
        check(CSV, "not valid", SimpleFieldWithListTypeTestDTO.class, INVALID);
        check(CSV, "not valid", SimpleFieldWithListTypeTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex field value (CSV).
     */
    public void testComplexFieldCSV() {
        check(CSV, "valid;;foo", ComplexElementTestDTO.class, VALID);
        check(CSV, ";valid;foo", ComplexElementTestDTO.class, VALID);
        check(CSV, ";;foo", ComplexElementTestDTO.class, INVALID);
        check(CSV, ";;foo", ComplexElementTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex field value (FLR).
     */
    public void testComplexFieldFLR() {
        check(FLR, "valid               foo       ", ComplexElementTestDTO.class, VALID);
        check(FLR, "          valid     foo       ", ComplexElementTestDTO.class, VALID);
        check(FLR, "                    foo       ", ComplexElementTestDTO.class, INVALID);
        check(FLR, "                    foo       ", ComplexElementTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record (CSV).
     */
    public void testSubRecordCSV() {
        check(CSV, "MR;foo" + "\n" + "SR;valid;;bar", SubRecordTestDTO.class, VALID);
        check(CSV, "MR;foo" + "\n" + "SR;;valid;bar", SubRecordTestDTO.class, VALID);
        check(CSV, "MR;foo" + "\n" + "SR;;;bar", SubRecordTestDTO.class, INVALID);
        check(CSV, "MR;foo" + "\n" + "SR;;;bar", SubRecordTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record (FLR).
     */
    public void testSubRecordFLR() {
        check(FLR, "MRfoo       " + "\n" + "SRvalid               bar       ", SubRecordTestDTO.class, VALID);
        check(FLR, "MRfoo       " + "\n" + "SR     valid          bar       ", SubRecordTestDTO.class, VALID);
        check(FLR, "MRfoo       " + "\n" + "SR                    bar       ", SubRecordTestDTO.class, INVALID);
        check(FLR, "MRfoo       " + "\n" + "SR                    bar       ", SubRecordTestDTO.class,
                VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record list (CSV).
     */
    public void testSubRecordListCSV() {
        check(CSV, "MR;foo" + "\n" + "LR;valid;;bar", SubRecordListTestDTO.class, VALID);
        check(CSV, "MR;foo" + "\n" + "LR;;valid;bar", SubRecordListTestDTO.class, VALID);
        check(CSV, "MR;foo" + "\n" + "LR;;;bar", SubRecordListTestDTO.class, INVALID);
        check(CSV, "MR;foo" + "\n" + "LR;;;bar", SubRecordListTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record list (FLR).
     */
    public void testSubRecordListFLR() {
        check(FLR, "MRfoo       " + "\n" + "LRvalid               bar       ", SubRecordListTestDTO.class, VALID);
        check(FLR, "MRfoo       " + "\n" + "LR     valid          bar       ", SubRecordListTestDTO.class, VALID);
        check(FLR, "MRfoo       " + "\n" + "LR                    bar       ", SubRecordListTestDTO.class, INVALID);
        check(FLR, "MRfoo       " + "\n" + "LR                    bar       ", SubRecordListTestDTO.class,
                VALIDATION_OFF);
    }

    @SuppressWarnings("unchecked")
    private void check(FormatType formatType, String inputString, Class<?> objectType, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(formatType);
        if (VALIDATION_OFF == mode) {
            config.setValidationMode(ValidationMode.NONE);
        } else {
            config.getValidatorProvider().registerValidatorType(ComplexElementDTO.class,
                    ComplexElementDTOValidator.class);
        }
        try {
            JSefaTestUtil.deserialize(formatType, config, inputString, objectType);
            assertFalse(mode == INVALID);
        } catch (DeserializationException e) {
            assertTrue(mode == INVALID);
        }
    }

    @CsvDataType()
    @FlrDataType()
    static final class SimpleFieldTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0, constraints = "pattern=\\w*")
        @FlrField(pos = 0, length = 10, constraints = "pattern=\\w*")
        String stringField;
    }

    @CsvDataType()
    @FlrDataType()
    static final class SimpleFieldWithListTypeTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0, listItem = @SimpleListItem(constraints = "pattern=\\w*"))
        List<String> stringList;
    }

    @CsvDataType()
    @FlrDataType()
    static final class ComplexElementTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0)
        ComplexElementDTO complexField;
    }

    @CsvDataType()
    @FlrDataType()
    static final class ComplexElementDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        String stringField1;

        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String stringField2;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String stringField3;
    }

    @CsvDataType(defaultPrefix = "MR")
    @FlrDataType(defaultPrefix = "MR")
    static final class SubRecordTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        String stringField;

        @CsvSubRecord(pos = 0, prefix = "SR")
        @FlrSubRecord(pos = 0, prefix = "SR")
        ComplexElementDTO complexField;
    }

    @CsvDataType(defaultPrefix = "MR")
    @FlrDataType(defaultPrefix = "MR")
    static final class SubRecordListTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        String stringField;

        @CsvSubRecordList(pos = 0, records = @Record(prefix = "LR"))
        @FlrSubRecordList(pos = 0, records = @Record(prefix = "LR"))
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
