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

import java.util.List;

import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.Namespace;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlNamespaces;

/**
 * DTO describing a department.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@XmlNamespaces(@Namespace(uri = "www.a-simple-namespace-sample.org/yellowPages"))
@XmlDataType(defaultElementName = "department")
public class Department {
    @XmlAttribute()
    String name;

    @XmlElementList(items = {@ListItem(name = "employee")})
    List<Employee> employees;

}
