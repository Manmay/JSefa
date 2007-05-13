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

package org.jsefa.rbf;

import static org.jsefa.JSefaTestUtil.FormatType.*;
import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * Tests to test the RBF serialization/deserialization of different DTOs
 * in/from one stream by using prefixes.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class PrefixTest extends TestCase {

    /**
     * Tests prefixes (FLR).
     */
    public void testFLR() {
        SimpleDTOA simpleDTOA = createSimpleDTOA("valueA1", "valueB1");
        SimpleDTOB simpleDTOB = createSimpleDTOB("valueA2", "valueB2");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(FLR, simpleDTOA, simpleDTOB);
    }

    /**
     * Tests prefixes (CSV).
     */
    public void testCSV() {
        SimpleDTOA simpleDTOA = createSimpleDTOA("valueA1", "valueB1");
        SimpleDTOB simpleDTOB = createSimpleDTOB("valueA2", "valueB2");
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(CSV, simpleDTOA, simpleDTOB);
    }

    private SimpleDTOA createSimpleDTOA(String fieldAValue, String fieldBValue) {
        SimpleDTOA object = new SimpleDTOA();
        object.fieldA = fieldAValue;
        object.fieldB = fieldBValue;
        return object;
    }

    private SimpleDTOB createSimpleDTOB(String fieldAValue, String fieldBValue) {
        SimpleDTOB object = new SimpleDTOB();
        object.fieldA = fieldAValue;
        object.fieldB = fieldBValue;
        return object;
    }

    @CsvDataType(defaultPrefix = "TYPE_A")
    @FlrDataType(defaultPrefix = "TYPE_A")
    static final class SimpleDTOA extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;
    }

    @CsvDataType(defaultPrefix = "TYPE_B")
    @FlrDataType(defaultPrefix = "TYPE_B")
    static final class SimpleDTOB extends AbstractTestDTO {
        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String fieldA;

        @CsvField(pos = 2)
        @FlrField(pos = 2, length = 10)
        String fieldB;
    }

}
