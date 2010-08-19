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

package org.jsefa.test.all;

import static org.jsefa.test.all.ValidatorTestUtil.Mode.INVALID;
import static org.jsefa.test.all.ValidatorTestUtil.Mode.VALID;
import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;
import junit.framework.TestCase;

import org.jsefa.common.validator.LongValidator;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests for testing the {@link LongValidator}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class LongValidatorTest extends TestCase {

    /**
     * Tests the constraint min.
     */
    public void testMinXML() {
        ValidatorTestUtil.check(XML, new LongDTO(0L), INVALID);
        ValidatorTestUtil.check(XML, new LongDTO(1L), VALID);
        ValidatorTestUtil.check(XML, new PrimitiveLongDTO(0L), INVALID);
        ValidatorTestUtil.check(XML, new PrimitiveLongDTO(1L), VALID);
    }

    /**
     * Tests the constraint max.
     */
    public void testMaxXML() {
        ValidatorTestUtil.check(XML, new LongDTO(5L), VALID);
        ValidatorTestUtil.check(XML, new LongDTO(11L), INVALID);
        ValidatorTestUtil.check(XML, new PrimitiveLongDTO(5L), VALID);
        ValidatorTestUtil.check(XML, new PrimitiveLongDTO(11L), INVALID);
    }

    @XmlDataType()
    static final class LongDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"min=1", "max=10"})
        Long longField;

        LongDTO() {
        }

        LongDTO(Long value) {
            this.longField = value;
        }

    }

    @XmlDataType()
    static final class PrimitiveLongDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"min=1", "max=10"})
        long longField;

        PrimitiveLongDTO() {
        }

        PrimitiveLongDTO(long value) {
            this.longField = value;
        }
    }

}
