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

package flr.employee;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

import org.jsefa.common.converter.DateConverter;
import org.jsefa.common.converter.SimpleTypeConverterConfiguration;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.FlrSerializer;

import xml.yellowpages.Department;

/**
 * Demo for demonstrating the serialization of a {@link Department}.
 * <p>
 * The code should be self explaining.
 * 
 * @author Norman Lahme-Huetig
 * @author Marko Kovacevic
 * 
 */
public class SerializationDemo {

    void start() {
        FlrSerializer serializer = (FlrSerializer) FlrIOFactory.createFactory(Employee.class).createSerializer();

        StringWriter writer = new StringWriter();
        serializer.open(writer);

        serializer.write(createEmployee("Erwin Schmidt", Role.SENIOR_DEVELOPER, false, "23.5.1964",
                new BigDecimal(7.83)));
        serializer.write(createEmployee("Betty Meier", Role.JUNIOR_DEVELOPER, null, "1.1.1984", new BigDecimal(
                4.28)));

        serializer.close(true);
        System.out.println("Result:");
        System.out.println(writer.toString());
    }

    private Employee createEmployee(String name, Role role, Boolean internal, String birthdDate, BigDecimal score) {
        Employee employee = new Employee();
        employee.name = name;
        employee.role = role;
        employee.internal = internal;
        employee.birthDate = createDate(birthdDate);
        employee.score = new Score();
        employee.score.type = "performance";
        employee.score.value = score;
        return employee;
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
