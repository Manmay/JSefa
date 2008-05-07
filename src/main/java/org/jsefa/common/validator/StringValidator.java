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

import java.util.regex.Pattern;
import static org.jsefa.common.validator.ValidationErrorCodes.PATTERN_MATCHING_FAILED;

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

    private final Pattern pattern;

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
        this.pattern = getPattern(configuration);
    }

    private Pattern getPattern(ValidatorConfiguration configuration) {
        String patternString = configuration.getConstraints().get(PATTERN);
        if (patternString == null || patternString.isEmpty()) {
            throw new ValidatorCreationException("No value for string constraint parameter 'pattern' given");
        }
        try {
            return Pattern.compile(patternString);
        } catch (Exception e) {
            throw new ValidatorCreationException("Wrong value for string constraint parameter 'pattern' given: "
                    + patternString);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object value) {
        if (pattern.matcher((String) value).matches()) {
            return ValidationResult.VALID;
        } else {
            String errorText = "The value " + value + " does not match the pattern " + pattern.pattern();
            return ValidationResult.create(ValidationError.create(PATTERN_MATCHING_FAILED, errorText));
        }
    }

}
