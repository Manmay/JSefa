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

package org.jsefa.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.sql.rowset.spi.XmlReader;

import junit.framework.TestCase;

/**
 * Tests to test the {@link XmlReader} character set encoding detection feature.
 * 
 * @author lahme
 * 
 */
public class XmlReaderFactoryTest extends TestCase {
    private static final String TEST_TEXT = "abc\u00E4\u00F6\u00FC\u00DF";
    
    /**
     * Tests for correct detection of ISO-8859-1 character set encoding.
     */
    public void testISO8859() {
        checkForEncoding("ISO-8859-1");
    }

    /**
     * Tests for correct detection of UTF-16 big-endian character set encoding.
     */
    public void testUTF16BE() {
        checkForEncoding("UTF-16BE");
    }
    
    /**
     * Tests for correct detection of UTF-16 little-endian character set encoding.
     */
    public void testUTF16LE() {
        checkForEncoding("UTF-16LE");
    }

    /**
     * Tests for correct detection of UTF-16 character set encoding.
     */
    public void testUTF16() {
        checkForEncoding("UTF-16");
    }

    /**
     * Tests for correct detection of UTF-8 character set encoding.
     */
    public void testUTF8() {
        checkForEncoding("UTF-8");
    }

    private void checkForEncoding(String encoding) {
        writeTestFile(encoding, TEST_TEXT);
        String fileContent = readTestFile();
        assertTrue(fileContent.indexOf(TEST_TEXT) != -1);
    }

    private void writeTestFile(String encoding, String text) {
        File file = new File(System.getProperty("java.io.tmpdir"), "xml-test-file.xml");
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
            writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>}n");
            writer.write("<root>" + text + "</root>");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readTestFile() {
        StringBuilder result = new StringBuilder();
        File file = new File(System.getProperty("java.io.tmpdir"), "xml-test-file.xml");
        BufferedReader reader = new BufferedReader(XmlReaderFactory.create(file));
        try {
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                result.append("\n");
                line = reader.readLine();
            }
            reader.close();
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
