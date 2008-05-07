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

package org.jsefa.common.mapping;

import org.jsefa.common.validator.Validator;

/**
 * A mapping between a node of the specific format and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * A node mapping consists of<br>
 * 1. a data type name: the name of the data type of this node or - if {@link #isIndirectMapping()} - of its
 * virtual parent node<br>
 * 2. an object type: the type of the java object this mapping is for<br>
 * 3. a field descriptor: describes the java object field the node maps to<br>
 * 4. a node descriptor: describes the node the java object maps to<br>
 * 5. a validator: optional
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <D> the type of the data type name
 * @param <N> the type of the node descriptor
 * 
 */
public abstract class NodeMapping<D, N extends NodeDescriptor<?>> {
    private final D dataTypeName;

    private final Class<?> objectType;

    private final FieldDescriptor fieldDescriptor;

    private final N nodeDescriptor;

    private final Validator validator;

    /**
     * Constructs a new <code>NodeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param nodeDescriptor the node descriptor
     * @param objectType the object type
     * @param fieldDescriptor the field descriptor
     * @param validator the validator; may be null
     */
    public NodeMapping(D dataTypeName, N nodeDescriptor, Class<?> objectType, FieldDescriptor fieldDescriptor,
            Validator validator) {
        this.dataTypeName = dataTypeName;
        this.nodeDescriptor = nodeDescriptor;
        this.objectType = objectType;
        this.fieldDescriptor = fieldDescriptor;
        this.validator = validator;
    }

    /**
     * @return the data type name
     */
    public final D getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * @return the node descriptor
     */
    public final N getNodeDescriptor() {
        return this.nodeDescriptor;
    }

    /**
     * @return the object type
     */
    public final Class<?> getObjectType() {
        return this.objectType;
    }

    /**
     * @return the field descriptor
     */
    public final FieldDescriptor getFieldDescriptor() {
        return this.fieldDescriptor;
    }

    /**
     * @return the validator
     */
    public final Validator getValidator() {
        return this.validator;
    }

    /**
     * Signals if this is an indirect mapping. This is the case if the data type name is not the one of this node
     * but of its virtual parent node instead.
     * 
     * @return true, if this is an indirect mapping; false otherwise.
     */
    public boolean isIndirectMapping() {
        return false;
    }

}
