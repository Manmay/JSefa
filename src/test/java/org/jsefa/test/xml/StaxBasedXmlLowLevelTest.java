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

import java.io.StringReader;

import junit.framework.TestCase;

import org.jsefa.xml.lowlevel.StaxBasedXmlLowLevelDeserializer;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.lowlevel.model.TextContent;
import org.jsefa.xml.lowlevel.model.XmlItemType;

/**
 * Tests to test the correct behaviour of the stax based low level implementation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class StaxBasedXmlLowLevelTest extends TestCase {

    /**
     * Tests the handling of consecutive character events.
     */
    public void testConsecutiveCharacters() {
        final String testXml = "<test>http://www.yahoo.com?a=1&amp;b=2&amp;c=3&amp;d=4</test>";
        final String testContent = "http://www.yahoo.com?a=1&b=2&c=3&d=4";

        StaxBasedXmlLowLevelDeserializer deserializer = new StaxBasedXmlLowLevelDeserializer(
                new XmlLowLevelConfiguration());
        deserializer.open(new StringReader(testXml));
        while (deserializer.hasNext()) {
            deserializer.moveToNext();
            if (deserializer.currentType() == XmlItemType.TEXT_CONTENT) {
                TextContent textContent = (TextContent) deserializer.current();
                assertEquals(testContent, textContent.getText());
            }
        }
        deserializer.close(true);
    }
}
