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

package org.jsefa.common.validator.traversal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;


final class TraversingMapValueValidator extends TraversingValidator {

    private Validator keyValidator;

    private Map<Class<?>, Validator> valueValidatorsByObjectType;

    TraversingMapValueValidator(Validator keyValidator, Map<Class<?>, Validator> valueValidatorsByObjectType) {
        if (checkTriviality(keyValidator, valueValidatorsByObjectType)) {
            return;
        }
        this.keyValidator = keyValidator;
        this.valueValidatorsByObjectType = new HashMap<Class<?>, Validator>(valueValidatorsByObjectType);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object object) {
        if (isTrivial()) {
            return ValidationResult.VALID;
        }
        if (object == null) {
            return ValidationResult.VALID;
        }
        List<ValidationError> errors = new ArrayList<ValidationError>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
            if (this.keyValidator !=  null) {
                errors.addAll(this.keyValidator.validate(entry.getKey()).getErrors());
            }
            Validator valueValidator = getValueValidator(getNormalizedObjectType(entry.getValue()));
            if (valueValidator != null) {
                errors.addAll(valueValidator.validate(entry.getValue()).getErrors());
            }
        }
        return ValidationResult.create(errors);
    }

    private Validator getValueValidator(Class<?> objectType) {
        return ReflectionUtil.getNearest(objectType, this.valueValidatorsByObjectType);
    }

    private Class<?> getNormalizedObjectType(Object value) {
        Class<?> objectType = value.getClass();
        if (Collection.class.isAssignableFrom(objectType)) {
            objectType = Collection.class;
        } else if (Map.class.isAssignableFrom(objectType)) {
            objectType = Map.class;
        }
        return objectType;
    }
    
    private boolean checkTriviality(Validator keyValidator, Map<?, Validator> valueValidators) {
        Collection<Validator> allValidators = new ArrayList<Validator>(valueValidators.values());
        if (keyValidator != null) {
            allValidators.add(keyValidator);
        }
        return checkTriviality(allValidators);
    }
    
    
}
