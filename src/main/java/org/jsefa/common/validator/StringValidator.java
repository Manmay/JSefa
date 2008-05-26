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

import static org.jsefa.common.validator.ValidationErrorCodes.PATTERN_MATCHING_FAILED;
import static org.jsefa.common.validator.ValidationErrorCodes.WRONG_LENGTH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * A validator for <code>String</code> values.
 * <p>
 * It is thread-safe and immutable.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class StringValidator implements Validator {

    private static final String PATTERN = "pattern";

    private static final String MIN_LENGTH = "minLength";

    private static final String MAX_LENGTH = "maxLength";

    private static final String LENGTH = "length";

    private final Pattern pattern;

    private final Integer minLength;

    private final Integer maxLength;
    
    private final Integer length;

    /**
     * Constructs a new <code>StringValidator</code>.
     * 
     * @param configuration the validator configuration
     * @return a string validator
     */
    public static StringValidator create(ValidatorConfiguration configuration) {
        return new StringValidator(configuration);
    }

    private StringValidator(ValidatorConfiguration configuration) {
        ConstraintsAccessor constraints = ConstraintsAccessor.create(configuration);
        this.pattern = constraints.getPattern(PATTERN, false);
        this.length = constraints.getInteger(LENGTH, false);
        this.minLength = constraints.getInteger(MIN_LENGTH, false);
        this.maxLength = constraints.getInteger(MAX_LENGTH, false);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object value) {
        String text = (String) value;
        Collection<ValidationError> errors = new ArrayList<ValidationError>();
        validatePattern(text, errors);
        validateLength(text, errors);
        validateMinLength(text, errors);
        validateMaxLength(text, errors);
        return ValidationResult.create(errors);
    }
    
    private void validatePattern(String value, Collection<ValidationError> errors) {
        if (pattern != null && !pattern.matcher(value).matches()) {
            String errorText = "The value " + value + " does not match the pattern " + pattern.pattern();
            errors.add(ValidationError.create(PATTERN_MATCHING_FAILED, errorText));
        }
    }
    
    private void validateLength(String value, Collection<ValidationError> errors) {
        if (this.length != null && value.length() != this.length) {
            String errorText = "The value " + value + " has not the required length of " + this.length;
            errors.add(ValidationError.create(WRONG_LENGTH, errorText));
        }
    }

    private void validateMinLength(String value, Collection<ValidationError> errors) {
        if (this.minLength != null && value.length() < this.minLength) {
            String errorText = "The value " + value + " is shorter than the minimum length of " + this.minLength;
            errors.add(ValidationError.create(WRONG_LENGTH, errorText));
        }
    }

    private void validateMaxLength(String value, Collection<ValidationError> errors) {
        if (this.maxLength != null && value.length() > this.maxLength) {
            String errorText = "The value " + value + " is longer than the maximum length of " + this.maxLength;
            errors.add(ValidationError.create(WRONG_LENGTH, errorText));
        }
    }
}
