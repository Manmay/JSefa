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

package performance;

import java.math.BigDecimal;
import java.util.Date;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * DTO with 10 elements of different data type.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 */
@XmlDataType()
@CsvDataType()
@FlrDataType()
class MixedDTO {

    @XmlElement()
    @CsvField(pos = 1)
    @FlrField(pos = 1, length = 15)
    String stringElement1;

    @XmlElement()
    @CsvField(pos = 2)
    @FlrField(pos = 2, length = 15)
    String stringElement2;

    @XmlElement()
    @CsvField(pos = 3)
    @FlrField(pos = 3, length = 15)
    Integer integerElement1;

    @XmlElement()
    @CsvField(pos = 4)
    @FlrField(pos = 4, length = 15)
    Integer integerElement2;

    @XmlElement()
    @CsvField(pos = 5)
    @FlrField(pos = 5, length = 15)
    Boolean booleanElement1;

    @XmlElement()
    @CsvField(pos = 6)
    @FlrField(pos = 6, length = 15)
    Boolean booleanElement2;

    @XmlElement()
    @CsvField(pos = 7)
    @FlrField(pos = 7, length = 15)
    Date dateElement1;

    @XmlElement()
    @CsvField(pos = 8)
    @FlrField(pos = 8, length = 15)
    Date dateElement2;

    @XmlElement()
    @CsvField(pos = 9)
    @FlrField(pos = 9, length = 15)
    BigDecimal bigDecimalElement1;

    @XmlElement()
    @CsvField(pos = 10)
    @FlrField(pos = 10, length = 15)
    BigDecimal bigDecimalElement2;

}
