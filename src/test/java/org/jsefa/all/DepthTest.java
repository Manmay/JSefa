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

package org.jsefa.all;

import static org.jsefa.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.JSefaTestUtil.FormatType.FLR;
import static org.jsefa.JSefaTestUtil.FormatType.XML;
import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests to test the serialization/deserialization of DTOs with different
 * depth.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class DepthTest extends TestCase {

    /**
     * Test with the depth one (CSV).
     */
    public void testDepth1CSV() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, createDepth1DTO());
    }

    /**
     * Test with the depth one (FLR).
     */
    public void testDepth1FLR() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, createDepth1DTO());
    }

    /**
     * Test with the depth one (XML).
     */
    public void testDepth1XML() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, createDepth1DTO());
    }

    /**
     * Test with the depth two (CSV).
     */
    public void testDepth2CSV() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, createDepth2DTO());
    }

    /**
     * Test with the depth two (FLR).
     */
    public void testDepth2FLR() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, createDepth2DTO());
    }

    /**
     * Test with the depth two (XML).
     */
    public void testDepth2XML() {
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, createDepth2DTO());
    }

    @CsvDataType(defaultPrefix = "DEPTH_2")
    @FlrDataType(defaultPrefix = "DEPTH_2")
    @XmlDataType()
    static final class Depth2DTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1)
        @XmlElement()
        Depth1DTO fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement()
        String fieldB;
    }

    @CsvDataType(defaultPrefix = "DEPTH_1")
    @FlrDataType(defaultPrefix = "DEPTH_1")
    @XmlDataType()
    static final class Depth1DTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1)
        @XmlElement()
        Depth0DTO fieldC;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement()
        String fieldD;
    }

    @CsvDataType(defaultPrefix = "DEPTH_0")
    @FlrDataType(defaultPrefix = "DEPTH_0")
    @XmlDataType()
    static class Depth0DTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        @XmlElement()
        String fieldE;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        @XmlElement()
        String fieldF;
    }

    private Depth2DTO createDepth2DTO() {
        Depth2DTO object = new Depth2DTO();
        object.fieldA = createDepth1DTO();
        object.fieldB = "valueB";
        return object;
    }

    private Depth1DTO createDepth1DTO() {
        Depth1DTO object = new Depth1DTO();
        object.fieldC = createDepth0DTO();
        object.fieldD = "valueD";
        return object;
    }

    private Depth0DTO createDepth0DTO() {
        Depth0DTO object = new Depth0DTO();
        object.fieldE = "valueE";
        object.fieldF = "valueF";
        return object;
    }

}
