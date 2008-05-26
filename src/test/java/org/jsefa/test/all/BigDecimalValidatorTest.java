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

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.jsefa.common.validator.BigDecimalValidator;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests for testing the {@link BigDecimalValidator}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class BigDecimalValidatorTest extends TestCase {

    /**
     * Tests the constraint min.
     */
    public void testMin() {
        ValidatorTestUtil.check(XML, new InclusiveDTO(new BigDecimal(0)), INVALID);
        ValidatorTestUtil.check(XML, new InclusiveDTO(new BigDecimal(1)), VALID);
    }

    /**
     * Tests the constraint max.
     */
    public void testMax() {
        ValidatorTestUtil.check(XML, new InclusiveDTO(new BigDecimal(10)), VALID);
        ValidatorTestUtil.check(XML, new InclusiveDTO(new BigDecimal(11)), INVALID);
    }

    /**
     * Tests the constraint minExclusive.
     */
    public void testMinExclusive() {
        ValidatorTestUtil.check(XML, new ExclusiveDTO(new BigDecimal(1)), INVALID);
        ValidatorTestUtil.check(XML, new ExclusiveDTO(new BigDecimal(1.01)), VALID);
    }

    /**
     * Tests the constraint maxExclusive.
     */
    public void testMaxExclusive() {
        ValidatorTestUtil.check(XML, new ExclusiveDTO(new BigDecimal(9.99)), VALID);
        ValidatorTestUtil.check(XML, new ExclusiveDTO(new BigDecimal(10)), INVALID);
    }


    @XmlDataType()
    static final class InclusiveDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"min=1", "max=10"})
        BigDecimal field;

        InclusiveDTO() {
        }

        InclusiveDTO(BigDecimal value) {
            this.field = value;
        }

    }

    @XmlDataType()
    static final class ExclusiveDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"minExclusive=1", "maxExclusive=10"})
        BigDecimal field;

        ExclusiveDTO() {
        }

        ExclusiveDTO(BigDecimal value) {
            this.field = value;
        }

    }
}
