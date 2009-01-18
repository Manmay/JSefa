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
import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.util.ReflectionUtil;

/**
 * A mapping between a java map type and a map data type of the exchange format.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 * @param <N> the type of the data type name
 * @param <D> the type of the node descriptor
 * @param <K> the type of the key node mapping
 * @param <V> the type of the value node mapping
 */

public abstract class MapTypeMapping<N, K extends NodeMapping<N, ?>, D extends NodeDescriptor<?>,
    V extends NodeMapping<N, D>> extends TypeMapping<N> {

    private final K keyNodeMapping;

    private final Collection<V> valueNodeMappings;
    
    private final Map<D, V> valueNodeMappingsByNodeDescriptor;
    
    private final Map<Class<?>, V> valueNodeMappingsByObjectType;

    private final ObjectAccessor objectAccessor;

    /**
     * Constructs a new <code>MapTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param keyNodeMapping the node mapping for the keys of the map
     * @param valueNodeMappings the node mappings for the values of the map
     * @param objectAccessor the object accessor
     */
    public MapTypeMapping(Class<?> objectType, N dataTypeName, K keyNodeMapping, Collection<V> valueNodeMappings,
            ObjectAccessor objectAccessor) {
        super(objectType, dataTypeName);
        this.keyNodeMapping = keyNodeMapping;
        this.valueNodeMappings = new ArrayList<V>(valueNodeMappings);
        this.valueNodeMappingsByNodeDescriptor = createValueNodeMappingsByNodeDescriptorMap(valueNodeMappings);
        this.valueNodeMappingsByObjectType = createValueNodeMappingsByObjectTypeMap(valueNodeMappings);
        this.objectAccessor = objectAccessor;
    }
    
    /**
     * @return the node mapping for the keys of the map.
     */
    public final K getKeyNodeMapping() {
        return this.keyNodeMapping;
    }

    /**
     * @return the node mappings for the values of the map.
     */
    public final Collection<V> getValueNodeMappings() {
        return Collections.unmodifiableCollection(this.valueNodeMappings);
    }
    
    /**
     * Returns the value node mapping for the given node descriptor.
     * @param <T> the expected type of the value node mapping
     * @param nodeDescriptor the node descriptor
     * @return the value node mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends V> T getValueNodeMapping(D nodeDescriptor) {
        return (T) this.valueNodeMappingsByNodeDescriptor.get(nodeDescriptor);
    }
    
    /**
     * Returns the value node mapping for a map value object with the given object type. If none is registered for the
     * object type the one for its super type is returned (and so on).
     * 
     * @param <T> the expected type of the node mapping
     * @param objectType the object type.
     * @return the element mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends V> T getValueNodeMapping(Class<?> objectType) {
        return (T) ReflectionUtil.getNearest(objectType, this.valueNodeMappingsByObjectType);
    }
    

    /**
     * @return the object accessor.
     */
    public final ObjectAccessor getObjectAccessor() {
        return this.objectAccessor;
    }
    
    /**
     * Creates a map of value node mappings by node descriptors.
     * @param nodeMappings the value node mappings for the map values
     * @return a map of value node mappings by node descriptors
     */
    @SuppressWarnings("unchecked")
    protected Map<D, V> createValueNodeMappingsByNodeDescriptorMap(Collection<V> nodeMappings) {
        Map<D, V> result = new HashMap<D, V>();
        for (NodeMapping nodeMapping : nodeMappings) {
            if (result.put((D) nodeMapping.getNodeDescriptor(), (V) nodeMapping) != null) {
                throw new IOFactoryException("The node descriptor is ambiguous: "
                        + nodeMapping.getNodeDescriptor());
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Map<Class<?>, V> createValueNodeMappingsByObjectTypeMap(Collection<V> nodeMappings) {
        Map<Class<?>, V> result = new HashMap<Class<?>, V>();
        for (NodeMapping nodeMapping : nodeMappings) {
            result.put(nodeMapping.getObjectType(), (V) nodeMapping);
        }
        return result;
    }

}
