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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.mapping.ComplexTypeMapping;
import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.mapping.ListTypeMapping;
import org.jsefa.common.mapping.MapTypeMapping;
import org.jsefa.common.mapping.NodeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingRegistry;
import org.jsefa.common.validator.ValidationError;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;

/**
 * Factory for creating traversing <code>Validator</code>s.
 * @author Norman Lahme-Huetig
 *
 * @param <N> the type of the data type name
 */
public class TraversingValidatorFactory<N> {
    private TypeMappingRegistry<N> typeMappingRegistry;

    private ObjectAccessorProvider objectAccessorProvider;

    private ConcurrentMap<N, TraversingComplexValueValidator> traversingComplexValueValidators;

    /**
     * Constructs a new <code>TraversingValidatorFactory</code>.
     * @param typeMappingRegistry the type mapping registry
     * @param objectAccessorProvider the object accessor provider
     */
    public TraversingValidatorFactory(TypeMappingRegistry<N> typeMappingRegistry,
            ObjectAccessorProvider objectAccessorProvider) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.objectAccessorProvider = objectAccessorProvider;
        this.traversingComplexValueValidators = new ConcurrentHashMap<N, TraversingComplexValueValidator>();
    }

    /**
     * Creates a new traversing <code>Validator</code>.
     * @param dataTypeName the data type name
     * @param rootValidator the root validator
     * @return a traversing <code>Validator</code>
     */
    public Validator create(N dataTypeName, Validator rootValidator) {
        return combine(create(this.typeMappingRegistry.get(dataTypeName)), rootValidator);
    }

    @SuppressWarnings("unchecked")
    private Validator create(TypeMapping<N> mapping) {
        if (mapping instanceof ComplexTypeMapping) {
            return createForComplexType((ComplexTypeMapping<N, ?, ?>) mapping);
        } else if (mapping instanceof ListTypeMapping) {
            return createForListType((ListTypeMapping<N, ?, ?>) mapping);
        } else if (mapping instanceof MapTypeMapping) {
            return createForMapType((MapTypeMapping<N, ?, ?, ?>) mapping);
        }
        return null;
    }

    private Validator createForComplexType(ComplexTypeMapping<N, ?, ?> mapping) {
        Validator validator = this.traversingComplexValueValidators.get(mapping.getDataTypeName());
        if (validator != null) {
            return validator;
        }
        TraversingComplexValueValidator result = new TraversingComplexValueValidator();
        this.traversingComplexValueValidators.put((N) mapping.getDataTypeName(), result);
        Map<FieldDescriptor, Validator> validatorsByFieldDescriptor = new HashMap<FieldDescriptor, Validator>();
        for (NodeMapping<N, ?> nodeMapping : mapping.getNodeMappings()) {
            if (nodeMapping.isIndirectMapping()) {
                continue;
            }
            if (validatorsByFieldDescriptor.get(nodeMapping.getFieldDescriptor()) == null) {
                Validator fieldValidator = combine(nodeMapping.getValidator(), create(this.typeMappingRegistry
                        .get(nodeMapping.getDataTypeName())));
                if (fieldValidator != null) {
                    validatorsByFieldDescriptor.put(nodeMapping.getFieldDescriptor(), fieldValidator);
                }
            }
        }
        result.init(mapping.getValidator(), validatorsByFieldDescriptor, objectAccessorProvider.get(mapping
                .getObjectType()));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Validator createForListType(ListTypeMapping<N, ?, ?> mapping) {
        Map<Class<?>, Validator> validatorsByObjectType = new HashMap<Class<?>, Validator>();
        for (NodeMapping<?, ?> nodeMapping : mapping.getNodeMappings()) {
            if (validatorsByObjectType.get(nodeMapping.getObjectType()) == null) {
                Validator itemValidator = combine(nodeMapping.getValidator(), create(this.typeMappingRegistry
                        .get((N) nodeMapping.getDataTypeName())));
                if (itemValidator != null) {
                    validatorsByObjectType.put(nodeMapping.getObjectType(), itemValidator);
                }
            }
        }
        return new TraversingCollectionValueValidator(validatorsByObjectType);
    }

    @SuppressWarnings("unchecked")
    private Validator createForMapType(MapTypeMapping<N, ?, ?, ?> mapping) {
        Map<Class<?>, Validator> valueValidatorsByObjectType = new HashMap<Class<?>, Validator>();
        for (NodeMapping<?, ?> nodeMapping : mapping.getValueNodeMappings()) {
            if (valueValidatorsByObjectType.get(nodeMapping.getObjectType()) == null) {
                Validator valueValidator = combine(nodeMapping.getValidator(), create(this.typeMappingRegistry
                        .get((N) nodeMapping.getDataTypeName())));
                if (valueValidator != null) {
                    valueValidatorsByObjectType.put(nodeMapping.getObjectType(), valueValidator);
                }
            }
        }
        NodeMapping<?, ?> keyNodeMapping = mapping.getKeyNodeMapping();
        Validator keyValidator = combine(keyNodeMapping.getValidator(),
                create(this.typeMappingRegistry.get((N) keyNodeMapping.getDataTypeName())));
        
        return new TraversingMapValueValidator(keyValidator, valueValidatorsByObjectType);
    }

    private Validator combine(Validator validatorA, Validator validatorB) {
        if (validatorA == null) {
            return validatorB;
        } else if (validatorB == null) {
            return validatorA;
        } else {
            return new AndValidator(validatorA, validatorB);
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
