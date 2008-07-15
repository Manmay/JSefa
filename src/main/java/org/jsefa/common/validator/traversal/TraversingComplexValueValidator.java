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

import org.jsefa.ObjectPathElement;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;

final class TraversingComplexValueValidator extends TraversingValidator {

    private Validator rootValidator;

    private Map<String, ?> validatorsByFieldNameAndType;

    private List<String> fieldNames;

    private ObjectAccessor objectAccessor;

    private static final ThreadLocal<List<Object>> OBJECT_PATH = new ThreadLocal<List<Object>>();

    void init(Validator rootValidator, Map<FieldDescriptor, Validator> fieldValidators, ObjectAccessor objectAccessor) {
        if (checkTriviality(rootValidator, fieldValidators)) {
            return;
        }
        this.rootValidator = rootValidator;
        this.objectAccessor = objectAccessor;
        this.validatorsByFieldNameAndType = createValidatorsByFieldNameAndTypeMap(fieldValidators);
        this.fieldNames = createFieldNamesList(fieldValidators);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(Object object) {
        if (isTrivial()) {
            return ValidationResult.VALID;
        }
        if (onObjectPath(object)) {
            return ValidationResult.VALID;
        }
        addToObjectPath(object);
        try {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            if (this.rootValidator != null) {
                errors.addAll(this.rootValidator.validate(object).getErrors());
            }
            for (String fieldName : this.fieldNames) {
                Object fieldValue = this.objectAccessor.getValue(object, fieldName);
                if (fieldValue != null) {
                    Validator fieldValidator = getFieldValidator(fieldName, fieldValue);
                    if (fieldValidator != null) {
                        for (ValidationError error : fieldValidator.validate(fieldValue).getErrors()) {
                            errors.add(createValidationError(error, new ObjectPathElement(object.getClass(),
                                    fieldName)));
                        }
                    }
                }
            }
            return ValidationResult.create(errors);
        } finally {
            removeFromObjectPath(object);
        }
    }

    @SuppressWarnings("unchecked")
    private Validator getFieldValidator(String fieldName, Object fieldValue) {
        Object value = this.validatorsByFieldNameAndType.get(fieldName);
        if (value instanceof Validator) {
            return (Validator) value;
        } else if (value instanceof Map) {
            return ReflectionUtil.getNearest(fieldValue.getClass(), (Map<Class<?>, Validator>) value);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> createValidatorsByFieldNameAndTypeMap(
            Map<FieldDescriptor, Validator> validatorsByFieldDescriptor) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (FieldDescriptor fieldDescriptor : validatorsByFieldDescriptor.keySet()) {
            Map<Class<?>, Validator> map = (Map<Class<?>, Validator>) result.get(fieldDescriptor.getName());
            if (map == null) {
                map = new HashMap<Class<?>, Validator>();
                result.put(fieldDescriptor.getName(), map);
            }
            map.put(fieldDescriptor.getObjectType(), validatorsByFieldDescriptor.get(fieldDescriptor));
        }
        for (String fieldName : result.keySet()) {
            Map<Class<?>, Validator> map = (Map<Class<?>, Validator>) result.get(fieldName);
            if (map.size() == 1) {
                result.put(fieldName, map.values().iterator().next());
            }
        }
        return result;
    }

    private List<String> createFieldNamesList(Map<FieldDescriptor, Validator> validators) {
        List<String> result = new ArrayList<String>();
        for (FieldDescriptor fieldDescriptor : validators.keySet()) {
            if (!result.contains(fieldDescriptor.getName())) {
                result.add(fieldDescriptor.getName());
            }
        }
        return result;
    }

    private boolean onObjectPath(Object object) {
        List<Object> path = TraversingComplexValueValidator.OBJECT_PATH.get();
        return path != null && path.contains(object);
    }

    private void addToObjectPath(Object object) {
        List<Object> path = TraversingComplexValueValidator.OBJECT_PATH.get();
        if (path == null) {
            path = new ArrayList<Object>();
            TraversingComplexValueValidator.OBJECT_PATH.set(path);
        }
        path.add(object);
    }

    private void removeFromObjectPath(Object object) {
        TraversingComplexValueValidator.OBJECT_PATH.get().remove(object);
    }

    private ValidationError createValidationError(ValidationError validationError, ObjectPathElement prefix) {
        List<ObjectPathElement> relativeObjectPath = new ArrayList<ObjectPathElement>();
        relativeObjectPath.add(prefix);
        relativeObjectPath.addAll(validationError.getRelativeObjectPath());
        return ValidationError.create(validationError.getErrorCode(), validationError.getErrorText(),
                relativeObjectPath.toArray(new ObjectPathElement[0]));
    }

    private boolean checkTriviality(Validator rootValidator, Map<?, Validator> validators) {
        Collection<Validator> allValidators = new ArrayList<Validator>(validators.values());
        if (rootValidator != null) {
            allValidators.add(rootValidator);
        }
        return checkTriviality(allValidators);
    }

}
