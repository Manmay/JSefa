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
 * A mapping between an XML attribute node and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * A node mapping consists of<br>
 * 1. a data type name:<br>
 * a) the name of the implicit parent node of the node this mapping is for - if this node is an element of an
 * implicit list<br>
 * b) the name of the node this mapping is for - in all other cases<br>
 * 2. an object type: the type of the java object this mapping is for<br>
 * 3. a field descriptor: describes the java object field the node maps to<br>
 * 4. a node descriptor: describes the node the java object maps to<br>
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <T> the type of the node descriptor
 */
public abstract class NodeMapping<T extends NodeDescriptor> {
    private final QName dataTypeName;

    private final Class<?> objectType;

    private final FieldDescriptor fieldDescriptor;

    private final T nodeDescriptor;

    /**
     * Constructs a new <code>NodeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param nodeDescriptor the node descriptor
     * @param objectType the object type
     * @param fieldDescriptor the field descriptor
     */
    protected NodeMapping(QName dataTypeName, T nodeDescriptor, Class<?> objectType,
            FieldDescriptor fieldDescriptor) {
        this.nodeDescriptor = nodeDescriptor;
        this.objectType = objectType;
        this.dataTypeName = dataTypeName;
        this.fieldDescriptor = fieldDescriptor;
    }

    /**
     * Returns the node descriptor.
     * 
     * @return the node descriptor
     */
    public T getNodeDescriptor() {
        return this.nodeDescriptor;
    }

    /**
     * Returns the object type.
     * 
     * @return the object type
     */
    public Class<?> getObjectType() {
        return this.objectType;
    }

    /**
     * Returns the data type name of the node.
     * 
     * @return the data type name
     */
    public QName getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * Returns the descriptor of the java object field the node maps to.
     * 
     * @return a field descriptor or null
     */
    public FieldDescriptor getFieldDescriptor() {
        return this.fieldDescriptor;
    }

}
