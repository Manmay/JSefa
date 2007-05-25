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

package org.jsefa.csv;

import static org.jsefa.JSefaTestUtil.FormatType.CSV;
import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.config.EscapeMode;
import org.jsefa.csv.config.QuoteMode;

/**
 * Tests to test the CSV serialization/deserialization with different quote mode.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class QuoteModeTest extends TestCase {
    /**
     * Test with mode "always" for the case the quotes are needed.
     */
    public void testQuoteAlways() {
        QuoteAlwaysDTO obj = new QuoteAlwaysDTO();
        obj.fieldA = "the ; field \"value \n with line break";
        obj.fieldB = obj.fieldA;

        CsvConfiguration config = createConfig(EscapeMode.DOUBLING);
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        assertTrue(serializationResult.indexOf("\"\"") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);

        config = createConfig(EscapeMode.ESCAPE_CHARACTER);
        obj.fieldA = "the ; field \"value";
        obj.fieldB = obj.fieldA;
        serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        assertFalse(serializationResult.indexOf("\"\"") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with mode "always" for the case the quotes are not needed.
     */
    public void testQuoteAlwaysButNotNeeded() {
        QuoteAlwaysDTO obj = new QuoteAlwaysDTO();
        obj.fieldA = "the field A";
        obj.fieldB = obj.fieldA;

        CsvConfiguration config = createConfig(EscapeMode.DOUBLING);
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);

        config = createConfig(EscapeMode.ESCAPE_CHARACTER);
        serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with mode "on demand" for the case the quotes are needed.
     */
    public void testOnDemandNeeded() {
        QuoteOnDemandDTO obj = new QuoteOnDemandDTO();
        obj.fieldA = "the ; field \"value \n with line break";
        obj.fieldB = obj.fieldA;

        CsvConfiguration config = createConfig(EscapeMode.DOUBLING);
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        assertTrue(serializationResult.indexOf("\"\"") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);

        config = createConfig(EscapeMode.ESCAPE_CHARACTER);
        obj.fieldA = "the ; field \"value";
        obj.fieldB = obj.fieldA;
        serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        assertFalse(serializationResult.indexOf("\"\"") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with mode "on demand" for the case the quotes are not needed.
     */
    public void testOnDemandNotNeeded() {
        QuoteOnDemandDTO obj = new QuoteOnDemandDTO();
        obj.fieldA = "the field A";
        obj.fieldB = obj.fieldA;

        CsvConfiguration config = createConfig(EscapeMode.DOUBLING);
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertFalse(serializationResult.charAt(0) == config.getQuoteCharacter());
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);

        config = createConfig(EscapeMode.ESCAPE_CHARACTER);
        serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertFalse(serializationResult.charAt(0) == config.getQuoteCharacter());
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    /**
     * Test with mode "default" where the default is set to "always" via the configuration.
     */
    public void testQuoteDefaultAsAlways() {
        QuoteDefaultDTO obj = new QuoteDefaultDTO();
        obj.fieldA = "the ; field \"value \n with line break";
        obj.fieldB = obj.fieldA;

        CsvConfiguration config = createConfig(QuoteMode.ALWAYS, EscapeMode.DOUBLING);
        String serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        assertTrue(serializationResult.indexOf("\"\"") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);

        config = createConfig(QuoteMode.ALWAYS, EscapeMode.ESCAPE_CHARACTER);
        obj.fieldA = "the ; field \"value";
        obj.fieldB = obj.fieldA;
        serializationResult = JSefaTestUtil.serialize(CSV, config, obj);
        assertTrue(serializationResult.charAt(0) == config.getQuoteCharacter());
        assertFalse(serializationResult.indexOf("\"\"") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, config, obj);
    }

    private CsvConfiguration createConfig(EscapeMode escapeMode) {
        CsvConfiguration config = new CsvConfiguration();
        config.setQuoteCharacterEscapeMode(escapeMode);
        return config;
    }
    
    private CsvConfiguration createConfig(QuoteMode defaultQuoteMode, EscapeMode escapeMode) {
        CsvConfiguration config = new CsvConfiguration();
        config.setDefaultQuoteMode(defaultQuoteMode);
        config.setQuoteCharacterEscapeMode(escapeMode);
        return config;
    }

    @CsvDataType()
    static final class QuoteAlwaysDTO extends AbstractTestDTO {
        @CsvField(pos = 1, quoteMode = QuoteMode.ALWAYS)
        String fieldA;

        @CsvField(pos = 2, quoteMode = QuoteMode.ALWAYS)
        String fieldB;
    }

    @CsvDataType()
    static final class QuoteOnDemandDTO extends AbstractTestDTO {
        @CsvField(pos = 1, quoteMode = QuoteMode.ON_DEMAND)
        String fieldA;

        @CsvField(pos = 2, quoteMode = QuoteMode.ON_DEMAND)
        String fieldB;
    }

    @CsvDataType()
    static final class QuoteDefaultDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        String fieldA;

        @CsvField(pos = 2)
        String fieldB;
    }

}
