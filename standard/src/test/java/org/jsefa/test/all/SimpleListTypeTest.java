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

import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.common.annotation.SimpleListItem;
import org.jsefa.common.converter.DateConverter;
import org.jsefa.common.converter.SimpleTypeConverterConfiguration;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of simple list types.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class SimpleListTypeTest extends TestCase {

    /**
     * Tests list of date objects with default format (XML).
     */
    public void testDateListXML() {
        checkDateList(XML);
    }

    /**
     * Tests list of date objects with default format (CSV).
     */
    public void testDateListCSV() {
        checkDateList(CSV);
    }

    /**
     * Tests list of date objects with explicitly given format (XML).
     */
    public void testDateListWithExplicitFormatXML() {
        checkDateListWithExplicitFormat(XML);
    }

    /**
     * Tests list of date objects with explicitly given format (CSV).
     */
    public void testDateListWithExplicitFormatCSV() {
        checkDateListWithExplicitFormat(CSV);
    }
    
    private void checkDateList(FormatType formatType) {
        DateListDTO dto = new DateListDTO();
        dto.listField = new ArrayList<Date>();
        dto.listField.add(createDate("01.01.2008"));
        dto.listField.add(createDate("01.02.2008"));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(formatType, dto);
        assertTrue(JSefaTestUtil.serialize(formatType, dto).contains(","));
    }

    private void checkDateListWithExplicitFormat(FormatType formatType) {
        DateListWithExplicitFormatDTO dto = new DateListWithExplicitFormatDTO();
        dto.listField = new ArrayList<Date>();
        dto.listField.add(createDate("01.01.2008"));
        dto.listField.add(createDate("01.02.2008"));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(formatType, dto);
        assertTrue(JSefaTestUtil.serialize(formatType, dto).contains("@"));
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class DateListDTO extends AbstractTestDTO {
        @XmlElement()
        @CsvField(pos = 1)
        List<Date> listField;
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class DateListWithExplicitFormatDTO extends AbstractTestDTO {
        @XmlElement(format = "@", listItem = @SimpleListItem(format = "dd.MM.yyyy"))
        @CsvField(pos = 1, format = "@", listItem = @SimpleListItem(format = "dd.MM.yyyy"))
        List<Date> listField;
    }

    private Date createDate(String date) {
        return DateConverter.create(SimpleTypeConverterConfiguration.EMPTY).fromString(date);
    }

}
