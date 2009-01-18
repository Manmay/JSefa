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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The abstract base class for implementations of {@link ObjectAccessorProvider}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class AbstractObjectAccessorProvider implements ObjectAccessorProvider {

    private final ConcurrentMap<Class<?>, ObjectAccessor> objectAccessors;

    /**
     * Constructs a new <code>AbstractObjectAccessorProvider</code>.
     */
    protected AbstractObjectAccessorProvider() {
        this.objectAccessors = new ConcurrentHashMap<Class<?>, ObjectAccessor>();
    }
    
    /**
     * {@inheritDoc}
     */
    public ObjectAccessor get(Class<?> objectType) {
        Class<?> aClass = objectType;
        if (objectType.isInterface()) {
            aClass = getImplementor(aClass);
        }
        if (aClass == null) {
            return null;
        }
        ObjectAccessor objectAccessor = this.objectAccessors.get(aClass);
        if (objectAccessor == null) {
            synchronized (this) {
                objectAccessor = create(aClass);
                this.objectAccessors.put(aClass, objectAccessor);
            }
        }
        return objectAccessor;
    }
    
    /**
     * Creates an <code>ObjectAccessor</code> for the given object type.
     * @param objectType the object type
     * @return an object accessor
     */
    protected abstract ObjectAccessor create(Class<?> objectType);


    /**
     * Returns an implementation for the given interface.
     * 
     * @param anInterface the interface
     * @return a class implementing the given interface or null.
     */
    private Class<?> getImplementor(Class<?> anInterface) {
        if (List.class.isAssignableFrom(anInterface)) {
            return ArrayList.class;
        }
        if (Set.class.isAssignableFrom(anInterface)) {
            return HashSet.class;
        }
        if (Queue.class.isAssignableFrom(anInterface)) {
            return LinkedList.class;
        }
        if (Map.class.isAssignableFrom(anInterface)) {
            return HashMap.class;
        }
        return null;
    }

}
