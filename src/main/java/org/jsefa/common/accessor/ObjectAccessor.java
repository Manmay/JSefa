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

/**
 * An <code>ObjectAccessor</code> provides access to an object (i.e. creates it) and to its fields (retrieval and
 * setting of field values).
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface ObjectAccessor {

    /**
     * Creates a new instance of the class this <code>ObjectAccessor</code> was created for.
     * 
     * @return a new object.
     */
    Object createObject();

    /**
     * Returns the value of the field with the name <code>fieldName</code> of the given object.
     * 
     * @param object the object
     * @param fieldName the field name
     * @return a field value
     */
    Object getValue(Object object, String fieldName);

    /**
     * Sets the value of the field with the name <code>fieldName</code> of the given object.
     * 
     * @param object the object
     * @param fieldName the field name
     * @param value the field value
     */
    void setValue(Object object, String fieldName, Object value);

}
