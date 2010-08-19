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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.MapKey;
import org.jsefa.xml.annotation.MapValue;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementMap;

/**
 * Tests to test implicit maps with <code>String</code> keys and elements of different depth.
 * 
 * @author Norman Lahme-Huetig
 */
public class ImplicitMapTypeTest extends TestCase {

    /**
     * Tests a map with <code>String</code> keys and <code>Integer</code> elements.
     */
    public void testStringToIntegerMap() {
        StringToIntegerMap map = new StringToIntegerMap();
        map.map = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            map.map.put("stringValue" + i, i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, map);
    }

    /**
     * Tests a map with <code>String</code> keys and <code>Depth0DTO</code> elements.
     */
    public void testStringToDepth0Map() {
        StringToDepth0Map map = new StringToDepth0Map();
        map.map = new HashMap<String, Depth0>();
        for (int i = 0; i < 10; i++) {
            map.map.put("stringValue" + i, createDepth0(i));
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, map);
    }
    
    /**
     * Tests a map with <code>String</code> keys and <code>Depth1DTO</code> elements.
     */
    public void testStringToDepth1Map() {
        StringToDepth1Map map = new StringToDepth1Map();
        map.map = new HashMap<String, Depth1>();
        for (int i = 0; i < 10; i++) {
            map.map.put("stringValue" + i, createDepth1(i));
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, map);
    }

    private Depth0 createDepth0(int i) {
        Depth0 object = new Depth0();
        object.fieldA = "valueA" + i;
        object.fieldB = "valueB" + i;
        return object;
    }
    
    private Depth1 createDepth1(int i) {
        Depth1 object = new Depth1();
        object.fieldC = "valueC" + i;
        object.fieldD = createDepth0(i);
        return object;
    }

    @XmlDataType()
    static final class StringToIntegerMap extends AbstractTestDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "key"), values = @MapValue(name = "value"))
        Map<String, Integer> map;
    }
    
    @XmlDataType()
    static final class StringToDepth0Map extends AbstractTestDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "key"), values = @MapValue(name = "value"))
        Map<String, Depth0> map;
    }    

    @XmlDataType()
    static final class StringToDepth1Map extends AbstractTestDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "key"), values = @MapValue(name = "value"))
        Map<String, Depth1> map;
    }    
    
    @XmlDataType()
    static class Depth0 extends AbstractTestDTO {
        @XmlAttribute()
        String fieldA;

        @XmlElement()
        String fieldB;
    }
    
    @XmlDataType()
    static final class Depth1 extends AbstractTestDTO {
        @XmlAttribute()
        String fieldC;

        @XmlElement()
        Depth0 fieldD;
    }    

}
