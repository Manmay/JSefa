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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

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

/**
 * Tests to test the FLR serialization/deserialization with sub records.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class SubRecordTest extends TestCase {
    /**
     * Tests a DTO with a single sub record (CSV).
     */
    public void testSingleSubRecordCSV() {
        SingleSubRecordDTO dto = createSingleSubRecordDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests a DTO with a single sub record (FLR).
     */
    public void testSingleSubRecordFLR() {
        SingleSubRecordDTO dto = createSingleSubRecordDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests a DTO with a single sub record list (CSV).
     */
    public void testSingleSubRecordListCSV() {
        SingleSubRecordListDTO dto = createSingleSubRecordListDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests a DTO with a single sub record list using the short annotation
     * syntax (FLR).
     */
    public void testSingleSubRecordListFLR() {
        SingleSubRecordListDTO dto = createSingleSubRecordListDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    private SimpleDTO createSimpleDTO(String content) {
        SimpleDTO obj = new SimpleDTO();
        obj.simpleField = content;
        return obj;
    }

    private SingleSubRecordDTO createSingleSubRecordDTO() {
        SingleSubRecordDTO obj = new SingleSubRecordDTO();
        obj.fieldA = "contentA";
        obj.subRecordField = createSimpleDTO("contentB");
        return obj;
    }

    private SingleSubRecordListDTO createSingleSubRecordListDTO() {
        SingleSubRecordListDTO dto = new SingleSubRecordListDTO();
        dto.fieldA = "fieldA content";
        dto.subRecordListField.add(createSimpleDTO("simpleField content1"));
        dto.subRecordListField.add(createSimpleDTO("simpleField content2"));
        return dto;
    }

    @CsvDataType(defaultPrefix = "LT")
    @FlrDataType(defaultPrefix = "LT")
    static class SingleSubRecordListDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 25)
        String fieldA;

        @CsvSubRecordList(pos = 2, records = { @Record(prefix = "ST", objectType = SimpleDTO.class) })
        @FlrSubRecordList(pos = 2, records = { @Record(prefix = "ST") })
        List<SimpleDTO> subRecordListField = new ArrayList<SimpleDTO>();

    }

    @CsvDataType(defaultPrefix = "RT")
    @FlrDataType(defaultPrefix = "RT")
    static class SingleSubRecordDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 25)
        String fieldA;

        @CsvSubRecord(pos = 2, prefix = "ST")
        @FlrSubRecord(pos = 2, prefix = "ST")
        SimpleDTO subRecordField;

    }

    @CsvDataType()
    @FlrDataType()
    static class SimpleDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 25)
        String simpleField;
    }

}
