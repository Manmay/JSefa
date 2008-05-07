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
import static org.jsefa.test.rbf.ExistenceValidationOnDeserializationTest.Mode.INVALID;
import static org.jsefa.test.rbf.ExistenceValidationOnDeserializationTest.Mode.VALID;
import static org.jsefa.test.rbf.ExistenceValidationOnDeserializationTest.Mode.VALIDATION_OFF;

import java.util.List;

import junit.framework.TestCase;

import org.jsefa.DeserializationException;
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
 * Tests for testing deserialization with existence validation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class ExistenceValidationOnDeserializationTest extends TestCase {
    enum Mode {
        VALIDATION_OFF, VALID, INVALID,
    }

    /**
     * Tests validation for a simple field value (CSV).
     */
    public void testSimpleFieldCSV() {
        check(CSV, "valid;valid", SimpleFieldTestDTO.class, VALID);
        check(CSV, ";invalid", SimpleFieldTestDTO.class, INVALID);
        check(CSV, ";invalid", SimpleFieldTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a simple field value (FLR).
     */
    public void testSimpleFieldFLR() {
        check(FLR, "valid     valid     ", SimpleFieldTestDTO.class, VALID);
        check(FLR, "          invalid   ", SimpleFieldTestDTO.class, INVALID);
        check(FLR, "          invalid   ", SimpleFieldTestDTO.class, VALIDATION_OFF);
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
        check(CSV, "MR;foo" + "\n" + "SR;valid;", SubRecordTestDTO.class, VALID);
        check(CSV, "MR;foo" + "\n" + "SR;;valid", SubRecordTestDTO.class, VALID);
        check(CSV, "MR;foo", SubRecordTestDTO.class, INVALID);
        check(CSV, "MR;foo", SubRecordTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record (FLR).
     */
    public void testSubRecordFLR() {
        check(FLR, "MRfoo       " + "\n" + "SRvalid               ", SubRecordTestDTO.class, VALID);
        check(FLR, "MRfoo       " + "\n" + "SR     valid          ", SubRecordTestDTO.class, VALID);
        check(FLR, "MRfoo       ", SubRecordTestDTO.class, INVALID);
        check(FLR, "MRfoo       ", SubRecordTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record list (CSV).
     */
    public void testSubRecordListCSV() {
        check(CSV, "MR;foo" + "\n" + "LR;valid;", SubRecordListTestDTO.class, VALID);
        check(CSV, "MR;foo" + "\n" + "LR;;valid", SubRecordListTestDTO.class, VALID);
        check(CSV, "MR;foo", SubRecordListTestDTO.class, INVALID);
        check(CSV, "MR;foo", SubRecordListTestDTO.class, VALIDATION_OFF);
    }

    /**
     * Tests validation for a sub record list (FLR).
     */
    public void testSubRecordListFLR() {
        check(FLR, "MRfoo       " + "\n" + "LRvalid               ", SubRecordListTestDTO.class, VALID);
        check(FLR, "MRfoo       " + "\n" + "LR     valid          ", SubRecordListTestDTO.class, VALID);
        check(FLR, "MRfoo       ", SubRecordListTestDTO.class, INVALID);
        check(FLR, "MRfoo       ", SubRecordListTestDTO.class, VALIDATION_OFF);
    }

    @SuppressWarnings("unchecked")
    private void check(FormatType formatType, String inputString, Class<?> objectType, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(formatType);
        if (VALIDATION_OFF == mode) {
            config.setValidationMode(ValidationMode.NONE);
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
