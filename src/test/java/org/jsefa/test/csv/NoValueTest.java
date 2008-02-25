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

package org.jsefa.test.csv;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import junit.framework.TestCase;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;

/**
 * Tests to test the CSV serialization/deserialization with different no value strings.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class NoValueTest extends TestCase {
    /**
     * Test with default no value string which is configured to be the empty string.
     */
    public void testDefaultAsEmpty() {
        DefaultNoValueStringDTO obj = new DefaultNoValueStringDTO();
        obj.fieldA = "not null";
        obj.fieldB = null;

        CsvConfiguration config = createConfig();
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertEquals("not null;\n", serializationResult);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with default no value string which is configured to be the "NULL" string.
     */
    public void testDefaultAsNotEmpty() {
        DefaultNoValueStringDTO obj = new DefaultNoValueStringDTO();
        obj.fieldA = "not null";
        obj.fieldB = null;

        CsvConfiguration config = createConfig("NULL");
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertEquals("not null;NULL\n", serializationResult);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with the empty string as an explicit no value string.
     */
    public void testExplicitAsEmpty() {
        ExplicitEmptyNoValueStringDTO obj = new ExplicitEmptyNoValueStringDTO();
        obj.fieldA = "not null";
        obj.fieldB = null;

        CsvConfiguration config = createConfig();
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertEquals("not null;\n", serializationResult);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with "NULL" as an explicit no value string.
     */
    public void testExplicitAsNotEmpty() {
        ExplicitNotEmptyNoValueStringDTO obj = new ExplicitNotEmptyNoValueStringDTO();
        obj.fieldA = "not null";
        obj.fieldB = null;

        CsvConfiguration config = createConfig();
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertEquals("not null;NULL\n", serializationResult);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    private CsvConfiguration createConfig(String noValueString) {
        CsvConfiguration config = new CsvConfiguration();
        config.setDefaultNoValueString(noValueString);
        return config;
    }

    private CsvConfiguration createConfig() {
        return new CsvConfiguration();
    }

    @CsvDataType()
    static final class DefaultNoValueStringDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        String fieldA;

        @CsvField(pos = 2)
        String fieldB;
    }

    @CsvDataType()
    static final class ExplicitEmptyNoValueStringDTO extends AbstractTestDTO {
        @CsvField(pos = 1, noValue = "")
        String fieldA;

        @CsvField(pos = 2, noValue = "")
        String fieldB;
    }

    @CsvDataType()
    static final class ExplicitNotEmptyNoValueStringDTO extends AbstractTestDTO {
        @CsvField(pos = 1, noValue = "NULL")
        String fieldA;

        @CsvField(pos = 2, noValue = "NULL")
        String fieldB;
    }

}
