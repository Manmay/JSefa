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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsefa.common.mapping.TypeMapping;

/**
 * A mapping between a java object type and a RBF list data type.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */

public final class RbfListTypeMapping extends TypeMapping<String> {

    private final Map<String, NodeModel> nodeModelsByPrefix;

    private final Map<Class<?>, NodeModel> nodeModelsByObjectType;

    /**
     * Constructs a new <code>RbfListTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     */
    public RbfListTypeMapping(String dataTypeName) {
        super(List.class, dataTypeName);
        this.nodeModelsByPrefix = new HashMap<String, NodeModel>();
        this.nodeModelsByObjectType = new HashMap<Class<?>, NodeModel>();
    }

    /**
     * Registeres the list item with the given data type name, prefix and object
     * type.
     * 
     * @param dataTypeName the data type name
     * @param prefix the prefix
     * @param objectType the object type
     */
    public void register(String dataTypeName, String prefix, Class<?> objectType) {
        assertNotFinished();
        NodeModel nodeModel = new NodeModel(dataTypeName, null, prefix);
        this.nodeModelsByPrefix.put(prefix, nodeModel);
        this.nodeModelsByObjectType.put(objectType, nodeModel);
    }

    /**
     * Returns the node model for the given object type.
     * 
     * @param objectType the object type
     * @return a node model
     */
    public NodeModel getNodeModel(Class<?> objectType) {
        return this.nodeModelsByObjectType.get(objectType);
    }

    /**
     * Returns the node model for the given prefix.
     * 
     * @param prefix the prefix
     * @return a node model
     */
    public NodeModel getNodeModel(String prefix) {
        return this.nodeModelsByPrefix.get(prefix);
    }

    /**
     * Returns the set of prefixes.
     * 
     * @return the set of prefixes.
     */
    public Set<String> getPrefixes() {
        return this.nodeModelsByPrefix.keySet();
    }

}
