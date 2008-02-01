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
 * A mapping between an XML text node and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class TextContentMapping extends NodeMapping<TextContentDescriptor> {

    /**
     * Constructs a new <code>TextContentMapping</code> for the given data type name, text content descriptor and
     * field descriptor.
     * 
     * @param dataTypeName the name of the data type.
     * @param textContentDescriptor the descriptor of the attribute node
     * @param fieldDescriptor the descriptor of the field
     */
    public TextContentMapping(QName dataTypeName, TextContentDescriptor textContentDescriptor,
            FieldDescriptor fieldDescriptor) {
        super(dataTypeName, textContentDescriptor, fieldDescriptor.getObjectType(), fieldDescriptor);
    }

}
