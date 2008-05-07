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
 * Exception thrown in case of a validation error.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final ValidationResult validationResult;

    /**
     * Constructs a new <code>ValidationException</code> with the specified {@link ValidationResult}.
     * 
     * @param validationResult the validation result
     */
    public ValidationException(ValidationResult validationResult) {
        super(createMessage(validationResult));
        this.validationResult = validationResult;
    }
    
    /**
     * @return the validation result
     */
    public ValidationResult getValidationResult() {
        return this.validationResult;
    }

    private static String createMessage(ValidationResult result) {
        StringBuilder message = new StringBuilder();
        for (ValidationError error : result.getErrors()) {
            message.append(error.getErrorText());
            message.append("\n");
        }
        return message.toString();
    }

}
