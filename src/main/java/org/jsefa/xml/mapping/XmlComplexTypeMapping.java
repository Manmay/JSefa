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

import java.util.Collection;
import java.util.Map;

import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.ComplexTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.validator.Validator;
import org.jsefa.xml.namespace.QName;

/**
 * A mapping between a java object type and a XML complex data type.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlComplexTypeMapping extends ComplexTypeMapping<QName, XmlNodeDescriptor, XmlNodeMapping<?>> {
    private final boolean textContentAllowed;

    /**
     * Constructs a new <code>XmlComplexTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param objectAccessor the object accessor
     * @param nodeMappings the node mappings
     * @param validator the validator; may be null
     */
    public XmlComplexTypeMapping(Class<?> objectType, QName dataTypeName, ObjectAccessor objectAccessor,
            Collection<XmlNodeMapping<?>> nodeMappings, Validator validator) {
        super(objectType, dataTypeName, nodeMappings, objectAccessor, validator);
        this.textContentAllowed = !getFieldNames(XmlNodeType.TEXT_CONTENT).isEmpty();
    }

    /**
     * @return true, if a text content is allowed for this data type; false otherwise.
     */
    public boolean isTextContentAllowed() {
        return this.textContentAllowed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<XmlNodeDescriptor, XmlNodeMapping<?>> createNodeMappingsByNodeDescriptorMap(
            Collection<XmlNodeMapping<?>> nodeMappings) {
        return XmlTypeMappingUtil.createNodeMappingsByNodeDescriptorMap(nodeMappings);
    }

}
