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

package org.jsefa.test.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.URI;

import junit.framework.TestCase;

import org.jsefa.xml.XmlDeserializer;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the correct resolution of DTDs.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class DtdResolutionTest extends TestCase {
    private static final String XML_FILE_1 = "dtd-test.xml";
    private static final String XML_FILE_2 = "dtd-test2.xml";
    
    /**
     * Tests DTD resolution without using entity references.
     */
    public void testWithoutEnityReferences() {
        XmlDeserializer deserializer = XmlIOFactory.createFactory(Person.class).createDeserializer();
        deserializer.open(createReader(XML_FILE_1), getURI(XML_FILE_1).toString());
        while (deserializer.hasNext()) {
            Person p = deserializer.next();
            assertNotNull(p);
            assertNotNull(p.name);
            assertTrue(p.name.length() > 0);
        }
        deserializer.close(true);
    }
    
    /**
     * Tests DTD resolution with using entity references.
     */
    public void testWithEnityReferences() {
        XmlDeserializer deserializer = XmlIOFactory.createFactory(Person.class).createDeserializer();
        deserializer.open(createReader(XML_FILE_2), getURI(XML_FILE_2).toString());
        while (deserializer.hasNext()) {
            Person p = deserializer.next();
            assertNotNull(p);
            assertNotNull(p.name);
            assertTrue(p.name.length() > 0);
            assertEquals("Schmidt", p.name);
        }
        deserializer.close(true);
    }

    private Reader createReader(String xmlFileName) {
        try {
            return new FileReader(new File(getURI(xmlFileName)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private URI getURI(String xmlFileName) {
        return new File("src/test/java/org/jsefa/test/xml", xmlFileName).toURI();
    }
    
    @XmlDataType
    static class Person {
        @XmlElement
        String name;
    }
}
