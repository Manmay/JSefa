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

import static org.jsefa.common.validator.ValidationErrorCodes.WRONG_QUANTITY;

import java.util.Collection;

/**
 * A validator for <code>Collection</code> values.
 * <p>
 * It is thread-safe and immutable.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class CollectionValidator implements Validator {

    private final Integer minItems;

    private final Integer maxItems;

    private static final String MIN = "min";

    private static final String MAX = "max";

    /**
     * Constructs a new <code>CollectionValidator</code>.
     * 
     * @param configuration the validator configuration
     * @return a collection validator
     */
    public static CollectionValidator create(ValidatorConfiguration configuration) {
        return new CollectionValidator(configuration);
    }

    private CollectionValidator(ValidatorConfiguration configuration) {
        this.minItems = getParameter(configuration, MIN);
        this.maxItems = getParameter(configuration, MAX);
    }

    private Integer getParameter(ValidatorConfiguration configuration, String name) {
        String value = configuration.getConstraints().get(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                throw new ValidatorCreationException("Wrong value for constraint parameter '" + name
                        + "' given: " + value);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object value) {
        int itemCount = ((Collection<?>) value).size();
        if ((this.minItems != null && this.minItems > itemCount)
                || (this.maxItems != null && this.maxItems < itemCount)) {
            String errorText = null;
            if (this.minItems == null) {
                errorText = "The collection must have at least " + this.minItems + " items, but has " + itemCount
                        + " items only";
            } else if (this.maxItems == null) {
                errorText = "The collection must not have more than " + this.maxItems + " items, but has " + itemCount
                        + " items";
            } else {
                errorText = "The number of collection items must be between " + this.minItems + " and " + this.maxItems
                        + ", but is " + itemCount;
            }
            return ValidationResult.create(ValidationError.create(WRONG_QUANTITY, errorText));
        } else {
            return ValidationResult.VALID;
        }
    }

}
