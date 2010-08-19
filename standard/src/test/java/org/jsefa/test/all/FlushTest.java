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

import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.TestCase;

import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.Serializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests for testing the flush operation (Serializer).
 * @author Norman Lahme-Huetig
 *
 */
public class FlushTest extends TestCase {
    
    /**
     * Tests it for XML.
     */
    public void testXML() {
        check(XmlIOFactory.createFactory(TestDTO.class));
    }
    
    /**
     * Tests it for CSV.
     */
    public void testCSV() {
        check(CsvIOFactory.createFactory(TestDTO.class));
    }

    /**
     * Tests it for FLR.
     */
    public void testFLR() {
        check(FlrIOFactory.createFactory(TestDTO.class));
    }

    private void check(IOFactory factory) {
        Serializer serializer = factory.createSerializer();
        Deserializer deserializer = factory.createDeserializer();

        StringWriter stringWriter = new StringWriter();
        Writer writer = new BufferedWriter(stringWriter);
        serializer.open(writer);
        serializer.write(createTestDTO("a", "b"));
        TestCase.assertTrue(stringWriter.toString().length() == 0);
        serializer.flush();
        assertIsComplete(stringWriter.toString(), deserializer);
        serializer.close(true);
        
    }
    
    private void assertIsComplete(String doc, Deserializer deserializer) {
        deserializer.open(new StringReader(doc));
        while (deserializer.hasNext()) {
            deserializer.next();
        }
        deserializer.close(true);
    }
    
    
    private TestDTO createTestDTO(String fieldA, String fieldB) {
        TestDTO dto = new TestDTO();
        dto.fieldA = fieldA;
        dto.fieldB = fieldB;
        return dto;
    }

    @CsvDataType()
    @FlrDataType()
    @XmlDataType()
    static final class TestDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement(pos = 1)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement(pos = 2)
        String fieldB;
    }

}
