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

import org.jsefa.common.validator.IntegerValidator;
import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;

/**
 * Tests for testing the {@link StringValidator}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class StringValidatorTest extends TestCase {

    /**
     * Tests the constraint pattern.
     */
    public void testPattern() {
        ValidatorTestUtil.check(XML, new PatternDTO("@"), INVALID);
        ValidatorTestUtil.check(XML, new PatternDTO("test"), VALID);
    }

    /**
     * Tests the constraint minLength.
     */
    public void testMinLength() {
        ValidatorTestUtil.check(XML, new LengthRangeDTO(""), INVALID);
        ValidatorTestUtil.check(XML, new LengthRangeDTO("a"), VALID);
    }

    /**
     * Tests the constraint maxLength.
     */
    public void testMaxLength() {
        ValidatorTestUtil.check(XML, new LengthRangeDTO("abcd"), INVALID);
        ValidatorTestUtil.check(XML, new LengthRangeDTO("abc"), VALID);
    }

    /**
     * Tests the constraint length.
     */
    public void testLength() {
        ValidatorTestUtil.check(XML, new LengthDTO("ab"), INVALID);
        ValidatorTestUtil.check(XML, new LengthDTO("abcd"), INVALID);
        ValidatorTestUtil.check(XML, new LengthDTO("abc"), VALID);
    }

    @XmlDataType()
    static final class PatternDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"pattern=\\w+"})
        String field;

        PatternDTO() {
        }

        PatternDTO(String value) {
            this.field = value;
        }
    }

    @XmlDataType()
    static final class LengthRangeDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"minLength=1", "maxLength=3"})
        String field;

        LengthRangeDTO() {
        }

        LengthRangeDTO(String value) {
            this.field = value;
        }
    }

    @XmlDataType()
    static final class LengthDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"length=3"})
        String field;

        LengthDTO() {
        }

        LengthDTO(String value) {
            this.field = value;
        }
    }

}
