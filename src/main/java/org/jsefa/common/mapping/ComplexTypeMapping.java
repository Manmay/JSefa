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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.Validator;

/**
 * A mapping between a java object type and a complex data type of the exchange format.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 * @param <N> the type of the data type name
 * @param <D> the type of the node descriptor
 * @param <M> the type of the node mapping
 */

public abstract class ComplexTypeMapping<N, D extends NodeDescriptor<?>, M extends NodeMapping<N, ?>> extends
        TypeMapping<N> {

    private final Collection<M> nodeMappings;

    private final Map<D, M> nodeMappingsByNodeDescriptor;
    
    private final Map<String, ?> nodeMappingsByFieldNameAndType;
    
    private final Map<NodeType, List<String>> fieldNamesByNodeType;

    private final ObjectAccessor objectAccessor;

    private final Validator validator;

    /**
     * Constructs a new <code>ComplexTypeMapping</code>.
     * 
     * @param objectType the object type.
     * @param dataTypeName the data type name.
     * @param nodeMappings the node mappings
     * @param objectAccessor the object accessor
     * @param validator the validator - may be null
     */
    public ComplexTypeMapping(Class<?> objectType, N dataTypeName, Collection<M> nodeMappings,
            ObjectAccessor objectAccessor, Validator validator) {
        super(objectType, dataTypeName);
        this.nodeMappings = new ArrayList<M>(nodeMappings);
        this.nodeMappingsByNodeDescriptor = createNodeMappingsByNodeDescriptorMap(nodeMappings);
        this.nodeMappingsByFieldNameAndType = createNodeMappingsByFieldNameAndTypeMap(nodeMappings);
        this.fieldNamesByNodeType = createFieldNamesByNodeTypeMap(nodeMappings);
        this.objectAccessor = objectAccessor;
        this.validator = validator;
    }

    /**
     * @return the node mappings.
     */
    public final Collection<M> getNodeMappings() {
        return Collections.unmodifiableCollection(this.nodeMappings);
    }
    
    /**
     * Returns the node mapping for the given node descriptor.
     * @param <T> the expected type of the node mapping
     * @param nodeDescriptor the node descriptor
     * @return the node mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends M> T getNodeMapping(D nodeDescriptor) {
        return (T) this.nodeMappingsByNodeDescriptor.get(nodeDescriptor);
    }
    
    /**
     * Returns the node mapping for the given field name and required object type of the field.
     * @param <T> the expected type of the node mapping
     * @param fieldName the field name
     * @param objectType the required object type of the field
     * @return the node mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends M> T getNodeMapping(String fieldName, Class<?> objectType) {
        Object value = this.nodeMappingsByFieldNameAndType.get(fieldName);
        if (value instanceof NodeMapping) {
            return (T) value;
        } else if (value instanceof Map) {
            return ReflectionUtil.getNearest(objectType, (Map<Class<?>, T>) value);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the names of all fields which map to a node of the given node type.
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
     * @return the object accessor.
     */
    public final ObjectAccessor getObjectAccessor() {
        return this.objectAccessor;
    }

    /**
     * @return the validator - may be null
     */
    public final Validator getValidator() {
        return this.validator;
    }

    /**
     * Creates a map of node mappings by node descriptors.
     * @param nodeMappings the node mappings
     * @return a map of node mappings by node descriptors
     */
    @SuppressWarnings("unchecked")
    protected Map<D, M> createNodeMappingsByNodeDescriptorMap(Collection<M> nodeMappings) {
        Map<D, M> result = new HashMap<D, M>();
        for (NodeMapping nodeMapping : nodeMappings) {
            if (result.put((D) nodeMapping.getNodeDescriptor(), (M) nodeMapping) != null) {
                throw new IOFactoryException("The node descriptor is ambiguous: "
                        + nodeMapping.getNodeDescriptor());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, ?> createNodeMappingsByFieldNameAndTypeMap(Collection<M> nodeMappings) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (NodeMapping nodeMapping : nodeMappings) {
            FieldDescriptor fieldDescriptor = nodeMapping.getFieldDescriptor();
            Map<Class<?>, M> map = (Map<Class<?>, M>) result.get(fieldDescriptor.getName());
            if (map == null) {
                map = new HashMap<Class<?>, M>();
                result.put(fieldDescriptor.getName(), map);
            }
            map.put(fieldDescriptor.getObjectType(), (M) nodeMapping);
        }
        for (String fieldName : result.keySet()) {
            Map<Class<?>, M> map = (Map<Class<?>, M>) result.get(fieldName);
            if (map.size() == 1) {
                result.put(fieldName, map.values().iterator().next());
            }
        }
        return result;
    }

    private Map<NodeType, List<String>> createFieldNamesByNodeTypeMap(Collection<M> nodeMappings) {
        Map<NodeType, List<String>> result = new HashMap<NodeType, List<String>>();
        for (M nodeMapping : nodeMappings) {
            NodeType nodeType = nodeMapping.getNodeDescriptor().getType();
            List<String> fieldNames = result.get(nodeType);
            if (fieldNames == null) {
                fieldNames = new ArrayList<String>();
                result.put(nodeType, fieldNames);
            }
            FieldDescriptor fieldDescriptor = nodeMapping.getFieldDescriptor();
            if (!fieldNames.contains(fieldDescriptor.getName())) {
                fieldNames.add(fieldDescriptor.getName());
            }
        }
        return result;
    }

}
