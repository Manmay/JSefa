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

import java.util.List;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingRegistry;

/**
 * Abstract super class for factories which can create {@link TypeMapping}s
 * from annotated classes.
 * <p>
 * It is thread safe and all subclasses should be thread-safe, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <D> the type of the data type name
 * @param <R> the type of the type mapping registry
 * 
 */
public abstract class TypeMappingFactory<D, R extends TypeMappingRegistry<D>> {

    private final SimpleTypeConverterProvider simpleTypeConverterProvider;

    private final ObjectAccessorProvider objectAccessorProvider;

    private final R typeMappingRegistry;

    /**
     * Constructs a new <code>TypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be
     *            registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to
     *            use
     * @param objectAccessorProvider the object accessor provider to use
     */
    public TypeMappingFactory(R typeMappingRegistry, SimpleTypeConverterProvider simpleTypeConverterProvider,
            ObjectAccessorProvider objectAccessorProvider) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.simpleTypeConverterProvider = simpleTypeConverterProvider;
        this.objectAccessorProvider = objectAccessorProvider;
    }

    /**
     * Creates a type mapping for the given object type, registers it with the
     * type mapping registry and returns its data type name. The first two steps
     * are omitted if a type mapping is already registered for the given object
     * type.
     * 
     * @param objectType the object type to create a type mapping for.
     * @return the name of the created or found data type.
     */
    public abstract D createIfAbsent(Class<?> objectType);

    /**
     * Returns the type mapping registry.
     * 
     * @return the type mapping registry.
     */
    public final R getTypeMappingRegistry() {
        return typeMappingRegistry;
    }

    /**
     * Returns the object accessor provider.
     * 
     * @return the object accessor provider.
     */
    protected final ObjectAccessorProvider getObjectAccessorProvider() {
        return objectAccessorProvider;
    }

    /**
     * Returns the simple type converter provider.
     * 
     * @return the simple type converter provider.
     */
    protected final SimpleTypeConverterProvider getSimpleTypeConverterProvider() {
        return simpleTypeConverterProvider;
    }

    /**
     * Returns true if and only if the given object type is a simple type.
     * 
     * @param objectType the object type
     * @return true, if the object type is a simple type; false otherwise.
     */
    protected final boolean hasSimpleType(Class<?> objectType) {
        return getSimpleTypeConverterProvider().hasConverterFor(objectType);
    }

    /**
     * Returns true if and only if the given object type is a list type.
     * 
     * @param objectType the object type
     * @return true, if the given object type is a list type; false otherwise.
     */
    protected final boolean hasListType(Class<?> objectType) {
        return List.class.isAssignableFrom(objectType);
    }

    /**
     * Asserts that a type mapping exists for the given data type name.
     * 
     * @param dataTypeName the data type name.
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertTypeMappingExists(D dataTypeName) {
        if (getTypeMappingRegistry().get(dataTypeName) == null) {
            throw new AnnotationException("No type mapping registered for data type name " + dataTypeName);
        }
    }

}
