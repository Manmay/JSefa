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

package org.jsefa.common.converter;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Provider for {@link SimpleTypeConverter}.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class SimpleTypeConverterProvider {
    private final ConcurrentMap<Class, Class<? extends SimpleTypeConverter>> converterTypeMap;

    /**
     * Constructs a <code>SimpleTypeConverterProvider</code>.
     */
    public SimpleTypeConverterProvider() {
        this.converterTypeMap = new ConcurrentHashMap<Class, Class<? extends SimpleTypeConverter>>();
        registerStandardConverterClasses();
    }

    /**
     * Returns true if and only if this provider has a
     * <code>SimpleTypeConverter</code> for the given value type.
     * 
     * @param valueType the type of the value a converter is needed for
     * @return true if this provider has a <code>SimpleTypeConverter</code>
     *         for the given type; false otherwise.
     */
    public boolean hasConverterFor(Class valueType) {
        return getConverterClass(valueType) != null;
    }

    /**
     * Returns a <code>SimpleTypeConverter</code> for the given value type and
     * format.
     * 
     * @param valueType the type of the value a converter is needed for
     * @param format the format the converter must be initialized with
     * @return the converter.
     */
    public SimpleTypeConverter getForValueType(Class valueType, String... format) {
        if (!hasConverterFor(valueType)) {
            return null;
        }
        Class<? extends SimpleTypeConverter> converterType = getConverterClass(valueType);
        return getForConverterType(converterType, valueType, format);
    }

    /**
     * Returns an instance of the given <code>SimpleTypeConverter</code> type
     * initialized with the given format.
     * <p>
     * Constructors of <code>SimpleTypeConverter</code> must have:
     * <p>
     * If <code>format</code> is not null and not empty:<br>
     * SimpleTypeConverter(String... format) or <br>
     * SimpleTypeConverter(Class valueType, String... format)
     * <p>
     * If <code>format</code> is null or empty:<br>
     * SimpleTypeConverter()<br>
     * SimpleTypeConverter(Class valueType)
     * <p>
     * 
     * @param converterType the <code>SimpleTypeConverter</code> type
     * @param valueType the type of the value a converter is needed for
     * @param format the format to initialize the converter with
     * @return the converter
     */
    public SimpleTypeConverter getForConverterType(Class<? extends SimpleTypeConverter> converterType,
            Class valueType, String... format) {
        try {
            if (format != null && format.length > 0) {
                Constructor constructor = getFormatConstructor(converterType, Class.class);
                if (constructor != null) {
                    Object[] args = new Object[2];
                    args[0] = valueType;
                    args[1] = format;
                    return (SimpleTypeConverter) constructor.newInstance(args);
                }
                constructor = getFormatConstructor(converterType);
                if (constructor != null) {
                    Object[] args = new Object[1];
                    args[0] = format;
                    return (SimpleTypeConverter) constructor.newInstance(args);
                }
                throw new ConversionException("No constructor with String argument found for class "
                        + converterType);
            } else {
                Constructor constructor = getConstructor(converterType, Class.class);
                if (constructor != null) {
                    return (SimpleTypeConverter) constructor.newInstance(valueType);
                }
                return (SimpleTypeConverter) converterType.newInstance();
            }
        } catch (Exception e) {
            throw new ConversionException("Could not create a SimpleTypeConverter for class " + converterType
                    + " and the format " + format, e);
        }
    }

    /**
     * Registers the given <code>SimpleTypeConverter</code> type as being
     * responsible for values of the given value type.
     * <p>
     * Constructors of <code>SimpleTypeConverter</code> must have:
     * <p>
     * If an instance could be created with a non empty <code>format</code>:<br>
     * SimpleTypeConverter(String... format) or <br>
     * SimpleTypeConverter(Class valueType, String... format)
     * <p>
     * Otherwise:<br>
     * SimpleTypeConverter()<br>
     * SimpleTypeConverter(Class valueType)
     * <p>
     * 
     * @param valueType the value type
     * @param converterType the <code>SimpleTypeConverter</code> type
     */
    public void registerConverterClass(Class valueType, Class<? extends SimpleTypeConverter> converterType) {
        this.converterTypeMap.put(valueType, converterType);
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

    private Class<? extends SimpleTypeConverter> getConverterClass(Class valueType) {
        Class type = valueType;
        while (type != null) {
            Class<? extends SimpleTypeConverter> converterType = this.converterTypeMap.get(type);
            if (converterType != null) {
                return converterType;
            }
            type = type.getSuperclass();
        }
        return null;
    }

    private void registerStandardConverterClasses() {
        registerConverterClass(String.class, StringConverter.class);
        registerConverterClass(boolean.class, BooleanConverter.class);
        registerConverterClass(Boolean.class, BooleanConverter.class);
        registerConverterClass(long.class, LongConverter.class);
        registerConverterClass(Long.class, LongConverter.class);
        registerConverterClass(int.class, IntegerConverter.class);
        registerConverterClass(Integer.class, IntegerConverter.class);
        registerConverterClass(BigDecimal.class, BigDecimalConverter.class);
        registerConverterClass(Date.class, DateConverter.class);
        registerConverterClass(XMLGregorianCalendar.class, XMLGregorianCalendarConverter.class);
        registerConverterClass(Enum.class, EnumConverter.class);
    }

    private Constructor getConstructor(Class type, Class... parameterTypes) {
        try {
            return type.getConstructor(parameterTypes);
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Constructor getFormatConstructor(Class type, Class... parameterTypes) {
        Constructor[] constructors = type.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            Class[] types = constructors[i].getParameterTypes();
            if (types.length == parameterTypes.length + 1) {
                if (String.class.equals(types[types.length - 1].getComponentType())) {
                    return constructors[i];
                }
            }
        }
        return null;
    }

}
