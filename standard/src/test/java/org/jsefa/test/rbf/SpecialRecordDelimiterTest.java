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

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.jsefa.IOFactory;
import org.jsefa.common.lowlevel.filter.FilterResult;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.common.lowlevel.filter.LineFilter;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.config.FlrConfiguration;
import org.jsefa.rbf.RbfDeserializer;
import org.jsefa.rbf.RbfSerializer;
import org.jsefa.rbf.config.RbfConfiguration;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;

/**
 * Tests to test the RBF serialization/deserialization using a special record delimiter.
 * 
 * @author Norman Lahme-Huetig
 */
public class SpecialRecordDelimiterTest extends TestCase {

    /**
     * Tests a single line with multiple records (CSV).
     */
    public void testRecordsOnlyCSV() {
        checkRecordsOnly(CSV);
    }

    /**
     * Tests a single line with multiple records (FLR).
     */
    public void testRecordsOnlyFLR() {
        checkRecordsOnly(FLR);
    }

    /**
     * Tests multiple records with a header (CSV).
     */
    public void testWithHeaderCSV() {
        checkWithHeaderAndFooter(CSV, 3, false);
    }

    /**
     * Tests multiple records with a header (FLR).
     */
    public void testWithHeaderFLR() {
        checkWithHeaderAndFooter(FLR, 3, false);
    }

    /**
     * Tests multiple records with a header and footer (CSV).
     */
    public void testWithHeaderAndFooterCSV() {
        checkWithHeaderAndFooter(CSV, 3, true);
    }

    /**
     * Tests multiple records with a header and footer (FLR).
     */
    public void testWithHeaderAndFooterFLR() {
        checkWithHeaderAndFooter(FLR, 3, true);
    }

    @SuppressWarnings("unchecked")
    private void checkRecordsOnly(FormatType formatType) {
        RbfConfiguration config;
        if (formatType == FLR) {
            config = new FlrConfiguration();
        } else {
            config = new CsvConfiguration();
        }
        config.setSpecialRecordDelimiter('?');
        config.setLineFilter(new ExpectingTruncatedLinesFilter());
        Object[] testDTOs = createTestDTOs("FIELDA", "FIELDB", 100);
        String serializationResult = JSefaTestUtil.serialize(formatType, config, testDTOs);
        // check if we have only one line
        assertTrue(serializationResult.indexOf("\n") < 0);
        // check if we have the expected number of special record delimiters
        assertEquals(countChar(serializationResult, config.getSpecialRecordDelimiter()), 100);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(formatType, config, testDTOs);
    }

    @SuppressWarnings("unchecked")
    private void checkWithHeaderAndFooter(FormatType formatType, int headerSize, boolean hasFooter) {
        RbfConfiguration config;
        if (formatType == FLR) {
            config = new FlrConfiguration();
        } else {
            config = new CsvConfiguration();
        }
        config.setSpecialRecordDelimiter('?');
        config.setLineFilter(new HeaderAndFooterFilter(headerSize, hasFooter, true));

        StringWriter writer = new StringWriter();
        IOFactory factory = JSefaTestUtil.createIOFactory(formatType, config, TestDTO.class);
        RbfSerializer serializer = (RbfSerializer) factory.createSerializer();
        serializer.open(writer);

        for (int i = 0; i < headerSize; i++) {
            serializer.getLowLevelSerializer().writeLine("HEADER " + i);
        }
        Object[] testDTOs = createTestDTOs("FIELDA", "FIELDB", 100);
        for (int i = 0; i < testDTOs.length; i++) {
            serializer.write(testDTOs[i]);
        }
        if (hasFooter) {
            serializer.getLowLevelSerializer().writeLine("");
            serializer.getLowLevelSerializer().writeLine("FOOTER");
        }
        serializer.close(true);

        RbfDeserializer deserializer = (RbfDeserializer) factory.createDeserializer();
        deserializer.open(new StringReader(writer.toString()));
        int count = 0;
        while (deserializer.hasNext()) {
            TestDTO dto = deserializer.next();
            assertEquals(dto, testDTOs[count++]);
        }
        assertEquals(count, testDTOs.length);
        assertEquals(deserializer.getStoredLines().size(), hasFooter ? headerSize + 1 : headerSize);
    }

    private TestDTO[] createTestDTOs(String fieldA, String fieldB, int count) {
        TestDTO[] result = new TestDTO[count];
        for (int i = 0; i < count; i++) {
            result[i] = createTestDTO(fieldA, fieldB);
        }
        return result;
    }

    private int countChar(String value, char character) {
        int count = 0;
        int index = 0;
        index = value.indexOf(character);
        while (index >= 0) {
            count++;
            index = value.indexOf(character, index + 1);
        }
        return count;
    }

    private TestDTO createTestDTO(String fieldA, String fieldB) {
        TestDTO dto = new TestDTO();
        dto.fieldA = fieldA;
        dto.fieldB = fieldB;
        return dto;
    }
    
    static final class ExpectingTruncatedLinesFilter implements LineFilter {

        public FilterResult filter(String content, int lineNumber, boolean truncated, boolean lastLine) {
            TestCase.assertTrue(truncated);
            return FilterResult.PASSED;
        }
        
    }

    @CsvDataType()
    @FlrDataType()
    static final class TestDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;
    }
}
