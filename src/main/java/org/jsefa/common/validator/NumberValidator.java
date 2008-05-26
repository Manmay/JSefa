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

import static org.jsefa.common.validator.ValidationErrorCodes.OUT_OF_RANGE;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A validator for <code>Number</code> values.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <T> the type of the number
 * 
 */
public abstract class NumberValidator<T extends Number> implements Validator {
    
    private static final String MIN = "min";
    private static final String MAX = "max";
    
    private final T min;
    private final T max;
    
    private final ConstraintsAccessor constraintsAccessor;
    
    /**
     * Constructs a new <code>NumberValidator</code>.
     * @param constraintsAccessor the accessor to the configured constraints
     */
    @SuppressWarnings("unchecked")
    protected NumberValidator(ConstraintsAccessor constraintsAccessor) {
        this.constraintsAccessor = constraintsAccessor;
        this.min = (T) constraintsAccessor.get(MIN, false);
        this.max = (T) constraintsAccessor.get(MAX, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public ValidationResult validate(Object value) {
        T number = (T) value;
        Collection<ValidationError> errors = new ArrayList<ValidationError>();
        validateMin(number, errors);
        validateMax(number, errors);
        return ValidationResult.create(errors);
    }
    
    /**
     * @return the constraints accessor
     */
    protected ConstraintsAccessor getConstraintsAccessor() {
        return this.constraintsAccessor;
    }
    
    private void validateMin(T value, Collection<ValidationError> errors) {
        if (this.min != null && compare(value, this.min) < 0) {
            String errorText = "The value " + value + " falls below the minimum value " + this.min;
            errors.add(ValidationError.create(OUT_OF_RANGE, errorText));
        }
    }
    
    private void validateMax(T value, Collection<ValidationError> errors) {
        if (this.max != null && compare(value, this.max) > 0) {
            String errorText = "The value " + value + " exceeds the maximum value " + this.max;
            errors.add(ValidationError.create(OUT_OF_RANGE, errorText));
        }
    }
    
    @SuppressWarnings("unchecked")
    private int compare(Object valueA, Object valueB) {
        return ((Comparable<T>) valueA).compareTo((T) valueB);
    }
    
    

}
