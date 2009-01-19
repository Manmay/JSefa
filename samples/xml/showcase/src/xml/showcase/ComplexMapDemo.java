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
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementMap;

/**
 * Demonstrates the mapping for a more complex map.
 * 
 * @author Norman Lahme-Huetig
 */
public final class ComplexMapDemo {

    private ComplexMapDemo() {

    }

    /**
     * The main method.
     * @param args no args expected here
     */
    public static void main(String[] args) {
        XmlIOFactory factory = XmlIOFactory.createFactory(PersonMapDTO.class);
        XmlSerializer serializer = factory.createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        serializer.write(createSampleData());
        serializer.close(true);
        System.out.println(writer.toString());
    }

    private static PersonMapDTO createSampleData() {
        PersonMapDTO data = new PersonMapDTO();
        data.map = new HashMap<String, Person>();
        data.map.put("ES", Person.create("Schmidt", "Erwin"));
        data.map.put("BM", Person.create("Maier", "Betty"));
        return data;

    }

    @XmlDataType(defaultElementName = "person-map")
    private static final class PersonMapDTO {
        @XmlElementMap(implicit = true, key = @MapKey(name = "key"), values = @MapValue(name = "person"))
        Map<String, Person> map;
    }
    
    @XmlDataType()
    private static final class Person {
        @XmlElement
        String surname;
        
        @XmlElement
        String firstname;
        
        static Person create(String surname, String firstname) {
            Person p = new Person();
            p.surname = surname;
            p.firstname = firstname;
            return p;
        }
    }
}
