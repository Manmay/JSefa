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

package org.jsefa.rbf.mapping;

import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.validator.Validator;

/**
 * A mapping between an rbf field node and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FieldMapping extends RbfNodeMapping<RbfFieldDescriptor> {

    /**
     * Constructs a new <code>FieldMapping</code>.
     * 
     * @param dataTypename the data type name
     * @param nodeDescriptor the node descriptor
     * @param objectType the object type
     * @param fieldDescriptor the field descriptor
     * @param validator the validator; may be null
     */
    public FieldMapping(String dataTypename, RbfFieldDescriptor nodeDescriptor, Class<?> objectType,
            FieldDescriptor fieldDescriptor, Validator validator) {
        super(dataTypename, nodeDescriptor, objectType, fieldDescriptor, validator);
    }

}
