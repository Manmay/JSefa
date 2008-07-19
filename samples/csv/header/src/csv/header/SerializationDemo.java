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

package csv.header;

import java.io.StringWriter;
import java.util.Date;

import org.jsefa.common.converter.DateConverter;
import org.jsefa.common.converter.SimpleTypeConverterConfiguration;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.CsvSerializer;

/**
 * Demo for demonstrating the serialization of a {@link Person} with a header.
 * <p>
 * The code should be self explaining
 * 
 * @author Norman Lahme-Huetig
 */
public class SerializationDemo {

    void start() {
        CsvSerializer serializer = (CsvSerializer) CsvIOFactory.createFactory(Person.class).createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        serializer.getLowLevelSerializer().writeLine("NAME;BIRTHDATE"); // the header
        serializer.write(createPerson("Erwin Schmidt", "23.5.1964"));
        serializer.write(createPerson("Betty Meier", "1.1.1984"));
        serializer.close(true);
        System.out.println("Result:");
        System.out.println(writer.toString());
    }

    private Person createPerson(String name, String birthdDate) {
        Person person = new Person();
        person.name = name;
        person.birthDate = createDate(birthdDate);
        return person;
    }

    private Date createDate(String date) {
        return DateConverter.create(SimpleTypeConverterConfiguration.EMPTY).fromString(date);
    }

    /**
     * Main method.
     * 
     * @param args no args needed.
     */
    public static void main(String[] args) {
        (new SerializationDemo()).start();
    }

}
