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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.TypeMapping;

/**
 * A mapping between a java object type and a complex RBF data type.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */

public final class RbfComplexTypeMapping extends TypeMapping<String> {
    private final ObjectAccessor objectAccessor;

    private final EnumMap<NodeType, List<String>> fieldNamesByNodeType;

    private final Map<String, NodeMapping> nodeMappingsByFieldName;

    /**
     * Constructs a new <code>RbfComplexTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param objectAccessor the object accessor
     * @param nodeMappings the node mappings
     */
    public RbfComplexTypeMapping(Class<?> objectType, String dataTypeName, ObjectAccessor objectAccessor,
            Collection<NodeMapping> nodeMappings) {
        super(objectType, dataTypeName);
        this.objectAccessor = objectAccessor;
        this.fieldNamesByNodeType = createFieldNamesByNodeType(nodeMappings);
        this.nodeMappingsByFieldName = createNodeMappingsByFieldNameMap(nodeMappings);
    }

    /**
     * Returns the object accessor.
     * 
     * @return the object accessor.
     */
    public ObjectAccessor getObjectAccessor() {
        return this.objectAccessor;
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
     * Returns the node mapping for the field with the given name.
     * 
     * @param <T> the expected type of the node mapping
     * @param fieldName the field name
     * @return the node mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends NodeMapping> T getNodeMapping(String fieldName) {
        return (T) this.nodeMappingsByFieldName.get(fieldName);
    }

    private Map<String, NodeMapping> createNodeMappingsByFieldNameMap(Collection<NodeMapping> nodeMappings) {
        Map<String, NodeMapping> result = new HashMap<String, NodeMapping>();
        for (NodeMapping nodeMapping : nodeMappings) {
            result.put(nodeMapping.getFieldName(), nodeMapping);
        }
        return result;
    }

    private EnumMap<NodeType, List<String>> createFieldNamesByNodeType(Collection<NodeMapping> nodeMappings) {
        EnumMap<NodeType, List<String>> result = new EnumMap<NodeType, List<String>>(NodeType.class);
        for (NodeMapping nodeMapping : nodeMappings) {
            NodeType nodeType = nodeMapping.getNodeType();
            List<String> fieldNames = result.get(nodeType);
            if (fieldNames == null) {
                fieldNames = new ArrayList<String>();
                result.put(nodeType, fieldNames);
            }
            String fieldName = nodeMapping.getFieldName();
            if (!fieldNames.contains(fieldName)) {
                fieldNames.add(fieldName);
            }
        }
        return result;
    }

}
