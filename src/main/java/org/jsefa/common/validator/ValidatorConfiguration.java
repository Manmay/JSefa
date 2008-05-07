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

import java.util.Map;

/**
 * A configuration for a <{@link Validator}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ValidatorConfiguration {
    private final Class<?> objectType;

    private final Map<String, String> constraints;

    /**
     * Constructs a new <code>ValidatorConfiguration</code>.
     * 
     * @param objectType the object type the validator is intended for
     * @param constraints the constraints the validator shall be initialized with
     * @return a validator configuration
     */
    public static ValidatorConfiguration create(Class<?> objectType, Map<String, String> constraints) {
        return new ValidatorConfiguration(objectType, constraints);
    }

    private ValidatorConfiguration(Class<?> objectType, Map<String, String> constraints) {
        this.objectType = objectType;
        this.constraints = constraints;
    }

    /**
     * Returns the object type the validator is intended for.
     * 
     * @param <T> the expected type
     * @return the object type
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getObjectType() {
        return (Class<T>) this.objectType;
    }

    /**
     * @return the constraints the validator shall be initialized with.
     */
    public Map<String, String> getConstraints() {
        return this.constraints;
    }

}
