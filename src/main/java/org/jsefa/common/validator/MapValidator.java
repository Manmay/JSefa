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
import java.util.Map;

/**
 * A validator for <code>Map</code> values.
 * <p>
 * It is thread-safe and immutable.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class MapValidator implements Validator {

    private final Integer minEntries;

    private final Integer maxEntries;

    private static final String MIN = "min";

    private static final String MAX = "max";

    /**
     * Constructs a new <code>MapValidator</code>.
     * 
     * @param configuration the validator configuration
     * @return a map validator
     */
    public static MapValidator create(ValidatorConfiguration configuration) {
        return new MapValidator(configuration);
    }

    private MapValidator(ValidatorConfiguration configuration) {
        ConstraintsAccessor constraints = ConstraintsAccessor.create(configuration);
        this.minEntries = constraints.getInteger(MIN, false);
        this.maxEntries = constraints.getInteger(MAX, false);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object value) {
        Map<?, ?> map = (Map<?, ?>) value;
        Collection<ValidationError> errors = new ArrayList<ValidationError>();
        validateMinEntries(map, errors);
        validateMaxEntries(map, errors);
        return ValidationResult.create(errors);
    }

    private void validateMinEntries(Map<?, ?> map, Collection<ValidationError> errors) {
        int entryCount = map.size();
        if (this.minEntries != null && entryCount < this.minEntries) {
            String errorText = "The map must have at least " + this.minEntries + " entries, but has "
                    + entryCount + " entries only";
            errors.add(ValidationError.create(WRONG_QUANTITY, errorText));
        }
    }

    private void validateMaxEntries(Map<?, ?> map, Collection<ValidationError> errors) {
        int entryCount = map.size();
        if (this.maxEntries != null && entryCount > this.maxEntries) {
            String errorText = "The number of map entries must not exceed " + this.maxEntries + ", but is "
                    + entryCount;
            errors.add(ValidationError.create(WRONG_QUANTITY, errorText));
        }
    }
}
