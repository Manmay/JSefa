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

package org.jsefa.common.config;

import static org.jsefa.common.config.CommonInitialConfigurationParameters.OBJECT_ACCESSOR_PROVIDER_CLASS;
import static org.jsefa.common.config.CommonInitialConfigurationParameters.SIMPLE_TYPE_CONVERTER_PROVIDER_CLASS;

import java.util.ArrayList;
import java.util.Collection;

import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.Serializer;
import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.accessor.ReflectionBasedObjectAccessorProvider;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.converter.provider.SimpleTypeConverterProviderImpl;
import org.jsefa.common.mapping.EntryPoint;
import org.jsefa.common.mapping.TypeMappingRegistry;
import org.jsefa.common.util.ReflectionUtil;

/**
 * The abstract superclass for configuration object classes. It uses lazy
 * initialization.
 * <p>
 * A configuration object is used when creating a new {@link IOFactory}. One
 * configuration object can be used for the creation of multiple factories as
 * each new factory holds its own copy of the configuration object. So the
 * configuration object can be changed after creating a factory with it without
 * affecting the configuration of the factory.
 * 
 * @param <T> the type of the TypeMappingRegistry
 * @param <E> the type of the EntryPoint
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class Configuration<T extends TypeMappingRegistry<?>, E extends EntryPoint<?, ?>> {

    private ObjectAccessorProvider objectAccessorProvider;

    private SimpleTypeConverterProvider simpleTypeConverterProvider;

    private T typeMappingRegistry;

    private Collection<E> entryPoints;

    /**
     * Constructs a new <code>Configuration</code>.
     */
    protected Configuration() {
    }

    /**
     * Constructs a new <code>Configuration</code> as a copy of the given one.
     * 
     * @param other the other config
     */
    @SuppressWarnings("unchecked")
    protected Configuration(Configuration<T, E> other) {
        setObjectAccessorProvider(other.getObjectAccessorProvider());
        getSimpleTypeConverterProvider().registerAll(other.getSimpleTypeConverterProvider());
        setTypeMappingRegistry((T) other.getTypeMappingRegistry().createCopy());
        setEntryPoints(new ArrayList<E>(other.getEntryPoints()));
    }

    /**
     * Returns the type mapping registry.
     * 
     * @return the type mapping registry
     */
    public final T getTypeMappingRegistry() {
        if (this.typeMappingRegistry == null) {
            this.typeMappingRegistry = createDefaultTypeMappingRegistry();
        }
        return this.typeMappingRegistry;
    }

    /**
     * Sets the type mapping registry.
     * 
     * @param typeMappingRegistry a type mapping registry
     */
    public final void setTypeMappingRegistry(T typeMappingRegistry) {
        this.typeMappingRegistry = typeMappingRegistry;
    }

    /**
     * Returns the entry points. An entry point is required for every type of
     * object which will be passed to {@link Serializer#write} or which should
     * be returned from {@link Deserializer#next} and only for these objects
     * (not for the objects related to these ones). If more than one entry point
     * is defined for the same data type name, then <br>
     * a) the last one is used for serialization<br>
     * b) all are used for deserialization whereas their respective designators
     * are used as alternative designators for the same data type.
     * 
     * @return the entry points.
     */
    public final Collection<E> getEntryPoints() {
        if (this.entryPoints == null) {
            this.entryPoints = new ArrayList<E>();
        }
        return this.entryPoints;
    }

    /**
     * Sets the entry points.
     * 
     * @param entryPoints the entry points
     */
    public final void setEntryPoints(Collection<E> entryPoints) {
        this.entryPoints = entryPoints;
    }

    /**
     * Returns the object accessor provider.
     * 
     * @return the object accessor provider
     */
    public final ObjectAccessorProvider getObjectAccessorProvider() {
        if (this.objectAccessorProvider == null) {
            Class<ObjectAccessorProvider> theClass = InitialConfiguration.get(OBJECT_ACCESSOR_PROVIDER_CLASS,
                    ReflectionBasedObjectAccessorProvider.class);
            this.objectAccessorProvider = ReflectionUtil.createInstance(theClass);
        }
        return this.objectAccessorProvider;
    }

    /**
     * Returns the simple type converter provider.
     * 
     * @return the simple type converter provider
     */
    public final SimpleTypeConverterProvider getSimpleTypeConverterProvider() {
        if (this.simpleTypeConverterProvider == null) {
            Class<SimpleTypeConverterProvider> theClass = InitialConfiguration.get(SIMPLE_TYPE_CONVERTER_PROVIDER_CLASS,
                    SimpleTypeConverterProviderImpl.class);
            this.simpleTypeConverterProvider = ReflectionUtil.createInstance(theClass);
        }
        return this.simpleTypeConverterProvider;
    }

    /**
     * Sets the <code>ObjectAccessorProvider</code>.
     * 
     * @param objectAccessorProvider the <code>ObjectAccessorProvider</code>
     */
    public final void setObjectAccessorProvider(ObjectAccessorProvider objectAccessorProvider) {
        this.objectAccessorProvider = objectAccessorProvider;
    }

    /**
     * Sets the <code>SimpleTypeConverterProvider</code>.
     * 
     * @param simpleTypeConverterProvider the
     *                <code>SimpleTypeConverterProvider</code>
     */
    public final void setSimpleTypeConverterProvider(SimpleTypeConverterProvider simpleTypeConverterProvider) {
        this.simpleTypeConverterProvider = simpleTypeConverterProvider;
    }

    /**
     * Creates a copy of this <code>Configuration</code>.
     * 
     * @return a copy of this <code>Configuration</code>
     */
    public abstract Configuration<T, E> createCopy();

    /**
     * Creates the default type mapping registry to be used if none is
     * explicitly given.
     * 
     * @return the default type mapping registry
     */
    protected abstract T createDefaultTypeMappingRegistry();

}
