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

package org.jsefa.common.validator;

/**
 * A validator for object values.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface Validator {

    /**
     * Validates the given value and returns a {@link ValidationResult} describing the result of the validation.
     * 
     * @param value the value to validate
     * @return the result of the validation.
     */
    ValidationResult validate(Object value);

}
