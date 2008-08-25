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

import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;
import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.config.XmlConfiguration;

/**
 * Tests to test correct serialization of line breaks
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class LineBreakTest extends TestCase {
    
    /**
     * Tests serialization/deserialization with "\n" as a line break
     */
    public static void testLineFeed() {
        XmlConfiguration config = new XmlConfiguration();
        config.setLineBreak("\n");
        TestDTO dto = new TestDTO();
        dto.field="content";
        String serializationResult = JSefaTestUtil.serialize(XML, config, dto);
        assertTrue(serializationResult.indexOf(">\n") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, config, dto);
    }

    /**
     * Tests serialization/deserialization with "\r\n" as a line break
     */
    public static void testCarriageReturnLineFeed() {
        XmlConfiguration config = new XmlConfiguration();
        config.setLineBreak("\r\n");
        TestDTO dto = new TestDTO();
        dto.field="content";
        String serializationResult = JSefaTestUtil.serialize(XML, config, dto);
        assertTrue(serializationResult.indexOf(">\r\n") > 0);
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, config, dto);
    }

    @XmlDataType()
    static class TestDTO extends AbstractTestDTO {
        @XmlElement()
        String field;
    }    
}
