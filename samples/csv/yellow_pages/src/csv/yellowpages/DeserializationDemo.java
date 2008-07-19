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

package csv.yellowpages;

import java.io.InputStreamReader;
import java.io.Reader;

import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;

/**
 * Demo for demonstrating the deserialization of the document "yellow-pages.csv".
 * <p>
 * The code should be self explaining.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class DeserializationDemo {
    void start() {
        Deserializer deserializer = CsvIOFactory.createFactory(Department.class).createDeserializer();
        deserializer.open(createFileReader());
        while (deserializer.hasNext()) {
            Department department = deserializer.next();
            print(department);
        }
        deserializer.close(true);

    }

    private void print(Department department) {
        System.out.println("---------------------------");
        System.out.println("Department " + department.name);
        System.out.println("Employees: ");
        for (Employee employee : department.employees) {
            System.out.println("---------------------------");
            System.out.println("Name: " + employee.name);
            System.out.println("Role: " + employee.role);
            System.out.println("Executive: " + employee.executive);
            System.out.println("Birthdate: " + employee.birthDate);
            System.out.println("Score type: " + employee.score.type);
            System.out.println("Score value: " + employee.score.value);
        }
    }

    private Reader createFileReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream("yellow-pages.csv"));
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
