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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.mapping.TypeMapping;
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
public final class XmlComplexTypeMapping extends TypeMapping<QName> {
    private final EnumMap<NodeType, List<String>> fieldNamesByNodeType;

    private final Map<NodeDescriptor, NodeMapping<?>> nodeMappingsByNodeDescriptor;

    private final Map<FieldDescriptor, NodeMapping<?>> nodeMappingsByFieldDescriptor;

    private final ObjectAccessor objectAccessor;

    private final boolean textContentAllowed;

    /**
     * Constructs a new <code>XmlComplexTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param objectAccessor the object accessor
     * @param nodeMappings the node mappings
     */
    public XmlComplexTypeMapping(Class<?> objectType, QName dataTypeName, ObjectAccessor objectAccessor,
            Collection<NodeMapping<?>> nodeMappings) {
        super(objectType, dataTypeName);
        this.objectAccessor = objectAccessor;
        this.fieldNamesByNodeType = createFieldNamesByNodeType(nodeMappings);
        this.nodeMappingsByNodeDescriptor = XmlTypeMappingUtil.createNodeMappingsByNodeDescriptorMap(nodeMappings);
        this.nodeMappingsByFieldDescriptor = createNodeMappingsByFieldDescriptorMap(nodeMappings);
        this.textContentAllowed = this.fieldNamesByNodeType.get(NodeType.TEXT_CONTENT) != null;
    }

    /**
     * Returns true if and only if a text content is allowed for this data type.
     * 
     * @return true, if a text content is allowed for this data type; false otherwise.
     */
    public boolean isTextContentAllowed() {
        return this.textContentAllowed;
    }

    /**
     * Returns the names of all fields which maps to a node of the given node type.
     * 
     * @param nodeType the node type
     * @return the list of fields (the order does matter).
     */
    public List<String> getFieldNames(NodeType nodeType) {
        List<String> result = this.fieldNamesByNodeType.get(nodeType);
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

    /**
     * Returns the node mapping for the given field descriptor.
     * 
     * @param <T> the expected type of the node mapping
     * @param fieldDescriptor the field descriptor
     * @return the node mapping.
     */
    @SuppressWarnings("unchecked")
    public <T extends NodeMapping<?>> T getNodeMapping(FieldDescriptor fieldDescriptor) {
        T result = (T) this.nodeMappingsByFieldDescriptor.get(fieldDescriptor);
        if (result == null) {
            Class<?> type = fieldDescriptor.getObjectType().getSuperclass();
            while (type != null) {
                result = (T) this.nodeMappingsByFieldDescriptor.get(new FieldDescriptor(fieldDescriptor.getName(),
                        type));
                if (result != null) {
                    break;
                }
                type = type.getSuperclass();
            }
        }
        return result;
    }

    /**
     * Returns the node mapping for the given node descriptor.
     * 
     * @param <T> the expected type of the node mapping
     * @param nodeDescriptor the descriptor of the node
     * @return the node mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends NodeMapping> T getNodeMapping(NodeDescriptor nodeDescriptor) {
        return (T) this.nodeMappingsByNodeDescriptor.get(nodeDescriptor);
    }

    /**
     * Returns the object accessor.
     * 
     * @return the object accessor.
     */
    public ObjectAccessor getObjectAccessor() {
        return this.objectAccessor;
    }

    private EnumMap<NodeType, List<String>> createFieldNamesByNodeType(Collection<NodeMapping<?>> nodeMappings) {
        EnumMap<NodeType, List<String>> result = new EnumMap<NodeType, List<String>>(NodeType.class);
        for (NodeMapping<?> nodeMapping : nodeMappings) {
            NodeType nodeType = nodeMapping.getNodeDescriptor().getType();
            List<String> fieldNames = result.get(nodeType);
            if (fieldNames == null) {
                fieldNames = new ArrayList<String>();
                result.put(nodeType, fieldNames);
            }
            String fieldName = nodeMapping.getFieldDescriptor().getName();
            if (!fieldNames.contains(fieldName)) {
                fieldNames.add(fieldName);
            }
        }
        return result;
    }

    private Map<FieldDescriptor, NodeMapping<?>> createNodeMappingsByFieldDescriptorMap(
            Collection<NodeMapping<?>> nodeMappings) {
        Map<FieldDescriptor, NodeMapping<?>> result = new HashMap<FieldDescriptor, NodeMapping<?>>();
        for (NodeMapping<?> nodeMapping : nodeMappings) {
            result.put(nodeMapping.getFieldDescriptor(), nodeMapping);
        }
        return result;
    }

}
