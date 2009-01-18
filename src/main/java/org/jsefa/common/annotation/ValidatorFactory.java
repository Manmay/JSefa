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

package org.jsefa.common.annotation;

import static org.jsefa.common.annotation.AnnotationParameterNames.CONSTRAINTS;
import static org.jsefa.common.annotation.AnnotationParameterNames.DEFAULT_VALIDATOR_TYPE;
import static org.jsefa.common.annotation.AnnotationParameterNames.LIST_ITEM;
import static org.jsefa.common.annotation.AnnotationParameterNames.OBJECT_TYPE;
import static org.jsefa.common.annotation.AnnotationParameterNames.VALIDATOR_TYPE;
import static org.jsefa.common.annotation.AnnotationParameterNames.VALUE_REQUIRED;
import static org.jsefa.common.validator.ValidationErrorCodes.MISSING_VALUE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsefa.ObjectPathElement;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
import org.jsefa.common.validator.provider.ValidatorProvider;

/**
 * Factory for creating {@link Validator}s from annotated classes.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ValidatorFactory {

    private final ObjectAccessorProvider objectAccessorProvider;

    private final ValidatorProvider validatorProvider;

    /**
     * Constructs a new <code>ValidatorFactory</code>.
     * 
     * @param validatorProvider the validator provider
     * @param objectAccessorProvider the object accessor provider
     */
    public ValidatorFactory(ValidatorProvider validatorProvider, ObjectAccessorProvider objectAccessorProvider) {
        this.validatorProvider = validatorProvider;
        this.objectAccessorProvider = objectAccessorProvider;
    }

    /**
     * Creates a contextual validator for the given object type.
     * 
     * @param objectType the object type to create a validator for
     * @param field the field to create a contextual validator for (may be null)
     * @param contextAnnotation the annotation providing parameters for constructing the validator
     * @param dataTypeAnnotationType the type of the data type annotation providing default parameters for
     *                constructing the validator
     * @return a validator
     */
    @SuppressWarnings("unchecked")
    public Validator createContextualValidator(Class<?> objectType, Field field, Annotation contextAnnotation,
            Class<? extends Annotation> dataTypeAnnotationType) {
        Class<? extends Validator> validatorType = null;
        Validator validator = null;
        String[] constraints = null;
        if (contextAnnotation != null) {
            validatorType = AnnotationDataProvider.get(contextAnnotation, VALIDATOR_TYPE);
            constraints = AnnotationDataProvider.get(contextAnnotation, CONSTRAINTS);
        }
        if (validatorType == null) {
            if (this.validatorProvider.hasValidatorFor(objectType)) {
                validator = this.validatorProvider.getForObjectType(objectType, constraints);
            } else {
                validatorType = AnnotationDataProvider.get(objectType, DEFAULT_VALIDATOR_TYPE,
                        dataTypeAnnotationType);
            }
        }
        if (validator == null && validatorType != null) {
            validator = this.validatorProvider.getForValidatorType(validatorType, objectType, constraints);
        }
        if (hasCollectionType(objectType) && contextAnnotation != null
                && AnnotationDataProvider.hasParameter(contextAnnotation.getClass(), LIST_ITEM)) {
            Annotation itemAnnotation = AnnotationDataProvider.get(contextAnnotation, LIST_ITEM);
            Class<?> itemObjectType = AnnotationDataProvider.get(itemAnnotation, OBJECT_TYPE);
            if (itemObjectType == null) {
                itemObjectType = ReflectionUtil.getActualTypeParameter(field, 0);
            }
            Validator itemValidator = createContextualValidator(itemObjectType, field, itemAnnotation,
                    dataTypeAnnotationType);
            if (itemValidator != null) {
                Validator itemListValidator = new SimpleListTypeValidator(itemValidator);
                if (validator != null) {
                    validator = new AndValidator(validator, itemListValidator);
                } else {
                    validator = itemListValidator;
                }
            }
        }
        return validator;
    }

    /**
     * Creates a context free validator for the given object type.
     * 
     * @param objectType the object type to create a validator for
     * @param fieldAnnotationTypes the possible annotation types for the fields of the given object type providing
     *                parameters for constructing the validator
     * @return a validator
     */
    public Validator createValidator(Class<?> objectType, Class<? extends Annotation>... fieldAnnotationTypes) {
        List<String> requiredFieldNames = new ArrayList<String>();
        for (Field field : AnnotatedFieldsProvider.getAnnotatedFields(objectType, fieldAnnotationTypes)) {
            Boolean required = AnnotationDataProvider.get(field, VALUE_REQUIRED, fieldAnnotationTypes);
            if (required != null && required) {
                requiredFieldNames.add(field.getName());
            }
        }
        if (requiredFieldNames.isEmpty()) {
            return null;
        } else {
            return new RequiredFieldsValidator(this.objectAccessorProvider.get(objectType), requiredFieldNames);
        }
    }

    private boolean hasCollectionType(Class<?> objectType) {
        return Collection.class.isAssignableFrom(objectType);
    }

    private static final class RequiredFieldsValidator implements Validator {

        private List<String> requiredFieldNames;

        private ObjectAccessor objectAccessor;

        private RequiredFieldsValidator(ObjectAccessor objectAccessor, List<String> requiredFieldNames) {
            this.objectAccessor = objectAccessor;
            this.requiredFieldNames = requiredFieldNames;
        }

        public ValidationResult validate(Object value) {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            for (String fieldName : this.requiredFieldNames) {
                if (this.objectAccessor.getValue(value, fieldName) == null) {
                    String errorText = "The field " + fieldName + " is required but no value is present";
                    errors.add(ValidationError.create(MISSING_VALUE, errorText,
                            new ObjectPathElement(value.getClass(), fieldName)));
                }
            }
            return ValidationResult.create(errors);
        }

    }

    private static final class SimpleListTypeValidator implements Validator {
        private Validator itemValidator;

        SimpleListTypeValidator(Validator itemValidator) {
            this.itemValidator = itemValidator;
        }

        public ValidationResult validate(Object value) {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            for (Object item : (List<?>) value) {
                errors.addAll(this.itemValidator.validate(item).getErrors());
            }
            return ValidationResult.create(errors);
        }

    }

    private static final class AndValidator implements Validator {
        private Validator validatorA;

        private Validator validatorB;

        AndValidator(Validator validatorA, Validator validatorB) {
            this.validatorA = validatorA;
            this.validatorB = validatorB;
        }

        public ValidationResult validate(Object value) {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            errors.addAll(validatorA.validate(value).getErrors());
            errors.addAll(validatorB.validate(value).getErrors());
            return ValidationResult.create(errors);
        }

    }

}
