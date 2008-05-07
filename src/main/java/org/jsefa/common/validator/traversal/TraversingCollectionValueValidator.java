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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;


final class TraversingCollectionValueValidator extends TraversingValidator {

    private final Map<Class<?>, Validator> validatorsByObjectType;

    @SuppressWarnings("unchecked")
    TraversingCollectionValueValidator(Map<Class<?>, Validator> validatorsByObjectType) {
        if (checkTriviality(validatorsByObjectType.values())) {
            this.validatorsByObjectType = Collections.EMPTY_MAP;
        } else {
            this.validatorsByObjectType = new HashMap<Class<?>, Validator>(validatorsByObjectType);
        }
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
        for (Object item : (Collection<?>) object) {
            Validator itemValidator = getItemValidator(getNormalizedObjectType(item));
            if (itemValidator != null) {
                errors.addAll(itemValidator.validate(item).getErrors());
            }
        }
        return ValidationResult.create(errors);
    }

    private Validator getItemValidator(Class<?> objectType) {
        return ReflectionUtil.getNearest(objectType, this.validatorsByObjectType);
    }
    
    private Class<?> getNormalizedObjectType(Object value) {
        Class<?> objectType = value.getClass();
        if (Collection.class.isAssignableFrom(objectType)) {
            objectType = Collection.class;
        }
        return objectType;
    }
    
}
