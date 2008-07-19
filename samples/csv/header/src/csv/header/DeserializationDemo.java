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

import java.io.InputStreamReader;
import java.io.Reader;

import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvDeserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

/**
 * Demo for demonstrating the deserialization of the document "person.csv" which contains a header.
 * <p>
 * The code should be self explaining.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class DeserializationDemo {

    void start() {
        CsvConfiguration config = new CsvConfiguration();
        // header of size 1, no footer, store the filtered lines
        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        CsvDeserializer deserializer = CsvIOFactory.createFactory(config, Person.class).createDeserializer();
        deserializer.open(createFileReader());
        while (deserializer.hasNext()) {
            Person person = deserializer.next();
            print(person);
        }
        String header = deserializer.getStoredLines().get(0).getContent();
        printHeader(header);
        deserializer.close(true);
    }

    private void printHeader(String header) {
        System.out.println("HEADER: " + header);
    }

    private void print(Person employee) {
        System.out.println("---------------------------");
        System.out.println("Name: " + employee.name);
        System.out.println("Birthdate: " + employee.birthDate);
    }

    private Reader createFileReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream("person.csv"));
    }

    /**
     * Main method.
     * 
     * @param args no args needed.
     */
    public static void main(String[] args) {
        (new DeserializationDemo()).start();
    }

}
