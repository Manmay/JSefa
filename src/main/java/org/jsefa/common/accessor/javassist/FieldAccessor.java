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

/**
 * An accessor to a field of an object for which this <code>FieldAccessor</code> was created for.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface FieldAccessor {

    /**
     * Returns the value of the field of the given object.
     * 
     * @param object the object
     * @return the field value
     */
    Object getValue(Object object);

    /**
     * Sets the value of the field of the given object.
     * 
     * @param object the object
     * @param value the value to be set
     */
    void setValue(Object object, Object value);

}
