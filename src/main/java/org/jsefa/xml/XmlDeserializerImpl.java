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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsefa.DeserializationException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.lowlevel.Attribute;
import org.jsefa.xml.lowlevel.ElementEnd;
import org.jsefa.xml.lowlevel.ElementStart;
import org.jsefa.xml.lowlevel.XmlItem;
import org.jsefa.xml.lowlevel.XmlItemType;
import org.jsefa.xml.lowlevel.XmlLowLevelDeserializer;
import org.jsefa.xml.mapping.AttributeDescriptor;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.NodeModel;
import org.jsefa.xml.mapping.TextContentDescriptor;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
import org.jsefa.xml.mapping.XmlSimpleTypeMapping;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;

/**
 * Default implementation of {@link XmlDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlDeserializerImpl implements XmlDeserializer {
    private final XmlTypeMappingRegistry typeMappingRegistry;

    private final Collection<XmlEntryPoint> entryPoints;

    private final XmlLowLevelDeserializer lowLevelDeserializer;

    private XmlEntryPoint currentEntryPoint;

    XmlDeserializerImpl(XmlConfiguration config, XmlTypeMappingRegistry typeMappingRegistry,
            Collection<XmlEntryPoint> entryPoints) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.entryPoints = entryPoints;
        this.lowLevelDeserializer = config.getLowLevelDriver().createDeserializer(config.getLowLevelConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    public void open(Reader reader) {
        this.lowLevelDeserializer.open(reader);
        this.currentEntryPoint = null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        try {
            if (!this.lowLevelDeserializer.hasNext()) {
                return false;
            }
            if (this.currentEntryPoint == null) {
                return moveToNextEntryPoint();
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new DeserializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object next() {
        if (!hasNext()) {
            return null;
        }
        try {
            return deserializeElement(this.currentEntryPoint.getDataTypeName());
        } catch (Exception e) {
            throw new DeserializationException("Error while deserialization", e);
        } finally {
            this.currentEntryPoint = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close(boolean closeReader) {
        this.lowLevelDeserializer.close(closeReader);
    }

    private Object deserializeElement(QName dataTypeName) {
        TypeMapping<QName> typeMapping = this.typeMappingRegistry.get(dataTypeName);
        if (typeMapping instanceof XmlSimpleTypeMapping) {
            return deserializeSimpleElement((XmlSimpleTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlComplexTypeMapping) {
            return deserializeComplexElement((XmlComplexTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlListTypeMapping) {
            return deserializeListElement((XmlListTypeMapping) typeMapping);
        } else {
            throw new IllegalArgumentException(
                    "Argument dataTypeName maps to a type mapping which is neither simple nor complex: "
                            + typeMapping.getClass());
        }
    }

    private Object deserializeSimpleElement(XmlSimpleTypeMapping simpleTypeMapping) {
        return simpleTypeMapping.getSimpleTypeConverter().fromString(getText());
    }

    @SuppressWarnings("unchecked")
    private Object deserializeComplexElement(XmlComplexTypeMapping typeMapping) {
        ObjectAccessor objectAccessor = typeMapping.getObjectAccessor();
        Object object = objectAccessor.createObject();
        ElementStart elementStart = (ElementStart) getCurrentXmlItem();
        for (Attribute attribute : elementStart.getAttributes()) {
            AttributeDescriptor attributeDescriptor = new AttributeDescriptor(attribute.getName(), null);
            NodeModel nodeModel = typeMapping.getNodeModel(attributeDescriptor);
            if (nodeModel != null) {
                XmlSimpleTypeMapping attributeTypeMapping = getSimpleTypeMapping(nodeModel.getDataTypeName());
                Object value = attributeTypeMapping.getSimpleTypeConverter().fromString(attribute.getValue());
                if (value != null) {
                    objectAccessor.setValue(object, nodeModel.getFieldName(), value);
                }
            }
        }
        if (typeMapping.isTextContentAllowed()) {
            NodeModel textContentModel = typeMapping.getNodeModel(new TextContentDescriptor(null));
            TypeMapping<QName> textContentTypeMapping = getSimpleTypeMapping(textContentModel.getDataTypeName());
            String fieldName = textContentModel.getFieldName();
            Object value = deserializeSimpleElement((XmlSimpleTypeMapping) textContentTypeMapping);
            if (value != null) {
                objectAccessor.setValue(object, fieldName, value);
            }
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                NodeModel childNodeModel = typeMapping.getNodeModel(getCurrentElementDescriptor());
                if (childNodeModel != null) {
                    String fieldName = childNodeModel.getFieldName();
                    Object value = deserializeElement(childNodeModel.getDataTypeName());
                    if (value != null) {
                        if (value instanceof List) {
                            List<Object> currentList = (List<Object>) objectAccessor.getValue(object, fieldName);
                            if (currentList != null) {
                                currentList.addAll((List) value);
                            } else {
                                objectAccessor.setValue(object, fieldName, value);
                            }
                        } else {
                            objectAccessor.setValue(object, fieldName, value);
                        }
                    }
                }
            }
        }
        return object;
    }

    private List<Object> deserializeListElement(XmlListTypeMapping typeMapping) {
        List<Object> listValue = new ArrayList<Object>();
        if (typeMapping.isImplicit()) {
            listValue.add(deserializeElement(typeMapping.getNodeModel(getCurrentElementDescriptor())
                    .getDataTypeName()));
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                QName listItemDataTypeName = typeMapping.getNodeModel(getCurrentElementDescriptor())
                        .getDataTypeName();
                if (listItemDataTypeName != null) {
                    listValue.add(deserializeElement(listItemDataTypeName));
                }
            }
        }
        return listValue;
    }

    private ElementDescriptor getCurrentElementDescriptor() {
        ElementStart elementStart = (ElementStart) getCurrentXmlItem();
        return new ElementDescriptor(elementStart.getName(), elementStart.getDataTypeName());
    }

    private String getText() {
        moveToNextXmlItem();
        if (getCurrentXmlItemType() == XmlItemType.TEXT_CONTENT) {
            return ((org.jsefa.xml.lowlevel.TextContent) getCurrentXmlItem()).getText();
        } else {
            return "";
        }
    }

    private void moveToNextXmlItem() {
        this.lowLevelDeserializer.moveToNext();
    }

    private QName moveToNextElement() {
        while (this.lowLevelDeserializer.hasNext()) {
            moveToNextXmlItem();
            if (getCurrentXmlItemType() == XmlItemType.ELEMENT_START) {
                return ((ElementStart) getCurrentXmlItem()).getName();
            }
        }
        return null;
    }

    private boolean moveToNextElement(int elementDepth) {
        while (this.lowLevelDeserializer.hasNext()) {
            moveToNextXmlItem();
            if (getCurrentXmlItemType() == XmlItemType.ELEMENT_START) {
                if (getCurrentDepth() == elementDepth) {
                    return true;
                }
            } else if (getCurrentXmlItemType() == XmlItemType.ELEMENT_END) {
                if (((ElementEnd) getCurrentXmlItem()).getDepth() < elementDepth) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean moveToNextEntryPoint() {
        this.currentEntryPoint = null;
        QName elementName = moveToNextElement();
        while (elementName != null) {
            for (XmlEntryPoint entryPoint : this.entryPoints) {
                if (entryPoint.getDesignator().equals(elementName)) {
                    QName dataTypeName = ((ElementStart) getCurrentXmlItem()).getDataTypeName();
                    if (dataTypeName == null || dataTypeName.equals(entryPoint.getDataTypeName())) {
                        this.currentEntryPoint = entryPoint;
                        return true;
                    }
                }
            }
            elementName = moveToNextElement();
        }
        return false;
    }

    private XmlItem getCurrentXmlItem() {
        return this.lowLevelDeserializer.current();
    }

    private XmlItemType getCurrentXmlItemType() {
        return this.lowLevelDeserializer.currentType();
    }

    private int getCurrentDepth() {
        return this.lowLevelDeserializer.currentDepth();
    }

    private XmlSimpleTypeMapping getSimpleTypeMapping(QName dataTypeName) {
        return (XmlSimpleTypeMapping) this.typeMappingRegistry.get(dataTypeName);
    }

}
