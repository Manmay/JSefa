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

/**
 * A mapping between a java object type and a data type of the target format. The latter type is denoted by its
 * name which has a generic type.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <T> the type of the data type name
 */
public abstract class TypeMapping<T> {
    private final Class<?> objectType;

    private final T dataTypeName;

    /**
     * Constructs a new <code>TypeMapping</code>.
     * 
     * @param objectType the object type.
     * @param dataTypeName the data type name.
     */
    public TypeMapping(Class<?> objectType, T dataTypeName) {
        this.objectType = objectType;
        this.dataTypeName = dataTypeName;
    }

    /**
     * Returns the object type this mapping is for.
     * 
     * @return the object type
     */
    public final Class<?> getObjectType() {
        return this.objectType;
    }

    /**
     * Returns the name of the data type this mapping is for.
     * 
     * @return the data type name
     */
    public final T getDataTypeName() {
        return this.dataTypeName;
    }

}
