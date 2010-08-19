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
package org.jsefa.test.io;

import java.io.StringReader;

import junit.framework.TestCase;

import org.jsefa.common.lowlevel.io.LineSegment;
import org.jsefa.common.lowlevel.io.LineSegmentReader;

import static org.jsefa.common.lowlevel.io.LineSegment.Terminator;
import static org.jsefa.common.lowlevel.io.LineSegment.Terminator.*;

/**
 * Tests for testing the {@link LineSegmentReader}.
 * 
 * @author Norman Lahme-Huetig
 */
public class LineSegmentReaderTest extends TestCase {

    /**
     * Tests the reading of complete lines.
     * @throws Exception ignore
     */
    public void testLines() throws Exception {
        String input = "a\nab\nabc";
        LineSegmentReader reader = new LineSegmentReader(new StringReader(input));
        check(reader.read(), "a", 1, 1, LINE_BREAK);
        check(reader.read(), "ab", 2, 1, LINE_BREAK);
        check(reader.read(), "abc", 3, 1, EOS);
        reader.close();
    }

    /**
     * Tests the reading of lines with limited size.
     * @throws Exception ignore
     */
    public void testLimitedLines() throws Exception {
        String input = "ab\nabcd\nabcde\na\n";
        LineSegmentReader reader = new LineSegmentReader(new StringReader(input));
        check(reader.read(-1, 2), "ab", 1, 1, LINE_BREAK);
        check(reader.read(-1, 2), "ab", 2, 1, NONE);
        reader.skipLine();
        check(reader.read(-1, 2), "ab", 3, 1, NONE);
        reader.skipLine();
        check(reader.read(-1, 2), "a", 4, 1, LINE_BREAK);
        reader.close();
    }
    
    /**
     * Tests the mark and reset operation when reading complete lines.
     * @throws Exception ignore
     */
    public void testMarkOnLines() throws Exception {
        String input = "a\nab\nabc";
        LineSegmentReader reader = new LineSegmentReader(new StringReader(input));
        check(reader.read(), "a", 1, 1, LINE_BREAK);
        reader.mark();
        check(reader.read(), "ab", 2, 1, LINE_BREAK);
        reader.reset(true);
        check(reader.read(), "ab", 2, 1, LINE_BREAK);
        check(reader.read(), "abc", 3, 1, EOS);
        reader.close();
    }
    
    /**
     * Tests the reading of segments with a special terminator.
     * @throws Exception ignore
     */
    public void testWithSpecialTerminator() throws Exception {
        String input = "a?ab?abc\na?ab?abc";
        LineSegmentReader reader = new LineSegmentReader(new StringReader(input));
        check(reader.read('?', -1), "a", 1, 1, SPECIAL_CHARACTER);
        check(reader.read('?', -1), "ab", 1, 3, SPECIAL_CHARACTER);
        check(reader.read('?', -1), "abc", 1, 6, LINE_BREAK);

        check(reader.read('?', -1), "a", 2, 1, SPECIAL_CHARACTER);
        check(reader.read('?', -1), "ab", 2, 3, SPECIAL_CHARACTER);
        check(reader.read('?', -1), "abc", 2, 6, EOS);
    }

    /**
     * Tests the mark and reset operation when reading segments with a special terminator.
     * @throws Exception ignore
     */
    public void testMarkOnSegmentsWithSpecialTerminator() throws Exception {
        String input = "a?ab?abc\n";
        LineSegmentReader reader = new LineSegmentReader(new StringReader(input));
        check(reader.read('?', -1), "a", 1, 1, SPECIAL_CHARACTER);
        reader.mark();
        check(reader.read('?', -1), "ab", 1, 3, SPECIAL_CHARACTER);
        reader.reset(true);
        check(reader.read('?', -1), "ab", 1, 3, SPECIAL_CHARACTER);
        check(reader.read('?', -1), "abc", 1, 6, LINE_BREAK);
    }

    private void check(LineSegment segment, String expectedContent, int expectedLineNumber, int expectedColumnNumber,
            Terminator expectedTerminator) {
        assertNotNull(segment);
        assertEquals(expectedContent, segment.getContent());
        assertEquals(expectedLineNumber, segment.getLineNumber());
        assertEquals(expectedColumnNumber, segment.getColumnNumber());
        assertEquals(expectedTerminator, segment.getTerminator());
    }

}
