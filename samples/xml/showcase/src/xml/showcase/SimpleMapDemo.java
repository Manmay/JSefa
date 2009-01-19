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

package xml.showcase;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.XmlSerializer;
import org.jsefa.xml.annotation.MapKey;
import org.jsefa.xml.annotation.MapValue;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElementMap;

/**
 * Demonstrates the mapping for a simple map.
 * 
 * @author Norman Lahme-Huetig
 */
public final class SimpleMapDemo {

    private SimpleMapDemo() {

    }

    /**
     * The main method.
     * @param args no args expected here
     */
    public static void main(String[] args) {
        XmlIOFactory factory = XmlIOFactory.createFactory(WeekdayMapDTO.class);
        XmlSerializer serializer = factory.createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        serializer.write(createSampleData());
        serializer.close(true);
        System.out.println(writer.toString());
    }

    private static WeekdayMapDTO createSampleData() {
        WeekdayMapDTO data = new WeekdayMapDTO();
        data.map = new HashMap<Integer, String>();
        data.map.put(0, "Monday");
        data.map.put(1, "Tuesday");
        data.map.put(2, "Wednesday");
        data.map.put(3, "Thursday");
        data.map.put(4, "Friday");
        data.map.put(5, "Saturday");
        data.map.put(5, "Sunday");
        return data;

    }

    @XmlDataType(defaultElementName = "weekday-map")
    private static final class WeekdayMapDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "index"), values = @MapValue(name = "day"))
        Map<Integer, String> map;
    }
}
