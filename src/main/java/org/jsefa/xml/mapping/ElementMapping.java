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

package org.jsefa.xml.mapping;

import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.xml.namespace.QName;

/**
 * A mapping between an XML element node and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * Note:<br>
 * 1. Each client must call {@link #finish()} when finishing the construction of an <code>ElementMapping</code>.<br>
 * 2. The construction must be performed by one thread only. <br>
 * 3. After the construction step an instance of this class is immutable and thread-safe.<br>
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ElementMapping extends NodeMapping<ElementDescriptor> {

    private final boolean elementNameIsAmbiguous;

    /**
     * Constructs a new <code>ElementMapping</code>.
     * 
     * @param dataTypeName the name of the data type. This may be the data type name of the element node itself or
     *                of its implicit parent node.
     * @param elementDescriptor the descriptor of the element
     * @param objectType the type of the object. May be different from the object type contained in the field
     *                descriptor.
     * @param fieldDescriptor the descriptor of the field
     * @param elementNameIsAmbiguous true, if the name of the element in its given context (position in the xml
     *                document) is ambiguous, i. e. there is sibling element with the same name; false otherwise.
     */
    public ElementMapping(QName dataTypeName, ElementDescriptor elementDescriptor, Class<?> objectType,
            FieldDescriptor fieldDescriptor, boolean elementNameIsAmbiguous) {
        super(dataTypeName, elementDescriptor, objectType, fieldDescriptor);
        this.elementNameIsAmbiguous = elementNameIsAmbiguous;
    }

    /**
     * Returns true if and only if the name of the element in its given context (position in the xml document) is
     * ambiguous, i. e. there is sibling element with the same name.
     * 
     * @return true, if the name of the element in its given context is ambiguous; false otherwise.
     */
    public boolean elementNameIsAmbiguous() {
        return elementNameIsAmbiguous;
    }

}
