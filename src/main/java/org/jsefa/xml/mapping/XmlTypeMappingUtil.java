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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class providing methods used in different type mapping classes.
 * <p>
 * This class is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
final class XmlTypeMappingUtil {

    static void finishNodeModelsByNodeDescriptor(Map<NodeDescriptor, NodeModel> nodeModelsByNodeDescriptor) {
        Set<NodeDescriptor> nodeDescriptorSet = new HashSet<NodeDescriptor>(nodeModelsByNodeDescriptor.keySet());
        Set<NodeDescriptor> ambiguousNodeDescriptors = new HashSet<NodeDescriptor>();
        for (NodeDescriptor nodeDescriptor : nodeDescriptorSet) {
            NodeDescriptor simplifiedNodeDescriptor = createSimplifiedNodeDescriptor(nodeDescriptor);
            if (!ambiguousNodeDescriptors.contains(simplifiedNodeDescriptor)) {
                if (nodeModelsByNodeDescriptor.get(simplifiedNodeDescriptor) != null) {
                    nodeModelsByNodeDescriptor.remove(simplifiedNodeDescriptor);
                    ambiguousNodeDescriptors.add(simplifiedNodeDescriptor);
                } else {
                    nodeModelsByNodeDescriptor.put(simplifiedNodeDescriptor, nodeModelsByNodeDescriptor
                            .get(nodeDescriptor));
                }
            }
        }
        for (NodeModel nodeModel : nodeModelsByNodeDescriptor.values()) {
            if (!nodeModel.isFinished()) {
                NodeDescriptor simplifiedNodeDescriptor = createSimplifiedNodeDescriptor(nodeModel
                        .getNodeDescriptor());
                if (nodeModelsByNodeDescriptor.get(simplifiedNodeDescriptor) == null) {
                    nodeModel.setRequiresDataTypeAttribute();
                }
                nodeModel.finish();
            }
        }
    }

    private static NodeDescriptor createSimplifiedNodeDescriptor(NodeDescriptor nodeDescriptor) {
        if (nodeDescriptor instanceof ElementDescriptor) {
            return new ElementDescriptor(nodeDescriptor.getName(), null);
        } else if (nodeDescriptor instanceof AttributeDescriptor) {
            return new AttributeDescriptor(nodeDescriptor.getName(), null);
        } else if (nodeDescriptor instanceof TextContentDescriptor) {
            return new TextContentDescriptor(null);
        } else {
            throw new UnsupportedOperationException("Unsupported node type: " + nodeDescriptor.getType());
        }
    }

    private XmlTypeMappingUtil() {

    }

}
