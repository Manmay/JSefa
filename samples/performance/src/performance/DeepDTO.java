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

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.annotation.CsvSubRecord;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.flr.annotation.FlrSubRecord;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * DTO with depth 5 and 10 <code>String</code> elements in total.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 */
@XmlDataType()
@CsvDataType(defaultPrefix = "L0")
@FlrDataType(defaultPrefix = "L0")
class DeepDTO {

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
    String stringElement3;

    @XmlElement()
    @CsvField(pos = 4)
    @FlrField(pos = 4, length = 15)
    String stringElement4;

    @XmlElement()
    @CsvField(pos = 5)
    @FlrField(pos = 5, length = 15)
    String stringElement5;

    @XmlElement()
    @CsvSubRecord(pos = 6, prefix = "L1")
    @FlrSubRecord(pos = 6, prefix = "L1")
    Level1DTO level1DTOElement;

    @XmlDataType()
    @CsvDataType(defaultPrefix = "L1")
    @FlrDataType(defaultPrefix = "L1")
    static final class Level1DTO {
        @XmlElement()
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        String stringElement;

        @XmlElement()
        @CsvSubRecord(pos = 2, prefix = "L2")
        @FlrSubRecord(pos = 2, prefix = "L2")
        Level2DTO level2DTOElement;
    }

    @XmlDataType()
    @CsvDataType(defaultPrefix = "L2")
    @FlrDataType(defaultPrefix = "L2")
    static final class Level2DTO {
        @XmlElement()
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        String stringElement;

        @XmlElement()
        @CsvSubRecord(pos = 2, prefix = "L3")
        @FlrSubRecord(pos = 2, prefix = "L3")
        Level3DTO level3DTOElement;
    }

    @XmlDataType()
    @CsvDataType(defaultPrefix = "L3")
    @FlrDataType(defaultPrefix = "L3")
    static final class Level3DTO {
        @XmlElement()
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        String stringElement;

        @XmlElement()
        @CsvSubRecord(pos = 2, prefix = "L4")
        @FlrSubRecord(pos = 2, prefix = "L4")
        Level4DTO level4DTOElement;
    }

    @XmlDataType()
    @CsvDataType(defaultPrefix = "L4")
    @FlrDataType(defaultPrefix = "L4")
    static final class Level4DTO {
        @XmlElement()
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        String stringElement;

        @XmlElement()
        @CsvSubRecord(pos = 2, prefix = "L5")
        @FlrSubRecord(pos = 2, prefix = "L5")
        Level5DTO level5DTOElement;
    }

    @XmlDataType()
    @CsvDataType(defaultPrefix = "L5")
    @FlrDataType(defaultPrefix = "L5")
    static final class Level5DTO {
        @XmlElement()
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 15)
        String stringElement;
    }

}
