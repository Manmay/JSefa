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

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A registry for {@link TypeMapping}s with support for hierarchical relations between the data types.
 * <p>
 * Instances of this class are thread-safe. This must be true for all subclasses, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <T> the type of the data type name
 * 
 */
public abstract class HierarchicalTypeMappingRegistry<T> extends TypeMappingRegistry<T> {

    private final ConcurrentMap<T, Collection<T>> subtypeRelation;

    /**
     * Constructs a new <code>HierarchicalTypeMappingRegistry</code>.
     */
    public HierarchicalTypeMappingRegistry() {
        this.subtypeRelation = new ConcurrentHashMap<T, Collection<T>>();
    }

    /**
     * Constructs a new <code>HierarchicalTypeMappingRegistry</code> as a copy of the given one.
     * 
     * @param other the registry that serves as a model for creating a new one
     */
    protected HierarchicalTypeMappingRegistry(HierarchicalTypeMappingRegistry<T> other) {
        super(other);
        this.subtypeRelation = new ConcurrentHashMap<T, Collection<T>>();
        for (T superDataTypeName : other.subtypeRelation.keySet()) {
            for (T subDataTypeName : other.getSubDataTypeNames(superDataTypeName)) {
                registerSubtypeRelation(superDataTypeName, subDataTypeName);
            }
        }
    }

    /**
     * Registers a subtype relation between the data types denoted by the given names.
     * 
     * @param superDataTypeName the name of the super data type
     * @param subDataTypeName the name of the sub data type
     */
    public final void registerSubtypeRelation(T superDataTypeName, T subDataTypeName) {
        Collection<T> subDataTypeNames = this.subtypeRelation.get(superDataTypeName);
        if (subDataTypeNames == null) {
            subDataTypeNames = new HashSet<T>();
            this.subtypeRelation.put(superDataTypeName, subDataTypeNames);
        }
        subDataTypeNames.add(subDataTypeName);
    }

    /**
     * Returns a collection of all data type names which denote direct or indirect sub data types of the one
     * denoted by rootDataTypeName. The given root data type name is also included.
     * 
     * @param rootDataTypeName the data type name denotig the root of the type hierarchy to retrieve.
     * @return a collection of data type names including the root data type name
     */
    public final Collection<T> getDataTypeNameTreeElements(T rootDataTypeName) {
        Collection<T> result = new HashSet<T>();
        result.add(rootDataTypeName);
        for (T subDataTypeName : getSubDataTypeNames(rootDataTypeName)) {
            result.addAll(getDataTypeNameTreeElements(subDataTypeName));
        }
        return result;
    }

    private Collection<T> getSubDataTypeNames(T dataTypeName) {
        Collection<T> result = this.subtypeRelation.get(dataTypeName);
        if (result == null) {
            result = new HashSet<T>();
        }
        return result;
    }

}
