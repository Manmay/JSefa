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

package org.jsefa.common.mapping;

import org.jsefa.common.converter.SimpleTypeConverter;

/**
 * A mapping between a java object type and a simple data type.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 * @param <N> the type of the data type name
 */
public abstract class SimpleTypeMapping<N> extends TypeMapping<N> {

    private final SimpleTypeConverter simpleTypeConverter;

    /**
     * Constructs a new <code>SimpleTypeMapping</code> from the given arguments.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param simpleTypeConverter the simple type converter
     */
    public SimpleTypeMapping(Class<?> objectType, N dataTypeName, SimpleTypeConverter simpleTypeConverter) {
        super(objectType, dataTypeName);
        this.simpleTypeConverter = simpleTypeConverter;
    }

    /**
     * Returns the simple type converter.
     * 
     * @return the simple type converter
     */
    public final SimpleTypeConverter getSimpleTypeConverter() {
        return this.simpleTypeConverter;
    }

}
