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
 * Tests for testing the {@link IntegerValidator}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class IntegerValidatorTest extends TestCase {

    /**
     * Tests the constraint min.
     */
    public void testMin() {
        ValidatorTestUtil.check(XML, new IntegerDTO(0), INVALID);
        ValidatorTestUtil.check(XML, new IntegerDTO(1), VALID);
        ValidatorTestUtil.check(XML, new IntDTO(0), INVALID);
        ValidatorTestUtil.check(XML, new IntDTO(1), VALID);
    }

    /**
     * Tests the constraint max.
     */
    public void testMax() {
        ValidatorTestUtil.check(XML, new IntegerDTO(5), VALID);
        ValidatorTestUtil.check(XML, new IntegerDTO(11), INVALID);
        ValidatorTestUtil.check(XML, new IntDTO(5), VALID);
        ValidatorTestUtil.check(XML, new IntDTO(11), INVALID);
    }

    @XmlDataType()
    static final class IntegerDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"min=1", "max=10"})
        Integer integerField;

        IntegerDTO() {
        }

        IntegerDTO(Integer value) {
            this.integerField = value;
        }

    }

    @XmlDataType()
    static final class IntDTO extends AbstractTestDTO {
        @XmlElement(constraints = {"min=1", "max=10"})
        int intField;

        IntDTO() {
        }

        IntDTO(int value) {
            this.intField = value;
        }
    }

}
