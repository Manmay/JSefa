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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Factory for creating classes implementing {@link FieldAccessor}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
final class FieldAccessorFactory {
    static Class createClass(Field field, Class containingClass) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass aClass = pool.makeClass(containingClass.getName() + "FACTORY_" + field.getName());
        aClass.addInterface(pool.get(FieldAccessor.class.getName()));

        CtMethod getMethod = new CtMethod(pool.get("java.lang.Object"), "getValue", new CtClass[]{pool
                .get("java.lang.Object")}, aClass);
        getMethod.setBody("{ return ((" + containingClass.getName() + ")$1)." + field.getName() + "; }");
        aClass.addMethod(getMethod);

        CtMethod setMethod = new CtMethod(CtClass.voidType, "setValue", new CtClass[]{
                pool.get("java.lang.Object"), pool.get("java.lang.Object")}, aClass);
        setMethod.setBody("{ ((" + containingClass.getName() + ")$1)." + field.getName() + " = ("
                + field.getType().getName() + ")$2; }");
        aClass.addMethod(setMethod);

        return aClass.toClass();
    }

    private FieldAccessorFactory() {

    }
}
