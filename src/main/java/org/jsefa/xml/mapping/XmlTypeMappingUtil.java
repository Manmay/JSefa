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
import java.util.Map;

import org.jsefa.IOFactoryException;

/**
 * Utility class providing methods used in different type mapping contexts.
 * <p>
 * This class is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlTypeMappingUtil {

    /**
     * Creates a map of node mappings with node descriptors as keys. For each element mapping contained in the
     * given collection of node mappings an additional simplified descriptor is created if an element name exists
     * and is not ambiguous so that the element mapping has two keys.
     * 
     * @param <D> the expected type of the node descriptor
     * @param <M> the expected type of the node mappings
     * @param nodeMappings the node mappings
     * @return a map of node mappings with node descriptors as keys
     */
    @SuppressWarnings("unchecked")
    public static <D extends XmlNodeDescriptor, M extends XmlNodeMapping<?>> Map<D, M>
        createNodeMappingsByNodeDescriptorMap(Collection<M> nodeMappings) {

        Map<D, M> result = new HashMap<D, M>();
        for (M nodeMapping : nodeMappings) {
            if (result.put((D) nodeMapping.getNodeDescriptor(), nodeMapping) != null) {
                throw new IOFactoryException("The node descriptor is ambiguous: "
                        + nodeMapping.getNodeDescriptor());
            }
            if (nodeMapping instanceof ElementMapping) {
                ElementMapping elementMapping = (ElementMapping) nodeMapping;
                if (elementMapping.getNodeDescriptor().getName() != null
                        && !elementMapping.elementNameIsAmbiguous()) {
                    ElementDescriptor simplifiedElementDescriptor = new ElementDescriptor(elementMapping
                            .getNodeDescriptor().getName(), null);
                    if (result.put((D) simplifiedElementDescriptor, nodeMapping) != null) {
                        throw new IOFactoryException("The simplified node descriptor is ambiguous: "
                                + simplifiedElementDescriptor);
                    }
                }
            }
        }
        return result;
    }

    private XmlTypeMappingUtil() {

    }

}
