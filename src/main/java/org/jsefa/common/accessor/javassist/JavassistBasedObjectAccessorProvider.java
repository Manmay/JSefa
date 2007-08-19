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

package org.jsefa.common.accessor.javassist;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;

import org.jsefa.common.accessor.ObjectAccessException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.accessor.ReflectionBasedObjectAccessorProvider;
import org.jsefa.common.util.ReflectionUtil;

/**
 * Javassist based implementation of {@link ObjectAccessorProvider} which uses
 * {@link ReflectionBasedObjectAccessorProvider} as a fallback (for private
 * attributes and for those with primitive types).
 * <p>
 * This implementation is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class JavassistBasedObjectAccessorProvider implements ObjectAccessorProvider {
    private final ConcurrentMap<Class<?>, ObjectAccessor> objectAccessors;

    private static JavassistBasedObjectAccessorProvider instance;

    /**
     * Returns the single <code>JavassistBasedObjectAccessorProvider</code>.
     * 
     * @return the <code>JavassistBasedObjectAccessorProvider</code>.
     */
    public static synchronized JavassistBasedObjectAccessorProvider getInstance() {
        if (instance == null) {
            instance = new JavassistBasedObjectAccessorProvider();
        }
        return instance;
    }

    private JavassistBasedObjectAccessorProvider() {
        this.objectAccessors = new ConcurrentHashMap<Class<?>, ObjectAccessor>();
    }

    /**
     * {@inheritDoc}
     */
    public ObjectAccessor get(Class<?> objectType) {
        if (objectType.isInterface()) {
            return null;
        }
        ObjectAccessor objectAccessor = this.objectAccessors.get(objectType);
        if (objectAccessor == null) {
            synchronized (this) {
                objectAccessor = create(objectType);
                this.objectAccessors.put(objectType, objectAccessor);
            }
        }
        return objectAccessor;
    }

    private ObjectAccessor create(Class<?> objectType) {
        try {
            ClassPool pool = ClassPool.getDefault();

            CtClass aClass = pool.makeClass(objectType.getName() + "FACTORY");
            aClass.addInterface(pool.get(ObjectAccessor.class.getName()));

            addFields(aClass);
            addConstructor(aClass, objectType);
            addCreateMethod(aClass, objectType);
            addGetMethod(aClass, objectType);
            addSetMethod(aClass, objectType);

            return (ObjectAccessor) aClass.toClass().newInstance();
        } catch (Exception e) {
            throw new ObjectAccessException("Could not create dynamic object accessor for type "
                    + objectType.getName());
        }
    }

    private void addFields(CtClass aClass) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtField mapField = new CtField(pool.get("java.util.Map"), "fieldAccessors", aClass);
        aClass.addField(mapField);
        CtField fallbackObjectAccessor = new CtField(pool.get(ObjectAccessor.class.getName()),
                "fallbackObjectAccessor", aClass);
        aClass.addField(fallbackObjectAccessor);
    }

    private void addConstructor(CtClass aClass, Class<?> objectType) throws Exception {
        CtConstructor constructor = CtNewConstructor.defaultConstructor(aClass);
        StringBuilder constructorBody = new StringBuilder();
        constructorBody.append("{ fieldAccessors = new java.util.HashMap();\n");
        for (Field field : ReflectionUtil.getAllFields(objectType)) {
            if (!Modifier.isPrivate(field.getModifiers()) && !field.getType().isPrimitive()) {
                Class<FieldAccessor> fieldAccessorClass = FieldAccessorFactory.createClass(field, objectType);
                constructorBody.append("fieldAccessors.put(\"" + field.getName() + "\", new "
                        + fieldAccessorClass.getName() + "());\n");
            }
        }
        constructorBody.append("fallbackObjectAccessor = (new "
                + ReflectionBasedObjectAccessorProvider.class.getName() + "()).get(" + objectType.getName()
                + ".class);");
        constructorBody.append("}");
        constructor.setBody(constructorBody.toString());
        aClass.addConstructor(constructor);
    }

    private void addCreateMethod(CtClass aClass, Class<?> objectType) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtMethod createMethod = new CtMethod(pool.get("java.lang.Object"), "createObject", new CtClass[]{}, aClass);
        createMethod.setBody("{ return new " + objectType.getName() + "();}");
        aClass.addMethod(createMethod);
    }

    private void addGetMethod(CtClass aClass, Class<?> objectType) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtMethod getMethod = new CtMethod(pool.get("java.lang.Object"), "getValue", new CtClass[]{
                pool.get("java.lang.Object"), pool.get("java.lang.String")}, aClass);
        StringBuilder getBody = new StringBuilder();
        getBody.append("{ " + FieldAccessor.class.getName() + " fieldAccessor = (" + FieldAccessor.class.getName()
                + ") fieldAccessors.get($2);");
        getBody.append("if (fieldAccessor != null) return fieldAccessor.getValue($1);\n");
        getBody.append("return fallbackObjectAccessor.getValue($1, $2);}");
        getMethod.setBody(getBody.toString());
        aClass.addMethod(getMethod);
    }

    private void addSetMethod(CtClass aClass, Class<?> objectType) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtMethod setMethod = new CtMethod(CtClass.voidType, "setValue", new CtClass[]{
                pool.get("java.lang.Object"), pool.get("java.lang.String"), pool.get("java.lang.Object")}, aClass);
        StringBuilder setBody = new StringBuilder();
        setBody.append("{ " + FieldAccessor.class.getName() + " fieldAccessor = (" + FieldAccessor.class.getName()
                + ") fieldAccessors.get($2);");
        setBody.append("if (fieldAccessor != null) { fieldAccessor.setValue($1, $3); }\n");
        setBody.append("else {fallbackObjectAccessor.setValue($1, $2, $3);}}");
        setMethod.setBody(setBody.toString());
        aClass.addMethod(setMethod);
    }

}
