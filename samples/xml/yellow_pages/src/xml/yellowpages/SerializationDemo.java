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

package xml.yellowpages;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import org.jsefa.common.converter.DateConverter;
import org.jsefa.xml.QName;
import org.jsefa.xml.XmlConstants;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.XmlSerializer;

/**
 * Demo for demonstrating the serialization of a {@link Department}.
 * <p>
 * The code should be self explaining.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class SerializationDemo {
    private void start() {
        XmlSerializer serializer = XmlIOFactory.createFactory(Department.class).createSerializer();

        StringWriter writer = new StringWriter();
        serializer.open(writer);
        serializer.getLowLevelSerializer().writeXmlDeclaration("1.0", "ISO-8859-1");
        serializer.getLowLevelSerializer().startElement(
                QName.create("www.a-simple-namespace-sample.org/yellowPages", "yellowPages"));
        serializer.getLowLevelSerializer().writeAttribute(
                QName.create(XmlConstants.XML_SCHEMA_INSTANCE_URI, "noNamespaceSchemaLocation"),
                "Zahlungserinnerung.xsd");

        Department department = createDepartment("Development");

        department.employees.add(createEmployee("Erwin Schmidt", Role.SENIOR_DEVELOPER, "23.5.1964",
                new BigDecimal(7.5)));

        serializer.write(department);
        serializer.getLowLevelSerializer().finishElement();
        serializer.close(true);
        System.out.println("Resulting document:");
        System.out.println(writer.toString());
    }

    private Department createDepartment(String name) {
        Department department = new Department();
        department.name = name;
        department.employees = new ArrayList<Employee>();
        return department;
    }

    private Employee createEmployee(String name, Role role, String birthDate, BigDecimal score) {
        DateConverter dateConverter = new DateConverter("dd.MM.yyyy");
        Employee employee = new Employee();
        employee.name = name;
        employee.role = role;
        employee.birthDate = (Date) dateConverter.fromString(birthDate);
        Score scoreObj = new Score();
        scoreObj.type = "performance";
        scoreObj.value = score;
        employee.score = scoreObj;
        return employee;
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