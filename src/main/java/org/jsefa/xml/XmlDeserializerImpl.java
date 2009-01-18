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
import java.util.Collection;
import java.util.Map;

import org.jsefa.DeserializationException;
import org.jsefa.ObjectPathElement;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.common.lowlevel.InputPosition;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.validator.ValidationException;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
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
import org.jsefa.xml.mapping.TextContentDescriptor;
import org.jsefa.xml.mapping.TextContentMapping;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
import org.jsefa.xml.mapping.XmlMapTypeMapping;
import org.jsefa.xml.mapping.XmlNodeMapping;
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

    private boolean validate;

    XmlDeserializerImpl(XmlConfiguration config, Map<ElementDescriptor, ElementMapping> entryElementMappings,
            XmlLowLevelDeserializer lowLevelDeserializer) {
        this.typeMappingRegistry = config.getTypeMappingRegistry();
        this.entryElementMappings = entryElementMappings;
        this.lowLevelDeserializer = lowLevelDeserializer;
        this.validate = config.getValidationMode().equals(ValidationMode.DESERIALIZATION)
                || config.getValidationMode().equals(ValidationMode.BOTH);
    }

    /**
     * {@inheritDoc}
     */
    public void open(Reader reader) {
        open(reader, null);
    }

    /**
     * {@inheritDoc}
     */
    public void open(Reader reader, String baseURI) {
        this.currentEntryElementMapping = null;
        try {
            this.lowLevelDeserializer.open(reader, baseURI);
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
            throw new DeserializationException(e).setInputPosition(getInputPosition());
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
            T result = (T) deserializeElement(this.currentEntryElementMapping.getDataTypeName());
            if (this.validate && result != null) {
                assertValueIsValid(result, this.currentEntryElementMapping);
            }
            return result;
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException(e).setInputPosition(getInputPosition());
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
            throw new DeserializationException("Error while closing the deserialization stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputPosition getInputPosition() {
        return this.lowLevelDeserializer.getInputPosition();
    }

    private Object deserializeElement(QName dataTypeName) {
        TypeMapping<QName> typeMapping = this.typeMappingRegistry.get(dataTypeName);
        if (typeMapping instanceof XmlSimpleTypeMapping) {
            return deserializeSimpleElement((XmlSimpleTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlComplexTypeMapping) {
            return deserializeComplexElement((XmlComplexTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlListTypeMapping) {
            return deserializeListElement((XmlListTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlMapTypeMapping) {
            return deserializeMapElement((XmlMapTypeMapping) typeMapping);
        } else {
            throw new IllegalArgumentException(
                    "Argument dataTypeName maps to a type mapping with unknown type: "
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
            try {
                if (attributeMapping != null) {
                    XmlSimpleTypeMapping attributeTypeMapping = getSimpleTypeMapping(attributeMapping
                            .getDataTypeName());
                    Object value = attributeTypeMapping.getSimpleTypeConverter().fromString(attribute.getValue());
                    if (value != null) {
                        objectAccessor.setValue(object, attributeMapping.getFieldDescriptor().getName(), value);
                    }
                }
            } catch (Exception e) {
                throw createException(e, typeMapping, attributeMapping.getFieldDescriptor().getName());
            }
        }
        if (typeMapping.isTextContentAllowed()) {
            TextContentMapping textContentMapping = typeMapping
                    .getNodeMapping(TextContentDescriptor.getInstance());
            TypeMapping<QName> textContentTypeMapping = getSimpleTypeMapping(textContentMapping.getDataTypeName());
            String fieldName = textContentMapping.getFieldDescriptor().getName();
            try {
                Object value = deserializeSimpleElement((XmlSimpleTypeMapping) textContentTypeMapping);
                if (value != null) {
                    objectAccessor.setValue(object, fieldName, value);
                }
            } catch (Exception e) {
                throw createException(e, typeMapping, fieldName);
            }
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                ElementMapping childElementMapping = typeMapping.getNodeMapping(getCurrentElementDescriptor());
                if (childElementMapping != null) {
                    String fieldName = childElementMapping.getFieldDescriptor().getName();
                    try {
                        Object value = deserializeElement(childElementMapping.getDataTypeName());
                        if (value != null) {
                            if (value instanceof Collection) {
                                Collection<Object> currentValue = (Collection<Object>) objectAccessor.getValue(object,
                                        fieldName);
                                if (currentValue != null) {
                                    currentValue.addAll((Collection) value);
                                } else {
                                    objectAccessor.setValue(object, fieldName, value);
                                }
                            } else if (value instanceof Map) {
                                Map<Object, Object> currentValue = (Map<Object, Object>) objectAccessor.getValue(object,
                                        fieldName);
                                if (currentValue != null) {
                                    currentValue.putAll((Map) value);
                                } else {
                                    objectAccessor.setValue(object, fieldName, value);
                                }
                            } else {
                                objectAccessor.setValue(object, fieldName, value);
                            }
                        }
                    } catch (Exception e) {
                        throw createException(e, typeMapping, fieldName);
                    }
                }
            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> deserializeListElement(XmlListTypeMapping typeMapping) {
        Collection<Object> listValue = (Collection<Object>) typeMapping.getObjectAccessor().createObject();
        if (typeMapping.isImplicit()) {
            XmlNodeMapping<?> listItemNodeMapping = typeMapping.getNodeMapping(getCurrentElementDescriptor());
            listValue.add(deserializeElement(listItemNodeMapping.getDataTypeName()));
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                XmlNodeMapping<?> listItemNodeMapping = typeMapping.getNodeMapping(getCurrentElementDescriptor());
                if (listItemNodeMapping != null) {
                    listValue.add(deserializeElement(listItemNodeMapping.getDataTypeName()));
                }
            }
        }
        return listValue;
    }
    
    @SuppressWarnings("unchecked")
    private Map<?, ?> deserializeMapElement(XmlMapTypeMapping typeMapping) {
        Map<Object, Object> map = (Map<Object, Object>) typeMapping.getObjectAccessor().createObject();
        if (typeMapping.isImplicit()) {
            XmlNodeMapping<?> valueNodeMapping = typeMapping.getValueNodeMapping(getCurrentElementDescriptor());
            map.put(deserializeMapKey(typeMapping), deserializeElement(valueNodeMapping.getDataTypeName()));
        } else {
            int childDepth = getCurrentDepth() + 1;
            while (moveToNextElement(childDepth)) {
                XmlNodeMapping<?> valueNodeMapping = typeMapping.getValueNodeMapping(getCurrentElementDescriptor());
                if (valueNodeMapping != null) {
                    map.put(deserializeMapKey(typeMapping), deserializeElement(valueNodeMapping.getDataTypeName()));
                }
            }
        }
        return map;
    }
    
    private Object deserializeMapKey(XmlMapTypeMapping typeMapping) {
        ElementStart elementStart = getCurrentXmlItem();
        QName keyName = typeMapping.getKeyNodeMapping().getNodeDescriptor().getName();
        for (Attribute attribute : elementStart.getAttributes()) {
            if (keyName.equals(attribute.getName())) {
                XmlSimpleTypeMapping attributeTypeMapping = getSimpleTypeMapping(typeMapping.getKeyNodeMapping()
                        .getDataTypeName());
                return attributeTypeMapping.getSimpleTypeConverter().fromString(attribute.getValue());
            }
        }
        throw new DeserializationException("No attribute " + keyName + " serving as key for map entry found");
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
    private void assertValueIsValid(Object object, XmlNodeMapping nodeMapping) {
        Validator validator = nodeMapping.getValidator();
        if (validator != null) {
            ValidationResult result = validator.validate(object);
            if (!result.isValid()) {
                throw new ValidationException(result);
            }
        }
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

    private DeserializationException createException(Exception cause, TypeMapping<?> typeMapping, String fieldName) {
        ObjectPathElement elem = new ObjectPathElement(typeMapping.getObjectType(), fieldName);
        if (cause instanceof DeserializationException) {
            return ((DeserializationException) cause).add(elem);
        } else {
            return new DeserializationException(cause).setInputPosition(getInputPosition()).add(elem);
        }
    }

}
