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

/**
 * Tests to test the correct serialization/deserialization when a cycle in the type mapping graph exists.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class TypeCycleTest extends TestCase {
    
    /**
     * Tests a simple cycle in the type mapping graph.
     */
    public void testCycle() {
        TestDTO dto = new TestDTO();
        dto.subDTOField = new SubDTO();
        dto.subDTOField.simpleField = "Test";
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, dto);
    }
    
    @XmlDataType
    private static class TestDTO extends AbstractTestDTO {
        @XmlElement
        SubDTO subDTOField;
    }
    
    @XmlDataType(subObjectTypes = SubDTO.class)
    private static class SuperDTO extends AbstractTestDTO {
        @XmlElement
        SuperDTO superDTOField;
    }

    @XmlDataType
    private static class SubDTO extends SuperDTO {
        @XmlElement
        String simpleField;
    }
}
