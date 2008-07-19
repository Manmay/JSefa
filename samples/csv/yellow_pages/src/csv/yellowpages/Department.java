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

import java.util.List;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.annotation.CsvSubRecordList;
import org.jsefa.rbf.annotation.Record;

/**
 * DTO describing a department.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@CsvDataType(defaultPrefix = "DEP")
public class Department {
    @CsvField(pos = 0)
    String name;

    @CsvSubRecordList(pos = 1, records = @Record(prefix = "EMP"))
    List<Employee> employees;

}
