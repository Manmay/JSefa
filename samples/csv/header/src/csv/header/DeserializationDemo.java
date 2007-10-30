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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;

/**
 * Demo for demonstrating the deserialization of the document "person.csv" which
 * contains a header.
 * <p>
 * There is no need for a special support for headers as one can use the Reader
 * given to {@link Deserializer#open(Reader)} to read the header first. This
 * example shows this idiom.
 * <p>
 * The code should be self explaining.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class DeserializationDemo {

    private void start() {
        Deserializer deserializer = CsvIOFactory.createFactory(Person.class).createDeserializer();
        BufferedReader reader = new BufferedReader(createFileReader());
        String header = readHeader(reader);
        printHeader(header);
        deserializer.open(reader);
        while (deserializer.hasNext()) {
            Person person = deserializer.next();
            print(person);
        }
        deserializer.close(true);
    }

    private String readHeader(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
