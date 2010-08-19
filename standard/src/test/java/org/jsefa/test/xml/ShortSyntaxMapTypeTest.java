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
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElementMap;

/**
 * Test to test maps whereas the type of the map keys and values is determinable from the generic argument of the map
 * field.
 * 
 * @author Norman Lahme-Huetig
 */
public class ShortSyntaxMapTypeTest extends TestCase {

    /**
     * Tests an explicit map with <code>String</code> keys and <code>Integer</code> values.
     */
    public void testExplicitListWithStringElements() {
        ExplicitStringToIntegerMap map = new ExplicitStringToIntegerMap();
        map.map = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            map.map.put("stringValue" + i, i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, map);
    }

    @XmlDataType()
    static final class ExplicitStringToIntegerMap extends AbstractTestDTO {
        @XmlElementMap(key = @MapKey(name = "key"), values = @MapValue(name = "value"))
        Map<String, Integer> map;
    }

    /**
     * Tests an implicit map with <code>String</code> keys and <code>Integer</code> values.
     */
    public void testImplicitListWithStringElements() {
        ImplicitStringToIntegerMap map = new ImplicitStringToIntegerMap();
        map.map = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            map.map.put("stringValue" + i, i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, map);
    }

    @XmlDataType()
    static final class ImplicitStringToIntegerMap extends AbstractTestDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "key"), values = @MapValue(name = "value"))
        Map<String, Integer> map;
    }
}
