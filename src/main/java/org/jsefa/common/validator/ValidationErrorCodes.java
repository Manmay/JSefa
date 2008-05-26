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
 * Constants for validation error codes. Modeled as Strings and not as enums to allow for user-defined error codes
 * used by user defined {@link Validator}s.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface ValidationErrorCodes {

    /**
     * A required value is missing.
     */
    String MISSING_VALUE = "MISSING_VALUE";
    
    /**
     * A value does not match a given pattern.
     */
    String PATTERN_MATCHING_FAILED = "PATTERN_MATCHING_FAILED";
    
    /**
     * A collection does have more or less items than allowed. 
     */
    String WRONG_QUANTITY = "WRONG_QUANTITY";
    
    /**
     * A numeric value is out of range. 
     */
    String OUT_OF_RANGE = "OUT_OF_RANGE";
    
    /**
     * The length of a value is smaller or bigger then allowed.
     */
    String WRONG_LENGTH = "WRONG_LENGTH";
    

}
