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

package org.jsefa;

import java.io.Serializable;

/**
 * An element of a path to an object.
 * 
 * @author Norman Lahme-Huetig
 */

public final class ObjectPathElement implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Class<?> objectType;
    
    private final String fieldName;
    
    /**
     * Constructs a new <code>ObjectPathElement</code>.
     * @param objectType the object type
     * @param fieldName the name of a field of the object type
     */
    public ObjectPathElement(Class<?> objectType, String fieldName) {
        this.objectType = objectType;
        this.fieldName = fieldName;
    }

    /**
     * Returns the object type.
     * @return an object type
     */
    public Class<?> getObjectType() {
        return objectType;
    }

    /**
     * Returns the name of the field of the object type.
     * @return a field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.objectType.getSimpleName());
        result.append('[');
        result.append(this.fieldName);
        result.append(']');
        return result.toString();
    }

}
