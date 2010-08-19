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
import static org.jsefa.test.rbf.ExistenceValidationOnSerializationTest.Mode.INVALID;
import static org.jsefa.test.rbf.ExistenceValidationOnSerializationTest.Mode.VALID;
import static org.jsefa.test.rbf.ExistenceValidationOnSerializationTest.Mode.VALIDATION_OFF;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.SerializationException;
import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.ValidationMode;
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
 * Tests for testing serialization with existence validation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class ExistenceValidationOnSerializationTest extends TestCase {
    enum Mode {
        VALIDATION_OFF, VALID, INVALID,
    }

    /**
     * Tests validation for a simple field value (CSV).
     */
    public void testSimpleFieldCSV() {
        SimpleFieldTestDTO dto = new SimpleFieldTestDTO();
        dto.stringField1 = "valid";
        dto.stringField2 = "valid";
        check(CSV, dto, VALID);
        dto.stringField1 = null;
        check(CSV, dto, INVALID);
        check(CSV, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple field value (FLR).
     */
    public void testSimpleFieldFLR() {
        SimpleFieldTestDTO dto = new SimpleFieldTestDTO();
        dto.stringField1 = "valid";
        dto.stringField2 = "valid";
        check(FLR, dto, VALID);
        dto.stringField1 = null;
        check(FLR, dto, INVALID);
        check(FLR, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex field value (CSV).
     */
    public void testComplexFieldCSV() {
        ComplexElementTestDTO dto = new ComplexElementTestDTO();
        dto.stringField = "foo";
        dto.complexField = new ComplexElementDTO();
        dto.complexField.stringField1 = "valid";
        check(CSV, dto, VALID);
        dto.complexField.stringField1 = null;
        dto.complexField.stringField2 = "valid";
        check(CSV, dto, VALID);
        dto.complexField = null;
        check(CSV, dto, INVALID);
        check(CSV, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a complex field value (FLR).
     */
    public void testComplexFieldFLR() {
        ComplexElementTestDTO dto = new ComplexElementTestDTO();
        dto.stringField = "foo";
        dto.complexField = new ComplexElementDTO();
        dto.complexField.stringField1 = "valid";
        check(FLR, dto, VALID);
        dto.complexField.stringField1 = null;
        dto.complexField.stringField2 = "valid";
        check(FLR, dto, VALID);
        dto.complexField = null;
        check(FLR, dto, INVALID);
        check(FLR, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record (CSV).
     */
    public void testSubRecordCSV() {
        SubRecordTestDTO dto = new SubRecordTestDTO();
        dto.stringField = "foo";
        dto.complexField = new ComplexElementDTO();
        dto.complexField.stringField1 = "valid";
        check(CSV, dto, VALID);
        dto.complexField.stringField1 = null;
        dto.complexField.stringField2 = "valid";
        check(CSV, dto, VALID);
        dto.complexField = null;
        check(CSV, dto, INVALID);
        check(CSV, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record (FLR).
     */
    public void testSubRecordFLR() {
        SubRecordTestDTO dto = new SubRecordTestDTO();
        dto.stringField = "foo";
        dto.complexField = new ComplexElementDTO();
        dto.complexField.stringField1 = "valid";
        check(FLR, dto, VALID);
        dto.complexField.stringField1 = null;
        dto.complexField.stringField2 = "valid";
        check(FLR, dto, VALID);
        dto.complexField = null;
        check(FLR, dto, INVALID);
        check(FLR, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record list (CSV).
     */
    public void testSubRecordListCSV() {
        SubRecordListTestDTO dto = new SubRecordListTestDTO();
        dto.stringField = "foo";
        ComplexElementDTO itemDTO =  new ComplexElementDTO();
        itemDTO.stringField1 = "valid";
        dto.listField = new ArrayList<ComplexElementDTO>();
        dto.listField.add(itemDTO);
        check(CSV, dto, VALID);

        itemDTO.stringField1 = null;
        itemDTO.stringField2 = "valid";
        check(CSV, dto, VALID);
        dto.listField = null;
        check(CSV, dto, INVALID);
        check(CSV, dto, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record list (FLR).
     */
    public void testSubRecordListFLR() {
        SubRecordListTestDTO dto = new SubRecordListTestDTO();
        dto.stringField = "foo";
        ComplexElementDTO itemDTO =  new ComplexElementDTO();
        itemDTO.stringField1 = "valid";
        dto.listField = new ArrayList<ComplexElementDTO>();
        dto.listField.add(itemDTO);
        check(CSV, dto, VALID);

        itemDTO.stringField1 = null;
        itemDTO.stringField2 = "valid";
        check(FLR, dto, VALID);
        dto.listField = null;
        check(FLR, dto, INVALID);
        check(FLR, dto, VALIDATION_OFF);
    }

    @SuppressWarnings("unchecked")
    private void check(FormatType formatType, Object value, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(formatType);
        if (VALIDATION_OFF == mode) {
            config.setValidationMode(ValidationMode.NONE);
        }
        try {
            JSefaTestUtil.serialize(formatType, config, value);
            assertFalse(mode == INVALID);
        } catch (SerializationException e) {
            assertTrue(mode == INVALID);
        }
    }

    @CsvDataType()
    @FlrDataType()
    static final class SimpleFieldTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0, required = true)
        @FlrField(pos = 0, length = 10, required = true)
        String stringField1;

        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String stringField2;
}

    @CsvDataType()
    @FlrDataType()
    static final class ComplexElementTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0, required = true)
        @FlrField(pos = 0, required = true)
        ComplexElementDTO complexField;

        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String stringField;
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
    }

    @CsvDataType(defaultPrefix = "MR")
    @FlrDataType(defaultPrefix = "MR")
    static final class SubRecordTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        String stringField;

        @CsvSubRecord(pos = 0, prefix = "SR", required = true)
        @FlrSubRecord(pos = 0, prefix = "SR", required = true)
        ComplexElementDTO complexField;
    }

    @CsvDataType(defaultPrefix = "MR")
    @FlrDataType(defaultPrefix = "MR")
    static final class SubRecordListTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        String stringField;

        @CsvSubRecordList(pos = 0, records = @Record(prefix = "LR"), required = true)
        @FlrSubRecordList(pos = 0, records = @Record(prefix = "LR"), required = true)
        List<ComplexElementDTO> listField;
    }

}
