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

import junit.framework.TestCase;

import org.jsefa.SerializationException;
import org.jsefa.common.config.Configuration;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.test.common.JSefaTestUtil.FormatType;

/**
 * Utility class for validator tests.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ValidatorTestUtil {

    enum Mode {
        VALID, INVALID,
    }
    
    private ValidatorTestUtil() {
        
    }

    @SuppressWarnings("unchecked")
    static void check(FormatType formatType, Object value, Mode mode) {
        Configuration config = JSefaTestUtil.createConfiguration(formatType);
        try {
            JSefaTestUtil.serialize(formatType, config, value);
            TestCase.assertFalse(mode == Mode.INVALID);
        } catch (SerializationException e) {
            TestCase.assertTrue(mode == Mode.INVALID);
        }
    }
}
