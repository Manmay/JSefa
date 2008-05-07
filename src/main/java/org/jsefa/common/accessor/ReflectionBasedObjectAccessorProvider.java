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

package org.jsefa.common.accessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jsefa.common.util.ReflectionUtil;

/**
 * Reflection based implementation of {@link ObjectAccessorProvider}.
 * <p>
 * This implementation is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ReflectionBasedObjectAccessorProvider extends AbstractObjectAccessorProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectAccessor create(Class<?> objectType) {
        return new ReflectionBasedObjectAccessor(objectType);
    }

    private static final class ReflectionBasedObjectAccessor implements ObjectAccessor {
        private final Map<String, Field> fields;

        private final Constructor<?> constructor;

        private ReflectionBasedObjectAccessor(Class<?> objectType) {
            this.constructor = ReflectionUtil.getDefaultConstructor(objectType);
            this.constructor.setAccessible(true);
            this.fields = new HashMap<String, Field>();
            for (Field field : ReflectionUtil.getAllFields(objectType)) {
                field.setAccessible(true);
                this.fields.put(field.getName(), field);
            }
        }

        public Object createObject() {
            try {
                return this.constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void setValue(Object object, String fieldName, Object value) {
            Field field = this.fields.get(fieldName);
            try {
                field.set(object, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Object getValue(Object object, String fieldName) {
            Field field = this.fields.get(fieldName);
            try {
                return field.get(object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
