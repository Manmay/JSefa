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

package org.jsefa.common.validator.provider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.Validator;
import org.jsefa.common.validator.ValidatorConfiguration;
import org.jsefa.common.validator.ValidatorCreationException;

/**
 * Provider for {@link Validator}s.
 * <p>
 * Each <code>Validator</code> must have a static factory method <code>create</code> , which is either
 * parameterless or has exactly one parameter of type {@link ValidatorConfiguration}.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ValidatorProvider {
    private final ConcurrentMap<Class<?>, Class<? extends Validator>> validatorTypeMap;

    /**
     * Constructs a <code>ValidatorProvider</code>.
     */
    public ValidatorProvider() {
        this.validatorTypeMap = new ConcurrentHashMap<Class<?>, Class<? extends Validator>>();
    }

    private ValidatorProvider(ValidatorProvider other) {
        this.validatorTypeMap = new ConcurrentHashMap<Class<?>, Class<? extends Validator>>(
                other.validatorTypeMap);
    }

    /**
     * Creates a copy of this <code>ValidatorProvider</code>.
     * 
     * @return a copy of this <code>ValidatorProvider</code>
     */
    public ValidatorProvider createCopy() {
        return new ValidatorProvider(this);
    }

    /**
     * Returns true if and only if this provider has a <code>Validator</code> for the given object type.
     * 
     * @param objectType the type of the object a validator is needed for
     * @return true if this provider has a <code>Validator</code> for the given type; false otherwise.
     */
    public boolean hasValidatorFor(Class<?> objectType) {
        return getValidatorType(objectType) != null;
    }

    /**
     * Returns a <code>Validator</code> for the given object type and constraints.
     * 
     * @param objectType the type of the object a validator is needed for
     * @param constraints the constraints the validator must be initialized with
     * @return the validator.
     */
    public Validator getForObjectType(Class<?> objectType, String[] constraints) {
        if (!hasValidatorFor(objectType)) {
            return null;
        }
        Class<? extends Validator> validatorType = getValidatorType(objectType);
        return getForValidatorType(validatorType, objectType, constraints);
    }

    /**
     * Returns an instance of the given <code>Validator</code> type initialized with the given constraints.
     * 
     * @param validatorType the <code>Validator</code> type
     * @param objectType the type of the object a validator is needed for
     * @param constraints the constraints to initialize the validator with
     * @return the validator
     */
    public Validator getForValidatorType(Class<? extends Validator> validatorType,
            Class<?> objectType, String[] constraints) {
        try {
            Method factoryMethod = ReflectionUtil.getMethod(validatorType, "create");
            if (constraints == null && factoryMethod != null) {
                return (Validator) ReflectionUtil.callMethod(null, factoryMethod, (Object[]) null);
            }
            factoryMethod = ReflectionUtil.getMethod(validatorType, "create", ValidatorConfiguration.class);
            if (factoryMethod != null) {
                if (constraints == null) {
                    return null;
                } else {
                    return (Validator) ReflectionUtil.callMethod(null, factoryMethod,
                            ValidatorConfiguration.create(objectType, toMap(constraints)));
                }
            }
            throw new ValidatorCreationException("No static create method found for class " + validatorType);
        } catch (Exception e) {
            throw new ValidatorCreationException("Could not create a Validator for class " + validatorType, e);
        }
    }

    /**
     * Registers the given <code>Validator</code> type as being responsible for values of the given object type.
     * 
     * @param objectType the object type
     * @param validatorType the <code>Validator</code> type
     */
    public void registerValidatorType(Class<?> objectType, Class<? extends Validator> validatorType) {
        this.validatorTypeMap.put(objectType, validatorType);
    }

    private Class<? extends Validator> getValidatorType(Class<?> objectType) {
        return ReflectionUtil.getNearest(objectType, this.validatorTypeMap);
    }

    private Map<String, String> toMap(String[] constraints) {
        Map<String, String> result = new HashMap<String, String>();
        for (String constraint : constraints) {
            int pos = constraint.indexOf('=');
            if (pos < 1 || pos == (constraint.length() - 1)) {
                throw new ValidatorCreationException("Wrong validator constraint format: " + constraint);
            }
            String name = constraint.substring(0, pos).trim();
            String value = constraint.substring(pos + 1).trim();
            if (name.length() == 0 || value.length() == 0) {
                throw new ValidatorCreationException("Wrong validator constraint format: " + constraint);
            }
            result.put(name, value);
        }
        return result;
    }

}
