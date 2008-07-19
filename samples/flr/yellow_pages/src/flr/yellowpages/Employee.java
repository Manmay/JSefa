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

package flr.yellowpages;

import java.util.Date;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.annotation.FlrSubRecord;

/**
 * DTO describing an employee.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@FlrDataType(defaultPrefix = "EMP")
public class Employee {
    @FlrField(pos = 1, length = 20)
    String name;

    @FlrField(pos = 2, length = 20)
    Role role;

    @FlrField(pos = 3, length = 5)
    boolean executive = false;

    @FlrField(pos = 4, format = "dd.MM.yyyy", length = 10)
    Date birthDate;

    @FlrSubRecord(pos = 5, prefix = "SCO")
    Score score;
}
