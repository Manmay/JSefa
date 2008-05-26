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

import java.util.ArrayList;
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
        ConstraintsAccessor constraints = ConstraintsAccessor.create(configuration);
        this.minItems = constraints.getInteger(MIN, false);
        this.maxItems = constraints.getInteger(MAX, false);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object value) {
        Collection<?> collection = (Collection<?>) value;
        Collection<ValidationError> errors = new ArrayList<ValidationError>();
        validateMinItems(collection, errors);
        validateMaxItems(collection, errors);
        return ValidationResult.create(errors);
    }

    private void validateMinItems(Collection<?> collection, Collection<ValidationError> errors) {
        int itemCount = collection.size();
        if (this.minItems != null && itemCount < this.minItems) {
            String errorText = "The collection must have at least " + this.minItems + " items, but has "
                    + itemCount + " items only";
            errors.add(ValidationError.create(WRONG_QUANTITY, errorText));
        }
    }

    private void validateMaxItems(Collection<?> collection, Collection<ValidationError> errors) {
        int itemCount = collection.size();
        if (this.maxItems != null && itemCount > this.maxItems) {
            String errorText = "The number of collection items must not exceed " + this.maxItems + ", but is "
                    + itemCount;
            errors.add(ValidationError.create(WRONG_QUANTITY, errorText));
        }
    }
}
