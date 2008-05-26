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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.jsefa.common.converter.BigDecimalConverter;
import org.jsefa.common.converter.SimpleTypeConverterConfiguration;

/**
 * A validator for <code>BigDecimal</code> values.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class BigDecimalValidator extends NumberValidator<BigDecimal> {

    private static final String MIN_EXCLUSIVE = "minExclusive";

    private static final String MAX_EXCLUSIVE = "maxExclusive";

    private final BigDecimal minExclusive;

    private final BigDecimal maxExclusive;

    /**
     * Constructs a new <code>BigDecimalValidator</code>.
     * 
     * @param configuration the validator configuration
     * @return a big decimal validator
     */
    public static BigDecimalValidator create(ValidatorConfiguration configuration) {
        return new BigDecimalValidator(configuration);
    }

    private BigDecimalValidator(ValidatorConfiguration configuration) {
        super(ConstraintsAccessor.create(configuration, BigDecimalConverter
                .create(SimpleTypeConverterConfiguration.EMPTY)));
        this.minExclusive = getConstraintsAccessor().get(MIN_EXCLUSIVE, false);
        this.maxExclusive = getConstraintsAccessor().get(MAX_EXCLUSIVE, false);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object value) {
        BigDecimal number = (BigDecimal) value;
        Collection<ValidationError> errors = new ArrayList<ValidationError>(super.validate(number).getErrors());
        validateMinExclusive(number, errors);
        validateMaxExclusive(number, errors);
        return ValidationResult.create(errors);
    }
    
    private void validateMinExclusive(BigDecimal number, Collection<ValidationError> errors) {
        if (this.minExclusive != null && number.compareTo(this.minExclusive) <= 0) {
            String errorText = "The value " + number + " is not above the minimum exclusive value " + this.minExclusive;
            errors.add(ValidationError.create(OUT_OF_RANGE, errorText));
        }
    }
    
    private void validateMaxExclusive(BigDecimal number, Collection<ValidationError> errors) {
        if (this.maxExclusive != null && number.compareTo(this.maxExclusive) >= 0) {
            String errorText = "The value " + number + " is not below the maximum exclusive value " + this.maxExclusive;
            errors.add(ValidationError.create(OUT_OF_RANGE, errorText));
        }
    }    

}
