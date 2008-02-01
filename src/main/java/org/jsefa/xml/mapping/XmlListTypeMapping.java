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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.namespace.QName;

/**
 * A mapping between a java object type and a XML list data type.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlListTypeMapping extends TypeMapping<QName> {

    private final boolean implicit;

    private final Map<ElementDescriptor, ElementMapping> elementMappingsByElementDescriptor;

    private final Map<Class<?>, ElementMapping> elementMappingsByObjectType;

    /**
     * Constructs a new <code>XmlListTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param implicit true, if there is no embracing element around the list items; false otherwise
     * @param elementMappings the element mappings
     */
    public XmlListTypeMapping(QName dataTypeName, boolean implicit, Collection<ElementMapping> elementMappings) {
        super(List.class, dataTypeName);
        this.implicit = implicit;
        this.elementMappingsByElementDescriptor = XmlTypeMappingUtil
                .createNodeMappingsByNodeDescriptorMap(elementMappings);
        this.elementMappingsByObjectType = createElementMappingsByObjectTypeMap(elementMappings);
    }

    /**
     * Returns true, if an element descriptor is already bound to the given object type.
     * 
     * @param objectType the object type
     * @return true, if an element descriptor is already bound to the given object type; false otherwise
     */
    public boolean hasRegistrationFor(Class<?> objectType) {
        return this.elementMappingsByObjectType.containsKey(objectType);
    }

    /**
     * Returns true, if there is no embracing element around the list items; false otherwise.
     * 
     * @return true, if there is no embracing element around the list items; false otherwise
     */
    public boolean isImplicit() {
        return this.implicit;
    }

    /**
     * Returns the element mapping for a list item object with the given object type. If none is registered for the
     * object type the one for its super type is returned (and so on).
     * 
     * @param objectType the object type.
     * @return the element mapping
     */
    public ElementMapping getElementMapping(Class<?> objectType) {
        Class<?> currentObjectType = objectType;
        while (currentObjectType != null) {
            ElementMapping result = this.elementMappingsByObjectType.get(currentObjectType);
            if (result != null) {
                return result;
            }
            currentObjectType = currentObjectType.getSuperclass();
        }
        return null;
    }

    /**
     * Returns the element mapping for the given element descriptor.
     * 
     * @param elementDescriptor the element descriptor
     * @return the element mapping
     */
    public ElementMapping getElementMapping(ElementDescriptor elementDescriptor) {
        return this.elementMappingsByElementDescriptor.get(elementDescriptor);
    }

    private Map<Class<?>, ElementMapping> createElementMappingsByObjectTypeMap(
            Collection<ElementMapping> elementMappings) {
        Map<Class<?>, ElementMapping> result = new HashMap<Class<?>, ElementMapping>();
        for (ElementMapping elementMapping : elementMappings) {
            result.put(elementMapping.getObjectType(), elementMapping);
        }
        return result;
    }

}
