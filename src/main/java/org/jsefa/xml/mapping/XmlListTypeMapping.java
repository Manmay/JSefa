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
import org.jsefa.common.mapping.ListTypeMapping;
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
public final class XmlListTypeMapping extends ListTypeMapping<QName, ElementDescriptor, ElementMapping> {

    private final boolean implicit;

    /**
     * Constructs a new <code>XmlListTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param implicit true, if there is no embracing element around the list items; false otherwise
     * @param elementMappings the element mappings
     * @param objectAccessor the object accessor
     */
    public XmlListTypeMapping(QName dataTypeName, boolean implicit, Collection<ElementMapping> elementMappings,
            ObjectAccessor objectAccessor) {
        super(Collection.class, dataTypeName, elementMappings, objectAccessor);
        this.implicit = implicit;
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
     * {@inheritDoc}
     */

    @Override
    protected Map<ElementDescriptor, ElementMapping> createNodeMappingsByNodeDescriptorMap(
            Collection<ElementMapping> nodeMappings) {
        return XmlTypeMappingUtil.createNodeMappingsByNodeDescriptorMap(nodeMappings);
    }

}
