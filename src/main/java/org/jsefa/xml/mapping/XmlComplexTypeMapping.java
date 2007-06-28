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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.namespace.QName;

/**
 * A mapping between a java object type and a XML complex data type.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlComplexTypeMapping extends TypeMapping<QName> {
    private final EnumMap<NodeType, List<String>> fieldNamesByNodeType;

    private final Map<NodeDescriptor, NodeModel> nodeModelsByNodeDescriptor;

    private final Map<TypedField, NodeModel> nodeModelsByTypedField;

    private final Map<String, NodeModel> nodeModelsByFieldName;

    private final ObjectAccessor objectAccessor;

    private boolean textContentAllowed = false;

    /**
     * Constructs a new <code>XmlComplexTypeMapping</code>.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param objectAccessor the object accessor
     */
    public XmlComplexTypeMapping(Class objectType, QName dataTypeName, ObjectAccessor objectAccessor) {
        super(objectType, dataTypeName);
        this.objectAccessor = objectAccessor;
        this.fieldNamesByNodeType = new EnumMap<NodeType, List<String>>(NodeType.class);
        this.nodeModelsByNodeDescriptor = new HashMap<NodeDescriptor, NodeModel>();
        this.nodeModelsByTypedField = new HashMap<TypedField, NodeModel>();
        this.nodeModelsByFieldName = new HashMap<String, NodeModel>();
    }

    /**
     * Declares that a node descriped by the given node descriptor maps to the
     * combination of the given field name and the given field type. The
     * combination of the field name and field type must uniquely map to the
     * node descriptor.
     * 
     * @param fieldName the field name
     * @param fieldType the field type
     * @param nodeDescriptor the node descriptor
     */
    public void register(String fieldName, Class fieldType, NodeDescriptor nodeDescriptor) {
        register(fieldName, fieldType, nodeDescriptor, nodeDescriptor.getDataTypeName(), false);
    }

    /**
     * Declares that a node described by the given node descriptor maps to the
     * combination of the given field name, the given field type and the given
     * data type name. The given data type name is different from the data type
     * name of the node and identifies a type mapping from which the type
     * mapping for the node can be determined. Thus the given data type name
     * determines the type mapping of the node only <b>indirectly</b>.
     * 
     * @param fieldName the field name
     * @param fieldType the field type
     * @param nodeDescriptor the node descriptor
     * @param dataTypeName the data type name
     */
    public void registerIndirect(String fieldName, Class fieldType, NodeDescriptor nodeDescriptor,
            QName dataTypeName) {
        register(fieldName, fieldType, nodeDescriptor, dataTypeName, true);
    }

    /**
     * Finishes the construction of the type mapping. This method must be called
     * after the last call to <code>register</code> or
     * <code>registerIndirect</code>.
     */
    public void finish() {
        registerNodeModelsByFieldName();
        XmlTypeMappingUtil.finishNodeModelsByNodeDescriptor(this.nodeModelsByNodeDescriptor);
        if (this.fieldNamesByNodeType.get(NodeType.TEXT_CONTENT) != null) {
            this.textContentAllowed = true;
        }
        super.finish();
    }

    /**
     * Returns true if and only if a text content is allowed for this data type.
     * 
     * @return true, if a text content is allowed for this data type; false
     *         otherwise.
     */
    public boolean isTextContentAllowed() {
        return this.textContentAllowed;
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
     * Returns the node model for the given combination of a field name and a
     * field type (the class of the field value).
     * 
     * @param fieldName the field name
     * @param fieldType the field typee
     * @return the node model.
     */
    public NodeModel getNodeModel(String fieldName, Class fieldType) {
        NodeModel result = this.nodeModelsByFieldName.get(fieldName);
        if (result == null) {
            Class type = fieldType;
            while (type != null) {
                result = this.nodeModelsByTypedField.get(new TypedField(fieldName, type));
                if (result != null) {
                    break;
                }
                type = type.getSuperclass();
            }
        }
        return result;
    }

    /**
     * Returns the node model for the given node descriptor.
     * 
     * @param nodeDescriptor the descriptor of the node
     * @return the node model
     */
    public NodeModel getNodeModel(NodeDescriptor nodeDescriptor) {
        return this.nodeModelsByNodeDescriptor.get(nodeDescriptor);
    }

    /**
     * Returns the object accessor.
     * 
     * @return the object accessor.
     */
    public ObjectAccessor getObjectAccessor() {
        return this.objectAccessor;
    }

    private void register(String fieldName, Class fieldType, NodeDescriptor nodeDescriptor, QName dataTypeName,
            boolean indirect) {
        assertNotFinished();
        List<String> fieldNames = getOrCreateFieldNameList(nodeDescriptor.getType());
        if (!fieldNames.contains(fieldName)) {
            fieldNames.add(fieldName);
        }

        TypedField field = new TypedField(fieldName, fieldType);
        NodeModel nodeModel;
        if (indirect) {
            nodeModel = new NodeModel(nodeDescriptor, dataTypeName, fieldName);
        } else {
            nodeModel = new NodeModel(nodeDescriptor, fieldName);
        }
        this.nodeModelsByTypedField.put(field, nodeModel);
        this.nodeModelsByNodeDescriptor.put(nodeDescriptor, nodeModel);
    }

    private List<String> getOrCreateFieldNameList(NodeType nodeType) {
        List<String> result = this.fieldNamesByNodeType.get(nodeType);
        if (result == null) {
            result = new ArrayList<String>();
            this.fieldNamesByNodeType.put(nodeType, result);
        }
        return result;
    }

    private void registerNodeModelsByFieldName() {
        Set<String> ambiguousFieldNames = new HashSet<String>();
        for (TypedField typedField : this.nodeModelsByTypedField.keySet()) {
            if (!ambiguousFieldNames.contains(typedField.getName())) {
                if (this.nodeModelsByFieldName.get(typedField.getName()) == null) {
                    this.nodeModelsByFieldName.put(typedField.getName(), this.nodeModelsByTypedField
                            .get(typedField));
                } else {
                    ambiguousFieldNames.add(typedField.getName());
                    this.nodeModelsByFieldName.remove(typedField.getName());
                }
            }
        }
    }

    /**
     * Encapsulates the combination of a field name and a potential type of the
     * field value.
     * <p>
     * Instances of this class are immutable and thread-safe.
     * 
     * @author Norman Lahme-Huetig
     * 
     */
    private static final class TypedField {
        private final String name;

        private final Class objectType;

        private final int hashCode;

        public TypedField(String name, Class objectType) {
            this.name = name;
            this.objectType = objectType;
            this.hashCode = 37 * (17 + getName().hashCode()) + getObjectType().hashCode();
        }

        public String getName() {
            return this.name;
        }

        public Class getObjectType() {
            return this.objectType;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TypedField)) {
                return false;
            }
            TypedField other = (TypedField) obj;
            return getName().equals(other.getName()) && getObjectType().equals(other.getObjectType());
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public String toString() {
            return getName() + "@" + getObjectType().getName();
        }

    }

}
