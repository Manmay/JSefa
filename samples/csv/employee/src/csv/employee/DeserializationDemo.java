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

package csv.employee;

import java.io.InputStreamReader;
import java.io.Reader;

import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;

/**
 * Demo for demonstrating the deserialization of the document "employee.csv".
 * <p>
 * The code should be self explaining.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class DeserializationDemo {

    void start() {
        Deserializer deserializer = CsvIOFactory.createFactory(Employee.class).createDeserializer();
        deserializer.open(createFileReader());
        while (deserializer.hasNext()) {
            Employee employee = deserializer.next();
            print(employee);
        }
        deserializer.close(true);
    }

    private void print(Employee employee) {
        System.out.println("---------------------------");
        System.out.println("Name: " + employee.name);
        System.out.println("Role: " + employee.role);
        System.out.println("Internal: " + employee.internal);
        System.out.println("Birthdate: " + employee.birthDate);
        System.out.println("Score: " + employee.score.value);
    }

    private Reader createFileReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream("employee.csv"));
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
