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

package org.jsefa.xml;

import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.SerializationException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;
import org.jsefa.xml.mapping.NodeModel;
import org.jsefa.xml.mapping.NodeType;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
import org.jsefa.xml.mapping.XmlSimpleTypeMapping;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;

/**
 * Default implementation of {@link XmlSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlSerializerImpl implements XmlSerializer {
    private final XmlTypeMappingRegistry typeMappingRegistry;

    private final Map<Class, NodeModel> entryNodeModels;

    private final XmlLowLevelSerializer lowLevelSerializer;
    
    private IdentityHashMap<Object, Object> complexObjectsOnPath;

    XmlSerializerImpl(XmlConfiguration config, XmlTypeMappingRegistry typeMappingRegistry,
            Map<Class, NodeModel> entryNodeModels) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.entryNodeModels = entryNodeModels;
        this.lowLevelSerializer = config.getLowLevelIOFactory().createSerializer(config.getLowLevelConfiguration());
        this.complexObjectsOnPath = new IdentityHashMap<Object, Object>();
    }

    /**
     * {@inheritDoc}
     */
    public void open(Writer writer) {
        this.lowLevelSerializer.open(writer);
        this.complexObjectsOnPath.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void write(Object object) {
        if (object == null) {
            return;
        }
        NodeModel nodeModel = this.entryNodeModels.get(object.getClass());
        if (nodeModel == null) {
            Class objectType = object.getClass().getSuperclass();
            while (objectType != null) {
                nodeModel = this.entryNodeModels.get(objectType);
                if (nodeModel != null) {
                    break;
                }
                objectType = objectType.getSuperclass();
            }
        }
        if (nodeModel == null) {
            throw new SerializationException("The following class was not registered for serialization: "
                    + object.getClass());
        }
        try {
            serializeElement(object, nodeModel);
        } catch (Exception e) {
            throw new SerializationException("Error during serialization", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void close(boolean closeWriter) {
        this.lowLevelSerializer.close(closeWriter);
    }

    /**
     * {@inheritDoc}
     */
    public XmlLowLevelSerializer getLowLevelSerializer() {
        return this.lowLevelSerializer;
    }

    private void serializeAttribute(Object object, NodeModel nodeModel) {
        if (object == null) {
            return;
        }
        String attributeValue = getSimpleTypeMapping(nodeModel).getSimpleTypeConverter().toString(object);
        this.lowLevelSerializer.writeAttribute(nodeModel.getNodeDescriptor().getName(), attributeValue);
    }

    private void serializeElement(Object object, NodeModel nodeModel) {
        TypeMapping typeMapping = this.typeMappingRegistry.get(nodeModel.getDataTypeName());
        if (typeMapping == null) {
            throw new SerializationException("No type mapping given for data type name "
                    + nodeModel.getDataTypeName());
        }
        if (typeMapping instanceof XmlSimpleTypeMapping) {
            serializeSimpleElement(object, nodeModel, (XmlSimpleTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlComplexTypeMapping) {
            serializeComplexElement(object, nodeModel, (XmlComplexTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlListTypeMapping) {
            serializeListElement((List) object, nodeModel, (XmlListTypeMapping) typeMapping);
        }
    }

    private void serializeSimpleElement(Object object, NodeModel nodeModel, XmlSimpleTypeMapping simpleTypeMapping) {
        if (object == null) {
            return;
        }
        String elementValue = simpleTypeMapping.getSimpleTypeConverter().toString(object);
        writeStartElement(nodeModel);
        this.lowLevelSerializer.writeText(elementValue);
        this.lowLevelSerializer.finishElement();
    }

    private void serializeComplexElement(Object object, NodeModel nodeModel, XmlComplexTypeMapping typeMapping) {
        if (object == null) {
            return;
        }
        if (this.complexObjectsOnPath.containsKey(object)) {
            throw new SerializationException("Cycle detected while serializing " + object);
        } else {
            this.complexObjectsOnPath.put(object, object);
        }
        ObjectAccessor objectAccessor = typeMapping.getObjectAccessor();
        writeStartElement(nodeModel);
        for (String fieldName : typeMapping.getFieldNames(NodeType.ATTRIBUTE)) {
            Object fieldValue = objectAccessor.getValue(object, fieldName);
            if (fieldValue != null) {
                NodeModel attributeModel = typeMapping.getNodeModel(fieldName, fieldValue.getClass());
                serializeAttribute(fieldValue, attributeModel);
            }
        }

        for (String fieldName : typeMapping.getFieldNames(NodeType.TEXT_CONTENT)) {
            Object fieldValue = objectAccessor.getValue(object, fieldName);
            if (fieldValue != null) {
                NodeModel textModel = typeMapping.getNodeModel(fieldName, fieldValue.getClass());
                String value = getSimpleTypeMapping(textModel).getSimpleTypeConverter().toString(fieldValue);
                this.lowLevelSerializer.writeText(value);
            }

        }
        for (String fieldName : typeMapping.getFieldNames(NodeType.ELEMENT)) {
            Object fieldValue = objectAccessor.getValue(object, fieldName);
            if (fieldValue != null) {
                Class fieldClass = fieldValue.getClass();
                if (List.class.isAssignableFrom(fieldClass)) {
                    fieldClass = List.class;
                }
                NodeModel childNodeModel = typeMapping.getNodeModel(fieldName, fieldClass);
                if (childNodeModel == null) {
                    throw new SerializationException("Unable to serialize field class " + fieldClass.getName()
                            + " for field " + fieldName + " within object type " + typeMapping.getObjectType());
                }
                serializeElement(fieldValue, childNodeModel);
            }
        }
        this.lowLevelSerializer.finishElement();
        this.complexObjectsOnPath.remove(object);
    }

    private void serializeListElement(List listObject, NodeModel nodeModel, XmlListTypeMapping typeMapping) {
        if (listObject == null) {
            return;
        }
        if (!typeMapping.isImplicit()) {
            writeStartElement(nodeModel);
        }
        for (Object listItemValue : listObject) {
            NodeModel listItemNodeModel = typeMapping.getNodeModel(listItemValue.getClass());
            if (listItemNodeModel == null) {
                throw new SerializationException("No node model found for list item with class "
                        + listItemValue.getClass());
            }
            serializeElement(listItemValue, listItemNodeModel);
        }
        if (!typeMapping.isImplicit()) {
            this.lowLevelSerializer.finishElement();
        }

    }

    private XmlSimpleTypeMapping getSimpleTypeMapping(NodeModel nodeModel) {
        return (XmlSimpleTypeMapping) this.typeMappingRegistry.get(nodeModel.getDataTypeName());
    }

    private void writeStartElement(NodeModel nodeModel) {
        if (nodeModel.requiresDataTypeAttribute()) {
            this.lowLevelSerializer.startElement(nodeModel.getNodeDescriptor().getName(), nodeModel
                    .getDataTypeName());
        } else {
            this.lowLevelSerializer.startElement(nodeModel.getNodeDescriptor().getName(), null);
        }
    }

}
