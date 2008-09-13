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
 * A mapping between a java object type and a list data type of the exchange format.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 * @param <N> the type of the data type name
 * @param <D> the type of the node descriptor
 * @param <M> the type of the node mapping
 */

public abstract class ListTypeMapping<N, D extends NodeDescriptor<?>,
    M extends NodeMapping<N, D>> extends TypeMapping<N> {

    private final Collection<M> nodeMappings;

    private final Map<D, M> nodeMappingsByNodeDescriptor;
    
    private final Map<Class<?>, M> nodeMappingsByObjectType;

    private final ObjectAccessor objectAccessor;

    /**
     * Constructs a new <code>ListTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param nodeMappings the node mappings
     * @param objectAccessor the object accessor
     */
    public ListTypeMapping(Class<?> objectType, N dataTypeName, Collection<M> nodeMappings,
            ObjectAccessor objectAccessor) {
        super(objectType, dataTypeName);
        this.nodeMappings = new ArrayList<M>(nodeMappings);
        this.nodeMappingsByNodeDescriptor = createNodeMappingsByNodeDescriptorMap(nodeMappings);
        this.nodeMappingsByObjectType = createNodeMappingsByObjectTypeMap(nodeMappings);
        this.objectAccessor = objectAccessor;
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
     * Returns the node mapping for a list item object with the given object type. If none is registered for the
     * object type the one for its super type is returned (and so on).
     * 
     * @param <T> the expected type of the node mapping
     * @param objectType the object type.
     * @return the element mapping
     */
    @SuppressWarnings("unchecked")
    public <T extends M> T getNodeMapping(Class<?> objectType) {
        return (T) ReflectionUtil.getNearest(objectType, this.nodeMappingsByObjectType);
    }
    

    /**
     * @return the object accessor.
     */
    public final ObjectAccessor getObjectAccessor() {
        return this.objectAccessor;
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
    private Map<Class<?>, M> createNodeMappingsByObjectTypeMap(Collection<M> nodeMappings) {
        Map<Class<?>, M> result = new HashMap<Class<?>, M>();
        for (NodeMapping nodeMapping : nodeMappings) {
            result.put(nodeMapping.getObjectType(), (M) nodeMapping);
        }
        return result;
    }

}
