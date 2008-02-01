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

package org.jsefa.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for reflection.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ReflectionUtil {

    /**
     * Returns the default constructor of the given object type.
     * 
     * @param objectType the object type
     * @return the default constructor
     */
    public static Constructor<?> getDefaultConstructor(Class<?> objectType) {
        try {
            return objectType.getDeclaredConstructor(new Class[]{});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new instance of the given object type using the default constructor.
     * 
     * @param <T> the type of the object to create
     * @param objectType the object type
     * @return an object
     */
    public static <T> T createInstance(Class<T> objectType) {
        try {
            return objectType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the fields of the given class. These include the inherited fields, too.
     * 
     * @param objectType the class
     * @return the list of fields.
     */
    public static Collection<Field> getAllFields(Class<?> objectType) {
        List<Field> fields = new ArrayList<Field>();
        Class<?> currentObjectType = objectType;
        while (currentObjectType != null) {
            for (Field field : currentObjectType.getDeclaredFields()) {
                fields.add(field);
            }
            currentObjectType = currentObjectType.getSuperclass();
        }
        return fields;
    }

    /**
     * Returns the path of classes to the given object type in reverse order. The first element is the given object
     * type, the second its superclass etc.
     * 
     * @param objectType the object type
     * @return a list of classes
     */
    public static List<Class<?>> getTypesInReverseOrder(Class<?> objectType) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        Class<?> type = objectType;
        while (!type.equals(Object.class) && !type.isInterface() && !type.isPrimitive()) {
            result.add(type);
            type = type.getSuperclass();
        }
        Collections.reverse(result);
        return result;
    }

    /**
     * Returns the method on the objectType with the given name and given parameter types.
     * 
     * @param objectType the object type
     * @param methodName the method name
     * @param parameterTypes the parameter types
     * @return a method or null if it does not exist.
     */
    @SuppressWarnings("unchecked")
    public static Method getMethod(Class<?> objectType, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = objectType.getMethod(methodName, parameterTypes);
            return method;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Calls the parameterless method with the given name on the given object.
     * 
     * @param <T> the expected type of the return value
     * @param object the object to invoke the method on
     * @param methodName the name of the method
     * @return the return value of the method call
     */
    @SuppressWarnings("unchecked")
    public static <T> T callMethod(Object object, String methodName) {
        try {
            Method method = object.getClass().getMethod(methodName, new Class[]{});
            return (T) method.invoke(object, new Object[]{});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls the given method with the given parameters on the given object.
     * 
     * @param <T> the expected type of the return value
     * @param object the object to invoke the method on
     * @param method the method
     * @param parameters the parameters
     * @return the return value of the method call
     */
    @SuppressWarnings("unchecked")
    public static <T> T callMethod(Object object, Method method, Object... parameters) {
        try {
            return (T) method.invoke(object, parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the first argument of the type of the given field or null if it does not exist.
     * 
     * @param field the field
     * @return a class or null
     */
    public static Class<?> getListEntryObjectType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type typeArg = parameterizedType.getActualTypeArguments()[0];
            if (typeArg instanceof Class) {
                return (Class<?>) typeArg;
            }
        }
        return null;
    }

    private ReflectionUtil() {
    }
}
