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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A registry for {@link TypeMapping}s. It provides the registration of {@link TypeMapping}s under their
 * respective data type name and the retrieval of them given their data type name.
 * <p>
 * It is thread-safe and all subclasses should be thread-safe, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <D> the type of the data type name.
 */
public abstract class TypeMappingRegistry<D> {

    private final ConcurrentMap<D, TypeMapping<D>> typeMappings;

    /**
     * Constructs a new <code>TypeMappingRegistry</code>.
     */
    public TypeMappingRegistry() {
        this.typeMappings = new ConcurrentHashMap<D, TypeMapping<D>>();
    }

    /**
     * Constructs a new <code>TypeMappingRegistry</code> as a copy of the given one.
     * 
     * @param other the registry to copy
     */
    protected TypeMappingRegistry(TypeMappingRegistry<D> other) {
        this();
        this.typeMappings.putAll(other.typeMappings);
    }

    /**
     * Registers the given type mapping under its data type name.
     * 
     * @param typeMapping the type mapping
     */
    public final void register(TypeMapping<D> typeMapping) {
        this.typeMappings.put(typeMapping.getDataTypeName(), typeMapping);
    }

    /**
     * Returns the type mapping registered under the given data type name.
     * 
     * @param dataTypeName the data type name
     * @return the type mapping
     */
    public final TypeMapping<D> get(D dataTypeName) {
        return this.typeMappings.get(dataTypeName);
    }

    /**
     * Creates a copy of this <code>TypeMappingRegistry</code>.
     * 
     * @return a copy of this <code>TypeMappingRegistry</code>
     */
    public abstract TypeMappingRegistry<D> createCopy();

}
