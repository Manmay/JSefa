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

import java.io.StringReader;

import junit.framework.TestCase;

import org.jsefa.csv.lowlevel.CsvLowLevelDeserializer;
import org.jsefa.csv.lowlevel.CsvLowLevelIOFactory;
import org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration;
import org.jsefa.csv.lowlevel.config.QuoteMode;

/**
 * Tests to test the CSV deserialization with respect to correct handling at the end of a line.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class EndOfLineTest extends TestCase {

    /**
     * Test one line with mode "no delimiter after last field" for the case the last field is not empty.
     */
    public void testOneLineNoEmptyLastFieldWithoutDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;b", false);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        deserializer.close(true);
    }

    /**
     * Test two lines with mode "no delimiter after last field" for the case the last field is not empty.
     */
    public void testTwoLinesNoEmptyLastFieldWithoutDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;b\na;b", false);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertFalse(deserializer.readNextRecord());

        deserializer.close(true);
    }

    /**
     * Test one line with mode "no delimiter after last field" for the case the last field is empty.
     */
    public void testOneLineEmptyLastFieldWithoutDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;", false);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        deserializer.close(true);
    }

    /**
     * Test two lines with mode "no delimiter after last field" for the case the last field is empty.
     */
    public void testTwoLinesEmptyLastFieldWithoutDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;\na;", false);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertFalse(deserializer.readNextRecord());

        deserializer.close(true);
    }

    /**
     * Test one line with mode "delimiter after last field" for the case the last field is not empty.
     */
    public void testOneLineNoEmptyLastFieldWithDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;b;", true);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertFalse(deserializer.readNextRecord());

        deserializer.close(true);
    }

    /**
     * Test two lines with mode "delimiter after last field" for the case the last field is not empty.
     */
    public void testTwoLinesNoEmptyLastFieldWithDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;b;\na;b;", true);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertFalse(deserializer.readNextRecord());

        deserializer.close(true);
    }

    /**
     * Test one line with mode "delimiter after last field" for the case the last field is empty.
     */
    public void testOneLineEmptyLastFieldWithDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;b;;", true);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertFalse(deserializer.readNextRecord());

        deserializer.close(true);
    }

    /**
     * Test two lines with mode "delimiter after last field" for the case the last field is empty.
     */
    public void testTwoLinesEmptyLastFieldWithDelimiter() {
        CsvLowLevelDeserializer deserializer = createAndOpenDeserializer("a;b;;\na;b;;", true);

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertTrue(deserializer.readNextRecord());
        assertEquals("a", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("b", deserializer.nextField(QuoteMode.NEVER));
        assertEquals("", deserializer.nextField(QuoteMode.NEVER));
        assertNull(deserializer.nextField(QuoteMode.NEVER));

        assertFalse(deserializer.readNextRecord());

        deserializer.close(true);
    }

    private CsvLowLevelDeserializer createAndOpenDeserializer(String input, boolean useDelimiterAfterLastField) {
        CsvLowLevelConfiguration config = new CsvLowLevelConfiguration();
        config.setUseDelimiterAfterLastField(useDelimiterAfterLastField);
        CsvLowLevelDeserializer deserializer = CsvLowLevelIOFactory.createFactory(config).createDeserializer();
        deserializer.open(new StringReader(input));
        return deserializer;
    }

}
