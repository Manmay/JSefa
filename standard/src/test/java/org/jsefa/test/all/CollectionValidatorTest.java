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

package org.jsefa.test.all;

import static org.jsefa.test.all.ValidatorTestUtil.Mode.INVALID;
import static org.jsefa.test.all.ValidatorTestUtil.Mode.VALID;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.FLR;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.annotation.CsvSubRecordList;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.annotation.FlrSubRecordList;
import org.jsefa.rbf.annotation.Record;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil.FormatType;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;

/**
 * Tests for testing the validators.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CollectionValidatorTest extends TestCase {

    /**
     * Tests for collections (CSV).
     */
    public void testCollectionCSV() {
        checkForCollection(CSV);
    }

    /**
     * Tests for collections (FLR).
     */
    public void testCollectionFLR() {
        checkForCollection(FLR);
    }

    /**
     * Tests for collections (XML).
     */
    public void testCollectionXML() {
        checkForCollection(XML);
    }

    private void checkForCollection(FormatType formatType) {
        ListValidatorTestDTO dto = new ListValidatorTestDTO();
        dto.listField = new ArrayList<ComplexElementDTO>();
        ValidatorTestUtil.check(formatType, dto, INVALID);
        dto.listField.add(new ComplexElementDTO());
        ValidatorTestUtil.check(formatType, dto, VALID);
        dto.listField.add(new ComplexElementDTO());
        ValidatorTestUtil.check(formatType, dto, VALID);
        dto.listField.add(new ComplexElementDTO());
        ValidatorTestUtil.check(formatType, dto, INVALID);
    }

    @CsvDataType(defaultPrefix = "MR")
    @FlrDataType(defaultPrefix = "MR")
    @XmlDataType(defaultElementName = "a")
    static final class ListValidatorTestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        @XmlElement(name = "b")
        String stringField;

        @CsvSubRecordList(pos = 0, records = @Record(prefix = "LR"), constraints = {"min=1", "max=2"})
        @FlrSubRecordList(pos = 0, records = @Record(prefix = "LR"), constraints = {"min=1", "max=2"})
        @XmlElementList(name = "l", implicit = false, items = @ListItem(name = "i"), constraints = {"min=1",
                "max=2"})
        List<ComplexElementDTO> listField;
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType(defaultElementName = "a")
    static final class ComplexElementDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        @XmlElement(name = "f1")
        String stringField1;

        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement(name = "f2")
        String stringField2;
    }

}
