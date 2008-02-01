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

package multiple.employee;

import java.io.InputStreamReader;
import java.io.Reader;

import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.flr.FlrIOFactory;

/**
 * Demo for demonstrating the deserialization of a {@link Employee}. The same {@link Employee} class is used for
 * CSV and a FLR format.
 * <p>
 * The code should be self explaining.
 * 
 * @author Marko Kovacevic
 * 
 */
public final class DeserializationDemo {

    private static final String CSV = "csv";

    private static final String FLR = "flr";

    private String formatType;

    private DeserializationDemo(String formatType) {
        this.formatType = formatType;
    }

    private void start() {
        Deserializer deserializer = getDeserializer();
        deserializer.open(getFileReader());
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
    }

    private Deserializer getDeserializer() {
        if (this.formatType.equals(CSV)) {
            return CsvIOFactory.createFactory(Employee.class).createDeserializer();
        } else if (this.formatType.equals(FLR)) {
            return FlrIOFactory.createFactory(Employee.class).createDeserializer();
        } else {
            System.out.println("Format not supported: " + this.formatType);
            throw new RuntimeException();
        }
    }

    private Reader getFileReader() {
        if (this.formatType.equals(CSV)) {
            return new InputStreamReader(this.getClass().getResourceAsStream("employee.csv"));
        } else if (this.formatType.equals(FLR)) {
            return new InputStreamReader(this.getClass().getResourceAsStream("employee.flr"));
        } else {
            System.out.println("Format not supported: " + this.formatType);
            throw new RuntimeException();
        }
    }

    /**
     * Main method.
     * 
     * @param args no args needed.
     */
    public static void main(String[] args) {
        System.out.println("CSV:");
        (new DeserializationDemo(CSV)).start();
        System.out.println("FLR:");
        (new DeserializationDemo(FLR)).start();
    }

}
