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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import junit.framework.TestCase;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.annotation.CsvSubRecordList;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.annotation.FlrSubRecordList;
import org.jsefa.rbf.annotation.Record;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;

/**
 * Tests to test sub record lists and simple list fields with a non list java field type.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CollectionsTest extends TestCase {

    /**
     * Tests a sub record list for a field of type <code>Set</code> (CSV).
     */
    public void testForSetCSV() {
        SubRecordListWithSetFieldDTO dto = createSubRecordListWithSetFieldDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests a sub record list for a field of type <code>Set</code> (FLR).
     */
    public void testForSetFLR() {
        SubRecordListWithSetFieldDTO dto = createSubRecordListWithSetFieldDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }

    /**
     * Tests a sub record list for a field of type <code>Queue</code> (CSV).
     */
    public void testForQueueCSV() {
        SubRecordListWithSetFieldDTO dto = createSubRecordListWithQueueFieldDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, dto);
    }

    /**
     * Tests a sub record list for a field of type <code>Queue</code> (FLR).
     */
    public void testForQueueFLR() {
        SubRecordListWithSetFieldDTO dto = createSubRecordListWithQueueFieldDTO();
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, dto);
    }
    
    /**
     * Tests a simple list for a field of type <code>Set</code> (CSV).
     */
    public void testSimpleListForSet() {
        SimpleListWithSetField list = new SimpleListWithSetField();
        list.set = new HashSet<String>();
        for (int i = 0; i < 10; i++) {
            list.set.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, list);
    }
    
    /**
     * Tests a simple list for a field of type <code>Queue</code> (CSV).
     */
    public void testSimpleListForQueue() {
        SimpleListWithQueueField list = new SimpleListWithQueueField();
        list.queue = new LinkedList<String>();
        for (int i = 0; i < 10; i++) {
            list.queue.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, list);
    }

    private SubRecordListWithSetFieldDTO createSubRecordListWithSetFieldDTO() {
        SubRecordListWithSetFieldDTO dto = new SubRecordListWithSetFieldDTO();
        dto.fieldA = "fieldA content";
        dto.subRecordListField.add(createSimpleDTO("simpleField content1"));
        dto.subRecordListField.add(createSimpleDTO("simpleField content2"));
        return dto;
    }
    
    private SubRecordListWithSetFieldDTO createSubRecordListWithQueueFieldDTO() {
        SubRecordListWithSetFieldDTO dto = new SubRecordListWithSetFieldDTO();
        dto.fieldA = "fieldA content";
        dto.subRecordListField.add(createSimpleDTO("simpleField content1"));
        dto.subRecordListField.add(createSimpleDTO("simpleField content2"));
        return dto;
    }

    private SimpleDTO createSimpleDTO(String content) {
        SimpleDTO obj = new SimpleDTO();
        obj.simpleField = content;
        return obj;
    }

    @CsvDataType(defaultPrefix = "LT")
    @FlrDataType(defaultPrefix = "LT")
    static class SubRecordListWithSetFieldDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 25)
        String fieldA;

        @CsvSubRecordList(pos = 2, records = {@Record(prefix = "ST", objectType = SimpleDTO.class)})
        @FlrSubRecordList(pos = 2, records = {@Record(prefix = "ST")})
        Set<SimpleDTO> subRecordListField = new HashSet<SimpleDTO>();
    }
    
    @CsvDataType(defaultPrefix = "LT")
    @FlrDataType(defaultPrefix = "LT")
    static class SubRecordListWithQueueFieldDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 25)
        String fieldA;

        @CsvSubRecordList(pos = 2, records = {@Record(prefix = "ST", objectType = SimpleDTO.class)})
        @FlrSubRecordList(pos = 2, records = {@Record(prefix = "ST")})
        Queue<SimpleDTO> subRecordListField = new LinkedList<SimpleDTO>();
    }

    @CsvDataType()
    @FlrDataType()
    static class SimpleDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 25)
        String simpleField;
    }
    
    @CsvDataType()
    static final class SimpleListWithSetField extends AbstractTestDTO {
        @CsvField(pos = 1)
        Set<String> set;
    }

    @CsvDataType()
    static final class SimpleListWithQueueField extends AbstractTestDTO {
        @CsvField(pos = 1)
        Queue<String> queue;
    }
    
    
    
}
