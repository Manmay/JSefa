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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.TypeMapping;

/**
 * A mapping between a java object type and a complex RBF data type.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */

public final class RbfComplexTypeMapping extends TypeMapping<String> {
    private final ObjectAccessor objectAccessor;

    private final Map<String, NodeModel> nodeModels;

    private final Map<NodeType, List<String>> fieldNamesByNodeType;

    /**
     * Constructs a new <code>RbfComplexTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param objectAccessor the object accessor
     */
    public RbfComplexTypeMapping(Class<?> objectType, String dataTypeName, ObjectAccessor objectAccessor) {
        super(objectType, dataTypeName);
        this.objectAccessor = objectAccessor;
        this.nodeModels = new HashMap<String, NodeModel>();
        this.fieldNamesByNodeType = new HashMap<NodeType, List<String>>();
    }

    /**
     * Specifies that the field with the given name maps to a node of the given
     * node type and a data type with the given name.
     * 
     * @param fieldName the field name
     * @param nodeType the node type
     * @param dataTypeName the name of its data type
     */
    public void register(String fieldName, NodeType nodeType, String dataTypeName) {
        assertNotFinished();
        List<String> fieldNames = getOrCreateFieldNameList(nodeType);
        if (!fieldNames.contains(fieldName)) {
            fieldNames.add(fieldName);
        }
        NodeModel nodeModel = new NodeModel(dataTypeName, fieldName);
        this.nodeModels.put(fieldName, nodeModel);
    }

    /**
     * Specifies that the field with the given name maps to a node of the given
     * node type, with the given prefix and a data type with the given name.
     * 
     * @param fieldName the field name
     * @param nodeType the node type
     * @param dataTypeName the name of its data type
     * @param prefix the prefix
     */
    public void registerWithPrefix(String fieldName, NodeType nodeType, String dataTypeName, String prefix) {
        assertNotFinished();
        List<String> fieldNames = getOrCreateFieldNameList(nodeType);
        if (!fieldNames.contains(fieldName)) {
            fieldNames.add(fieldName);
        }
        NodeModel nodeModel = new NodeModel(dataTypeName, fieldName, prefix);
        this.nodeModels.put(fieldName, nodeModel);
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
     * Returns the names of all fields which maps to a node of the given node
     * type.
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
     * Returns the node model for the field with the given name.
     * 
     * @param fieldName the field name
     * @return the node model
     */
    public NodeModel getNodeModel(String fieldName) {
        return this.nodeModels.get(fieldName);
    }

    private List<String> getOrCreateFieldNameList(NodeType nodeType) {
        List<String> result = this.fieldNamesByNodeType.get(nodeType);
        if (result == null) {
            result = new ArrayList<String>();
            this.fieldNamesByNodeType.put(nodeType, result);
        }
        return result;
    }

}
