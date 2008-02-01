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
import java.util.List;
import java.util.Map;

import org.jsefa.DeserializationException;
import org.jsefa.SerializationException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.lowlevel.XmlLowLevelDeserializer;
import org.jsefa.xml.lowlevel.model.Attribute;
import org.jsefa.xml.lowlevel.model.ElementEnd;
import org.jsefa.xml.lowlevel.model.ElementStart;
import org.jsefa.xml.lowlevel.model.TextContent;
import org.jsefa.xml.lowlevel.model.XmlItem;
import org.jsefa.xml.lowlevel.model.XmlItemType;
import org.jsefa.xml.mapping.AttributeDescriptor;
import org.jsefa.xml.mapping.AttributeMapping;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.ElementMapping;
import org.jsefa.xml.mapping.NodeMapping;
import org.jsefa.xml.mapping.TextContentDescriptor;
import org.jsefa.xml.mapping.TextContentMapping;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
import org.jsefa.xml.mapping.XmlSimpleTypeMapping;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;
import org.jsefa.xml.namespace.QName;

/**
 * Default implementation of {@link XmlDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlDeserializerImpl implements XmlDeserializer {
    private final XmlTypeMappingRegistry typeMappingRegistry;

    private final Map<ElementDescriptor, ElementMapping> entryElementMappings;

    private final XmlLowLevelDeserializer lowLevelDeserializer;

    private ElementMapping currentEntryElementMapping;

    XmlDeserializerImpl(XmlConfiguration config, Map<ElementDescriptor, ElementMapping> entryElementMappings,
            XmlLowLevelDeserializer lowLevelDeserializer) {
        this.typeMappingRegistry = config.getTypeMappingRegistry();
        this.entryElementMappings = entryElementMappings;
        this.lowLevelDeserializer = lowLevelDeserializer;
    }

    /**
     * {@inheritDoc}
     */
    public void open(Reader reader) {
        this.currentEntryElementMapping = null;
        try {
            this.lowLevelDeserializer.open(reader);
        } catch (Exception e) {
            throw new DeserializationException("Error while opening the deserialization stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        try {
            if (!this.lowLevelDeserializer.hasNext()) {
                return false;
            }
            if (this.currentEntryElementMapping == null) {
                return moveToNextEntryElement();
            } else {
                return true;
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T next() {
        try {
            if (!hasNext()) {
                return null;
            }
            return (T) deserializeElement(this.currentEntryElementMapping.getDataTypeName());
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException("Error while deserializing", e);
        } finally {
            this.currentEntryElementMapping = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close(boolean closeReader) {
        try {
            this.lowLevelDeserializer.close(closeReader);
        } catch (Exception e) {
            throw new SerializationException("Error while closing the deserialization stream");
        }
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
        ElementStart elementStart = getCurrentXmlItem();
        for (Attribute attribute : elementStart.getAttributes()) {
            AttributeDescriptor attributeDescriptor = new AttributeDescriptor(attribute.getName());
            AttributeMapping attributeMapping = typeMapping.getNodeMapping(attributeDescriptor);
            if (attributeMapping != null) {
                XmlSimpleTypeMapping attributeTypeMapping = getSimpleTypeMapping(attributeMapping
                        .getDataTypeName());
                Object value = attributeTypeMapping.getSimpleTypeConverter().fromString(attribute.getValue());
                if (value != null) {
                    objectAccessor.setValue(object, attributeMapping.getFieldDescriptor().getName(), value);
                }
            }
        }
        if (typeMapping.isTextContentAllowed()) {
            TextContentMapping textContentMapping = typeMapping
                    .getNodeMapping(TextContentDescriptor.getInstance());
            TypeMapping<QName> textContentTypeMapping = getSimpleTypeMapping(textContentMapping.getDataTypeName());
            String fieldName = textContentMapping.getFieldDescriptor().getName();
            Object value = deserializeSimpleElement((XmlSimpleTypeMapping) textContentTypeMapping);
            if (value != null) {
                objectAccessor.setValue(object, fieldName, value);
            }
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                ElementMapping childElementMapping = typeMapping.getNodeMapping(getCurrentElementDescriptor());
                if (childElementMapping != null) {
                    String fieldName = childElementMapping.getFieldDescriptor().getName();
                    Object value = deserializeElement(childElementMapping.getDataTypeName());
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
            listValue.add(deserializeElement(typeMapping.getElementMapping(getCurrentElementDescriptor())
                    .getDataTypeName()));
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                NodeMapping<?> listItemNodeMapping = typeMapping.getElementMapping(getCurrentElementDescriptor());
                if (listItemNodeMapping != null) {
                    listValue.add(deserializeElement(listItemNodeMapping.getDataTypeName()));
                }
            }
        }
        return listValue;
    }

    private ElementDescriptor getCurrentElementDescriptor() {
        ElementStart elementStart = getCurrentXmlItem();
        return new ElementDescriptor(elementStart.getName(), elementStart.getDataTypeName());
    }

    private String getText() {
        moveToNextXmlItem();
        if (getCurrentXmlItemType() == XmlItemType.TEXT_CONTENT) {
            return ((TextContent) getCurrentXmlItem()).getText();
        } else {
            return "";
        }
    }

    private void moveToNextXmlItem() {
        this.lowLevelDeserializer.moveToNext();
    }

    private boolean moveToNextElement() {
        while (this.lowLevelDeserializer.hasNext()) {
            moveToNextXmlItem();
            if (getCurrentXmlItemType() == XmlItemType.ELEMENT_START) {
                return true;
            }
        }
        return false;
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

    private boolean moveToNextEntryElement() {
        this.currentEntryElementMapping = null;
        while (moveToNextElement()) {
            this.currentEntryElementMapping = this.entryElementMappings.get(getCurrentElementDescriptor());
            if (this.currentEntryElementMapping != null) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T extends XmlItem> T getCurrentXmlItem() {
        return (T) this.lowLevelDeserializer.current();
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
