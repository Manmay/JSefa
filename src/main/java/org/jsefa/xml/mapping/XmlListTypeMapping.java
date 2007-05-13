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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.ConfigurationException;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.QName;

/**
 * A mapping between a java object type and a XML list data type.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlListTypeMapping extends TypeMapping<QName> {

    private final boolean implicit;

    private final Map<NodeDescriptor, NodeModel> nodeModelsByNodeDescriptor;

    private final Map<Class, NodeModel> nodeModelsByObjectType;

    /**
     * Constructs a new <code>XmlListTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param implicit true, if there is no embracing element around the list
     *            items; false otherwise
     */
    public XmlListTypeMapping(QName dataTypeName, boolean implicit) {
        super(List.class, dataTypeName);
        this.implicit = implicit;
        this.nodeModelsByNodeDescriptor = new HashMap<NodeDescriptor, NodeModel>();
        this.nodeModelsByObjectType = new HashMap<Class, NodeModel>();
    }

    /**
     * Specifies which list item object type maps to which element.
     * 
     * @param elementDescriptor the element descriptor
     * @param objectType the object type
     */
    public void register(ElementDescriptor elementDescriptor, Class objectType) {
        assertNotFinished();
        if (this.nodeModelsByNodeDescriptor.containsKey(elementDescriptor)) {
            throw new ConfigurationException("The element " + elementDescriptor.getName()
                    + " is already registered for the list with data type name " + this.getDataTypeName());
        }
        if (this.nodeModelsByObjectType.containsKey(objectType)) {
            throw new ConfigurationException("The object type " + objectType.getName()
                    + " is already registered for the list with data type name " + this.getDataTypeName());
        }
        NodeModel nodeModel = new NodeModel(elementDescriptor, null);
        this.nodeModelsByNodeDescriptor.put(elementDescriptor, nodeModel);
        this.nodeModelsByObjectType.put(objectType, nodeModel);
    }

    /**
     * Finishes the construction of the type mapping. This method must be called
     * after the last call to <code>register</code>.
     */
    public void finish() {
        XmlTypeMappingUtil.finishNodeModelsByNodeDescriptor(this.nodeModelsByNodeDescriptor);
        super.finish();
    }

    /**
     * Returns true, if there is no embracing element around the list items;
     * false otherwise.
     * 
     * @return true, if there is no embracing element around the list items;
     *         false otherwise
     */
    public boolean isImplicit() {
        return this.implicit;
    }

    /**
     * Returns the node model for a list item object with the given object type.
     * If none is registered for the object type the one for its super type is
     * returned (and so on).
     * 
     * @param objectType the object type.
     * @return the node model.
     */
    public NodeModel getNodeModel(Class objectType) {
        Class currentObjectType = objectType;
        while (currentObjectType != null) {
            NodeModel result = this.nodeModelsByObjectType.get(currentObjectType);
            if (result != null) {
                return result;
            }
            currentObjectType = currentObjectType.getSuperclass();
        }
        return null;
    }

    /**
     * Returns the node model for the given element descriptor.
     * 
     * @param elementDescriptor the element descriptor
     * @return the node model
     */
    public NodeModel getNodeModel(ElementDescriptor elementDescriptor) {
        return this.nodeModelsByNodeDescriptor.get(elementDescriptor);
    }

}
