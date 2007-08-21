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

package org.jsefa.common.converter.provider;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jsefa.common.converter.ConversionException;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.SimpleTypeConverterConfiguration;
import org.jsefa.common.util.ReflectionUtil;

/**
 * Provider for {@link SimpleTypeConverter}.
 * <p>
 * Each <code>SimpleTypeConverter</code> must have a static factory method
 * <code>create</code>, which is either parameterless or with exactly one
 * parameter of type {@link SimpleTypeConverterConfiguration}.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class SimpleTypeConverterProvider {
    private final ConcurrentMap<Class<?>, Class<? extends SimpleTypeConverter>> converterTypeMap;

    /**
     * Constructs a <code>SimpleTypeConverterProvider</code>.
     */
    protected SimpleTypeConverterProvider() {
        this.converterTypeMap = new ConcurrentHashMap<Class<?>, Class<? extends SimpleTypeConverter>>();
        registerStandardConverterClasses();
    }

    /**
     * Returns true if and only if this provider has a
     * <code>SimpleTypeConverter</code> for the given object type.
     * 
     * @param objectType the type of the object a converter is needed for
     * @return true if this provider has a <code>SimpleTypeConverter</code>
     *         for the given type; false otherwise.
     */
    public boolean hasConverterFor(Class<?> objectType) {
        return getConverterClass(objectType) != null;
    }

    /**
     * Returns a <code>SimpleTypeConverter</code> for the given object type
     * and format.
     * 
     * @param objectType the type of the object a converter is needed for
     * @param format the format the converter must be initialized with
     * @return the converter.
     */
    public SimpleTypeConverter getForObjectType(Class<?> objectType, String... format) {
        if (!hasConverterFor(objectType)) {
            return null;
        }
        Class<? extends SimpleTypeConverter> converterType = getConverterClass(objectType);
        return getForConverterType(converterType, objectType, format);
    }

    /**
     * Returns an instance of the given <code>SimpleTypeConverter</code> type
     * initialized with the given format.
     * 
     * @param converterType the <code>SimpleTypeConverter</code> type
     * @param objectType the type of the object a converter is needed for
     * @param format the format to initialize the converter with
     * @return the converter
     */
    public SimpleTypeConverter getForConverterType(Class<? extends SimpleTypeConverter> converterType,
            Class<?> objectType, String... format) {
        try {
            Method factoryMethod = ReflectionUtil.getMethod(converterType, "create",
                    SimpleTypeConverterConfiguration.class);
            if (factoryMethod != null) {
                return (SimpleTypeConverter) factoryMethod.invoke(null, SimpleTypeConverterConfiguration.create(
                        objectType, format));
            }
            factoryMethod = ReflectionUtil.getMethod(converterType, "create");
            if (factoryMethod != null) {
                return (SimpleTypeConverter) factoryMethod.invoke(null, (Object[]) null);
            }
            throw new ConversionException("No static create method found for class " + converterType);
        } catch (Exception e) {
            throw new ConversionException("Could not create a SimpleTypeConverter for class " + converterType, e);
        }
    }

    /**
     * Registers the given <code>SimpleTypeConverter</code> type as being
     * responsible for values of the given object type.
     * 
     * @param objectType the object type
     * @param converterType the <code>SimpleTypeConverter</code> type
     */
    public void registerConverterClass(Class<?> objectType, Class<? extends SimpleTypeConverter> converterType) {
        this.converterTypeMap.put(objectType, converterType);
    }

    /**
     * Merges the content of the other SimpleTypeConverter into this one.
     * <p>
     * This overrides existing registrations of converter classes for data type
     * names which are known to both simple type converters.
     * 
     * @param other a simple type converter
     */
    public void registerAll(SimpleTypeConverterProvider other) {
        this.converterTypeMap.putAll(other.converterTypeMap);
    }

    /**
     * Registers the standard converter classes.
     */
    protected abstract void registerStandardConverterClasses();

    private Class<? extends SimpleTypeConverter> getConverterClass(Class<?> objectType) {
        Class<?> type = objectType;
        while (type != null) {
            Class<? extends SimpleTypeConverter> converterType = this.converterTypeMap.get(type);
            if (converterType != null) {
                return converterType;
            }
            type = type.getSuperclass();
        }
        return null;
    }

}
