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
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jsefa.SerializationException;
import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.ValidationException;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;
import org.jsefa.xml.mapping.AttributeMapping;
import org.jsefa.xml.mapping.ElementMapping;
import org.jsefa.xml.mapping.TextContentMapping;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
import org.jsefa.xml.mapping.XmlMapTypeMapping;
import org.jsefa.xml.mapping.XmlNodeMapping;
import org.jsefa.xml.mapping.XmlNodeType;
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

    private final Map<Class<?>, ElementMapping> entryElementMappings;

    private final XmlLowLevelSerializer lowLevelSerializer;

    private IdentityHashMap<Object, Object> complexObjectsOnPath;

    private boolean validate;

    XmlSerializerImpl(XmlConfiguration config, Map<Class<?>, ElementMapping> entryElementMappings,
            XmlLowLevelSerializer lowLevelSerializer) {
        this.typeMappingRegistry = config.getTypeMappingRegistry();
        this.entryElementMappings = entryElementMappings;
        this.lowLevelSerializer = lowLevelSerializer;
        this.complexObjectsOnPath = new IdentityHashMap<Object, Object>();
        this.validate = config.getValidationMode().equals(ValidationMode.SERIALIZATION)
                || config.getValidationMode().equals(ValidationMode.BOTH);
    }

    /**
     * {@inheritDoc}
     */
    public void open(Writer writer) {
        this.complexObjectsOnPath.clear();
        try {
            this.lowLevelSerializer.open(writer);
        } catch (Exception e) {
            throw new SerializationException("Error while opening the serialization stream");
        }

    }

    /**
     * {@inheritDoc}
     */
    public void write(Object object) {
        if (object == null) {
            return;
        }
        try {
            ElementMapping nodeMapping = ReflectionUtil.getNearest(object.getClass(), this.entryElementMappings);
            if (nodeMapping == null) {
                throw new SerializationException("The following class was not registered for serialization: "
                        + object.getClass());
            }
            if (this.validate) {
                assertValueIsValid(object, nodeMapping);
            }
            serializeElement(object, nodeMapping);
        } catch (SerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flush() {
        try {
            this.lowLevelSerializer.flush();
        } catch (Exception e) {
            throw new SerializationException("Error while flushing the serialization stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close(boolean closeWriter) {
        try {
            this.lowLevelSerializer.close(closeWriter);
        } catch (Exception e) {
            throw new SerializationException("Error while closing the serialization stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    public XmlLowLevelSerializer getLowLevelSerializer() {
        return this.lowLevelSerializer;
    }

    private void serializeAttribute(Object object, AttributeMapping attributeMapping) {
        if (object == null) {
            return;
        }
        String attributeValue = getSimpleTypeMapping(attributeMapping).getSimpleTypeConverter().toString(object);
        this.lowLevelSerializer.writeAttribute(attributeMapping.getNodeDescriptor().getName(), attributeValue);
    }

    private void serializeElement(Object object, ElementMapping elementMapping) {
        TypeMapping<?> typeMapping = this.typeMappingRegistry.get(elementMapping.getDataTypeName());
        if (typeMapping == null) {
            throw new SerializationException("No type mapping given for data type name "
                    + elementMapping.getDataTypeName());
        }
        if (typeMapping instanceof XmlSimpleTypeMapping) {
            serializeSimpleElement(object, elementMapping, (XmlSimpleTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlComplexTypeMapping) {
            serializeComplexElement(object, elementMapping, (XmlComplexTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlListTypeMapping) {
            serializeListElement(object, elementMapping, (XmlListTypeMapping) typeMapping);
        } else if (typeMapping instanceof XmlMapTypeMapping) {
            serializeMapElement(object, elementMapping, (XmlMapTypeMapping) typeMapping);
        }
    }

    private void serializeSimpleElement(Object object, ElementMapping elementMapping,
            XmlSimpleTypeMapping simpleTypeMapping) {
        Object value = getValue(object);
        if (value == null) {
            return;
        }
        writeStartElement(elementMapping);
        writeInjectedAttributes(object);
        String elementValue = simpleTypeMapping.getSimpleTypeConverter().toString(value);
        this.lowLevelSerializer.writeText(elementValue, elementMapping.getTextMode());
        this.lowLevelSerializer.writeEndElement();
    }

    private void serializeComplexElement(Object object, ElementMapping elementMapping,
            XmlComplexTypeMapping typeMapping) {
        Object value = getValue(object);
        if (value == null) {
            return;
        }
        if (this.complexObjectsOnPath.containsKey(value)) {
            throw new SerializationException("Cycle detected while serializing " + value);
        } else {
            this.complexObjectsOnPath.put(value, value);
        }
        ObjectAccessor objectAccessor = typeMapping.getObjectAccessor();
        writeStartElement(elementMapping);
        writeInjectedAttributes(object);
        for (String fieldName : typeMapping.getFieldNames(XmlNodeType.ATTRIBUTE)) {
            Object fieldValue = objectAccessor.getValue(value, fieldName);
            if (fieldValue != null) {
                AttributeMapping attributeMapping = typeMapping.getNodeMapping(fieldName,
                        getNormalizedObjectType(fieldValue));
                serializeAttribute(fieldValue, attributeMapping);
            }
        }

        for (String fieldName : typeMapping.getFieldNames(XmlNodeType.TEXT_CONTENT)) {
            Object fieldValue = objectAccessor.getValue(value, fieldName);
            if (fieldValue != null) {
                TextContentMapping textContentMapping = typeMapping.getNodeMapping(fieldName,
                        getNormalizedObjectType(fieldValue));
                String text = getSimpleTypeMapping(textContentMapping).getSimpleTypeConverter().toString(
                        fieldValue);
                this.lowLevelSerializer.writeText(text, textContentMapping.getTextMode());
            }

        }
        for (String fieldName : typeMapping.getFieldNames(XmlNodeType.ELEMENT)) {
            Object fieldValue = objectAccessor.getValue(value, fieldName);
            if (fieldValue != null) {
                ElementMapping childElementMapping = typeMapping.getNodeMapping(fieldName,
                        getNormalizedObjectType(fieldValue));
                if (childElementMapping == null) {
                    throw new SerializationException("Unable to serialize field class "
                            + getNormalizedObjectType(fieldValue).getName() + " for field " + fieldName
                            + " within object type " + typeMapping.getObjectType());
                }
                serializeElement(fieldValue, childElementMapping);
            }
        }
        this.lowLevelSerializer.writeEndElement();
        this.complexObjectsOnPath.remove(value);
    }

    private void serializeListElement(Object object, ElementMapping elementMapping,
            XmlListTypeMapping typeMapping) {
        Collection<?> listObject = getValue(object);
        if (listObject == null) {
            return;
        }
        if (!typeMapping.isImplicit()) {
            writeStartElement(elementMapping);
            writeInjectedAttributes(object);
        }
        for (Object listItemValue : listObject) {
            ElementMapping listItemMapping = typeMapping.getNodeMapping(listItemValue.getClass());
            if (listItemMapping == null) {
                throw new SerializationException("No element mapping found for list item with class "
                        + listItemValue.getClass());
            }
            serializeElement(listItemValue, listItemMapping);
        }
        if (!typeMapping.isImplicit()) {
            this.lowLevelSerializer.writeEndElement();
        }

    }

    private void serializeMapElement(Object object, ElementMapping elementMapping,
            XmlMapTypeMapping typeMapping) {
        Map<?, ?> mapObject = getValue(object);
        if (mapObject == null) {
            return;
        }
        if (!typeMapping.isImplicit()) {
            writeStartElement(elementMapping);
            writeInjectedAttributes(object);
        }
        for (Map.Entry<?, ?> entry : mapObject.entrySet()) {
            ElementMapping valueMapping = typeMapping.getValueNodeMapping(entry.getValue().getClass());
            if (valueMapping == null) {
                throw new SerializationException("No element mapping found for map value with class "
                        + entry.getClass());
            }
            serializeElement(new MapEntryData(entry.getKey(), entry.getValue(), typeMapping), valueMapping);
        }
        if (!typeMapping.isImplicit()) {
            this.lowLevelSerializer.writeEndElement();
        }

    }

    private XmlSimpleTypeMapping getSimpleTypeMapping(XmlNodeMapping<?> nodeMapping) {
        return (XmlSimpleTypeMapping) this.typeMappingRegistry.get(nodeMapping.getDataTypeName());
    }

    private Class<?> getNormalizedObjectType(Object value) {
        Class<?> objectType = value.getClass();
        if (Collection.class.isAssignableFrom(objectType)) {
            objectType = Collection.class;
        }
        return objectType;
    }

    private void writeStartElement(ElementMapping elementMapping) {
        if (elementMapping.elementNameIsAmbiguous()) {
            this.lowLevelSerializer.writeStartElement(elementMapping.getNodeDescriptor().getName(), elementMapping
                    .getDataTypeName());
        } else {
            this.lowLevelSerializer.writeStartElement(elementMapping.getNodeDescriptor().getName(), null);
        }
    }

    private void writeInjectedAttributes(Object object) {
        if (object instanceof MapEntryData) {
            MapEntryData mapEntryData = (MapEntryData) object;
            serializeAttribute(mapEntryData.key, mapEntryData.attributeMapping);
        }
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
    private <T> T getValue(Object object) {
        if (object instanceof MapEntryData) {
            return (T) ((MapEntryData) object).value;
        } else {
            return (T) object;
        }
    }

    private static final class MapEntryData {
        final Object key;
        final Object value;
        final AttributeMapping attributeMapping;

        MapEntryData(Object key, Object value, XmlMapTypeMapping mapTypeMapping) {
            this.key = key;
            this.value = value;
            this.attributeMapping = mapTypeMapping.getKeyNodeMapping();
        }
    }

}
