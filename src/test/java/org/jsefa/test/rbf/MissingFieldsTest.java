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

package org.jsefa.test.rbf;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.CSV;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.FLR;
import junit.framework.TestCase;

import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;

/**
 * Tests for testing deserialization of records with missing fields.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class MissingFieldsTest extends TestCase {
    
    /**
     * Tests it for CSV.
     */
    public void testCSV() {
        check(CSV, "a", TestDTO.class);
    }
    
    /**
     * Tests it for FLR.
     */
    public void testFLR() {
        check(FLR, "a         ", TestDTO.class);
    }    

    @SuppressWarnings("unchecked")
    private void check(FormatType formatType, String inputString, Class<?> objectType) {
        JSefaTestUtil.deserialize(formatType, inputString, objectType);
    }
    
    
    @CsvDataType()
    @FlrDataType()
    static final class TestDTO extends AbstractTestDTO {
        @CsvField(pos = 0)
        @FlrField(pos = 0, length = 10)
        String stringFieldA;

        @CsvField(pos = 1)
        @FlrField(pos = 1, length = 10)
        String stringFieldB;
    }
}
