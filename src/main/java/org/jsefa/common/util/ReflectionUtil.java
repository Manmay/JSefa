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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * type, the second its superclass etc. Returns an empty collection if the given object type is an interface or
     * is primitive. The class {@link Object} is not included.
     * 
     * @param objectType the object type
     * @return a list of classes
     */
    @SuppressWarnings("unchecked")
    public static List<Class<?>> getTypesInReverseOrder(Class<?> objectType) {
        if (objectType.isInterface() || objectType.isPrimitive()) {
            return Collections.EMPTY_LIST;
        }
        List<Class<?>> result = new ArrayList<Class<?>>();
        Class<?> type = objectType;
        while (!type.equals(Object.class)) {
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
    public static Method getMethod(Class<?> objectType, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = objectType.getDeclaredMethod(methodName, parameterTypes);
            if (method == null) {
                method = objectType.getMethod(methodName, parameterTypes);
            }
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
            Method method = getMethod(object.getClass(), methodName, new Class[]{});
            return (T) callMethod(object, method, new Object[]{});
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
            method.setAccessible(true);
            return (T) method.invoke(object, parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the actual type parameter with the given index of the given field or null if it does not exist.
     * 
     * @param field the field
     * @param index the index of the requested actual type parameter
     * @return a class or null
     */
    public static Class<?> getActualTypeParameter(Field field, int index) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (index < typeArgs.length) {
                Type typeArg = typeArgs[index];
                if (typeArg instanceof Class) {
                    return (Class<?>) typeArg;
                }
            }
        }
        return null;
    }

    /**
     * Returns the value mapped to the given object type or one of its supertypes (classes or interfaces) using a
     * nearest match algorithm. I. e. it returns the value mapped to the object type nearest to the given one.
     * <p>
     * The used "nearest" relation has the following properties:<br>
     * 1. An object type is the nearest object type to itself.<br>
     * 2. Any direct super type (class or interface) is nearer to a given type then any indirect super type.
     * 3. A direct super class of a class A is nearer than any interface implemented by A.<br>
     * 4. For two interfaces I1 and I2 directly implemented by a given class A, I1 is nearer to A than I2 if I1 is
     * mentioned before I2 in the the <code>implements</code> clause of the declaration of the class.<br>
     * 
     * @param <T> the expected type of the return value
     * @param objectType the object type
     * @param map the map
     * @return an instance of T or null
     */
    public static <T> T getNearest(Class<?> objectType, Map<Class<?>, T> map) {
        T value = map.get(objectType);
        if (value == null) {
            LinkedList<Class<?>> types = new LinkedList<Class<?>>();
            Class<?> type = objectType;
            while (value == null) {
                Class<?> superType = type.getSuperclass();
                if (superType != null && superType != Object.class) {
                    types.add(superType);
                }
                for (Class<?> anInterface : type.getInterfaces()) {
                    types.add(anInterface);
                }
                if (!types.isEmpty()) {
                    type = types.removeFirst();
                    value = map.get(type);
                } else {
                    break;
                }
            }
        }
        return value;
    }
    
    /**
     * Checks whether a given Class is available on the current platform.
     * 
     * @param clazz The full qualified name of the class to check.
     * @return      <code>true</code> if class is available, <code>false</code> otherwise.
     */
    public static boolean hasClass(final String clazz) {
        try {
            return Class.forName(clazz) != null;
        } catch (final ClassNotFoundException exception) {
            return false;
        }
    }

    /**
     * Returns a Class object which represents the class with the given name.
     * 
     * @param clazz The full qualified name of the class.
     * @return      The Class object representing the class with the given name.
     */
    public static Class<?> getClass(final String clazz) {
        try {
            return Class.forName(clazz);
        } catch (final ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }    

    private ReflectionUtil() {
    }
}
