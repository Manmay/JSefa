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

package org.jsefa.xml.annotation;

import static org.jsefa.common.annotation.AnnotationParameterNames.CONVERTER_TYPE;
import static org.jsefa.common.annotation.AnnotationParameterNames.DATA_TYPE_NAME;
import static org.jsefa.common.annotation.AnnotationParameterNames.FORMAT;
import static org.jsefa.common.annotation.AnnotationParameterNames.NAME;
import static org.jsefa.common.annotation.AnnotationParameterNames.DEFAULT_NAME;
import static org.jsefa.common.annotation.AnnotationParameterNames.OBJECT_TYPE;
import static org.jsefa.common.annotation.AnnotationParameterNames.TEXT_MODE;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.annotation.AnnotatedFieldsProvider;
import org.jsefa.common.annotation.AnnotationDataProvider;
import org.jsefa.common.annotation.AnnotationException;
import org.jsefa.common.annotation.AnnotationParameterNames;
import org.jsefa.common.annotation.TypeMappingFactory;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingException;
import org.jsefa.common.validator.Validator;
import org.jsefa.common.validator.provider.ValidatorProvider;
import org.jsefa.xml.lowlevel.TextMode;
import org.jsefa.xml.mapping.AttributeDescriptor;
import org.jsefa.xml.mapping.AttributeMapping;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.ElementMapping;
import org.jsefa.xml.mapping.ElementMappingsBuilder;
import org.jsefa.xml.mapping.TextContentDescriptor;
import org.jsefa.xml.mapping.TextContentMapping;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
import org.jsefa.xml.mapping.XmlMapTypeMapping;
import org.jsefa.xml.mapping.XmlNodeMapping;
import org.jsefa.xml.mapping.XmlSimpleTypeMapping;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;
import org.jsefa.xml.mapping.support.XmlDataTypeDefaultNameRegistry;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;
import org.jsefa.xml.namespace.QNameParser;

/**
 * Factory for creating {@link TypeMapping}s from annotated classes.
 * <p>
 * It is thread safe.
 * 
 * @author Norman Lahme-Huetig
 * @author Matthias Derer
 */
public final class XmlTypeMappingFactory extends TypeMappingFactory<QName, XmlTypeMappingRegistry> {

    private static final String DEFAULT_DATA_TYPE_NAMES_URI = "http://www.jsefa.org/xml/types/defaults/object-types";

    private XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry;

    /**
     * Constructs a new <code>XmlTypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param validatorProvider the validator provider to use
     * @param objectAccessorProvider the object accessor provider to use
     * @param dataTypeDefaultNameRegistry the data type default name registry to use
     */
    public XmlTypeMappingFactory(XmlTypeMappingRegistry typeMappingRegistry,
            SimpleTypeConverterProvider simpleTypeConverterProvider, ValidatorProvider validatorProvider,
            ObjectAccessorProvider objectAccessorProvider, XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry) {
        super(typeMappingRegistry, simpleTypeConverterProvider, validatorProvider, objectAccessorProvider);
        this.dataTypeDefaultNameRegistry = dataTypeDefaultNameRegistry;
    }

    /**
     * Creates a new {@link TypeMapping} for the given object type from its annotations if it is not already registered
     * in the type mapping registry given to the constructor of the class. The resulting type mapping will be registered
     * in the type mapping registry under the returned name.
     * <p>
     * Additional type mappings will be created and registered recursivly if necessary.
     * 
     * @param objectType the object type
     * @return the name of the created type mapping. The type mapping itself can be retrieved using the type mapping
     *         registry.
     */
    public QName createIfAbsent(Class<?> objectType) {
        return createIfAbsent(objectType, null, null);
    }

    /**
     * {@inheritDoc}
     */
    protected QName getAnnotatedDataTypeName(Annotation annotation, Class<?> annotationContextClass) {
        return getAnnotatedDataTypeName(annotation, NamespaceManagerFactory.create(annotationContextClass));
    }

    private QName createIfAbsent(Class<?> objectType, Field field, Annotation annotation) {
        if (hasSimpleType(objectType)) {
            return createSimpleTypeMappingIfAbsent(objectType, field, annotation);
        } else if (hasComplexType(objectType)) {
            return createComplexTypeMappingIfAbsent(objectType);
        } else {
            throw new TypeMappingException("Unknown data type for class " + objectType.getName());
        }
    }

    private QName createSimpleTypeMappingIfAbsent(Class<?> objectType, Field field, Annotation annotation) {
        QName dataTypeName = createSimpleDataTypeName(objectType, field, annotation);
        if (prepareToCreate(objectType, dataTypeName)) {
            SimpleTypeConverter converter = createSimpleTypeConverter(objectType, field, annotation);
            getTypeMappingRegistry().register(new XmlSimpleTypeMapping(dataTypeName, objectType, converter));
        }
        return dataTypeName;
    }

    @SuppressWarnings("unchecked")
    private QName createComplexTypeMappingIfAbsent(Class<?> objectType) {
        NamespaceManager namespaceManager = NamespaceManagerFactory.create(objectType);
        QName dataTypeName = createComplexDataTypeName(objectType, namespaceManager);
        if (prepareToCreate(objectType, dataTypeName)) {
            for (Class<?> subObjectType : objectType.getAnnotation(XmlDataType.class).subObjectTypes()) {
                QName subDataTypeName = createComplexDataTypeName(subObjectType, NamespaceManagerFactory
                        .create(subObjectType));
                // QName subDataTypeName =
                // createComplexTypeMappingIfAbsent(subObjectType);
                getTypeMappingRegistry().registerSubtypeRelation(dataTypeName, subDataTypeName);
            }
            for (Class<?> subObjectType : objectType.getAnnotation(XmlDataType.class).subObjectTypes()) {
                createComplexTypeMappingIfAbsent(subObjectType);
            }
            Collection<XmlNodeMapping<?>> nodeMappings = new ArrayList<XmlNodeMapping<?>>();
            nodeMappings.addAll(createAttributeMappings(objectType, namespaceManager));
            TextContentMapping textContentMapping = createTextContentMapping(objectType);
            if (textContentMapping != null) {
                nodeMappings.add(textContentMapping);
            }
            nodeMappings.addAll(createElementMappings(objectType, namespaceManager));
            Validator validator = getValidatorFactory().createValidator(objectType, XmlElement.class,
                    XmlElementList.class, XmlElementMap.class, XmlTextContent.class, XmlAttribute.class);
            XmlComplexTypeMapping mapping = new XmlComplexTypeMapping(objectType, dataTypeName,
                    getObjectAccessorProvider().get(objectType), nodeMappings, validator);
            getTypeMappingRegistry().register(mapping);
        }
        return dataTypeName;
    }

    private QName createListTypeMappingIfAbsent(Field field, NamespaceManager namespaceManager) {
        QName dataTypeName = createCollectionDataTypeName(field);
        if (prepareToCreate(Collection.class, dataTypeName)) {
            XmlElementList xmlElementList = field.getAnnotation(XmlElementList.class);
            if (xmlElementList == null || xmlElementList.items().length == 0) {
                throw new AnnotationException("No XmlElementList annotation with proper content found");
            }
            ElementMappingsBuilder elementMappingsBuilder = new ElementMappingsBuilder();
            addElementMappingsForCollectionItemsOrMapValues(elementMappingsBuilder, field, null,
                    xmlElementList.items(), namespaceManager);
            XmlListTypeMapping listMapping = new XmlListTypeMapping(dataTypeName, xmlElementList.implicit(),
                    elementMappingsBuilder.getResult(), getObjectAccessorProvider().get(field.getType()));
            getTypeMappingRegistry().register(listMapping);
        }
        return dataTypeName;
    }

    private QName createMapTypeMappingIfAbsent(Field field, NamespaceManager namespaceManager) {
        QName dataTypeName = createMapDataTypeName(field);
        if (prepareToCreate(Map.class, dataTypeName)) {
            XmlElementMap xmlElementMap = field.getAnnotation(XmlElementMap.class);
            if (xmlElementMap == null || xmlElementMap.values().length == 0) {
                throw new AnnotationException("No XmlElementMap annotation with proper content found");
            }
            AttributeMapping mapKeyMapping = createAttributeMapping(createForMapKeyIfAbsent(field, xmlElementMap.key(),
                    namespaceManager), field, xmlElementMap.key(), namespaceManager);
            ElementMappingsBuilder elementMappingsBuilder = new ElementMappingsBuilder();
            addElementMappingsForCollectionItemsOrMapValues(elementMappingsBuilder, field, null,
                    xmlElementMap.values(), namespaceManager);
            XmlMapTypeMapping mapMapping = new XmlMapTypeMapping(dataTypeName, xmlElementMap.implicit(), mapKeyMapping,
                    elementMappingsBuilder.getResult(), getObjectAccessorProvider().get(field.getType()));
            getTypeMappingRegistry().register(mapMapping);
        }
        return dataTypeName;
    }

    @SuppressWarnings("unchecked")
    private Collection<AttributeMapping> createAttributeMappings(Class<?> objectType, 
    		NamespaceManager namespaceManager) {
        Collection<AttributeMapping> result = new ArrayList<AttributeMapping>();
        for (Field field : AnnotatedFieldsProvider.getAnnotatedFields(objectType, XmlAttribute.class)) {
            XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
            QName dataTypeName = getAnnotatedDataTypeName(xmlAttribute, namespaceManager);
            if (dataTypeName == null) {
                dataTypeName = createSimpleTypeMappingIfAbsent(field.getType(), field, xmlAttribute);
            } else {
                assertTypeMappingExists(dataTypeName);
            }
            result.add(createAttributeMapping(dataTypeName, field, xmlAttribute, namespaceManager));
        }
        return result;
    }

    private AttributeMapping createAttributeMapping(QName dataTypeName, Field field, Annotation annotation,
            NamespaceManager namespaceManager) {
        AttributeDescriptor attributeDescriptor = createAttributeDescriptor(annotation, field.getName(),
                namespaceManager);
        Class<?> normalizedObjectType = getTypeMappingRegistry().get(dataTypeName).getObjectType();
        AttributeMapping attributeMapping = new AttributeMapping(dataTypeName, attributeDescriptor,
                normalizedObjectType,
                new FieldDescriptor(field.getName(), getNormalizedFieldType(field, dataTypeName)),
                getValidatorFactory().createContextualValidator(normalizedObjectType, field, annotation,
                        XmlDataType.class));
        return attributeMapping;
    }

    @SuppressWarnings("unchecked")
    private TextContentMapping createTextContentMapping(Class<?> objectType) {
        TextContentMapping textContentMapping = null;
        Field textContentField = getTextContentField(objectType);
        if (textContentField != null) {
            if (AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, XmlElement.class, XmlElementList.class,
                    XmlElementMap.class).size() > 0) {
                throw new AnnotationException("No element declarations allowed if a text content declaration exists");
            }
            XmlTextContent xmlTextContent = textContentField.getAnnotation(XmlTextContent.class);
            QName fieldDataTypeName = createSimpleTypeMappingIfAbsent(textContentField.getType(), textContentField,
                    xmlTextContent);
            TextContentDescriptor textContentDescriptor = TextContentDescriptor.getInstance();
            TextMode textMode = getTextModeFromField(textContentField);
            Class<?> normalizedObjectType = getTypeMappingRegistry().get(fieldDataTypeName).getObjectType();
            textContentMapping = new TextContentMapping(fieldDataTypeName, textContentDescriptor, new FieldDescriptor(
                    textContentField.getName(), normalizedObjectType), getValidatorFactory().createContextualValidator(
                    normalizedObjectType, textContentField, xmlTextContent, XmlDataType.class), textMode);
        }
        return textContentMapping;
    }

    @SuppressWarnings("unchecked")
    private Collection<ElementMapping> createElementMappings(Class<?> objectType, NamespaceManager namespaceManager) {
        ElementMappingsBuilder elementMappingsBuilder = new ElementMappingsBuilder();
        for (Field field : AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, XmlElement.class,
                XmlElementList.class, XmlElementMap.class)) {
            if (hasCollectionType(field.getType()) && field.getAnnotation(XmlElementList.class) != null) {
                QName fieldDataTypeName = createListTypeMappingIfAbsent(field, namespaceManager);
                addElementMappingsForElementList(field, fieldDataTypeName, namespaceManager, elementMappingsBuilder);
            } else if (hasMapType(field.getType()) && field.getAnnotation(XmlElementMap.class) != null) {
                QName fieldDataTypeName = createMapTypeMappingIfAbsent(field, namespaceManager);
                addElementMappingsForElementMap(field, fieldDataTypeName, namespaceManager, elementMappingsBuilder);
            } else if (hasComplexType(field.getType())) {
                QName fieldDataTypeName = getAnnotatedDataTypeName(field.getAnnotation(XmlElement.class),
                        namespaceManager);
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createComplexTypeMappingIfAbsent(field.getType());
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                addElementMappingsForElement(field, fieldDataTypeName, namespaceManager, elementMappingsBuilder);
            } else {
                XmlElement xmlElement = field.getAnnotation(XmlElement.class);
                QName fieldDataTypeName = getAnnotatedDataTypeName(xmlElement, namespaceManager);
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createSimpleTypeMappingIfAbsent(field.getType(), field, xmlElement);
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                addElementMappingsForElement(field, fieldDataTypeName, namespaceManager, elementMappingsBuilder);
            }
        }
        return elementMappingsBuilder.getResult();
    }

    private void addElementMappingsForElement(Field field, QName fieldDataTypeName, NamespaceManager namespaceManager,
            ElementMappingsBuilder elementMappingsBuilder) {
        XmlElement xmlElement = field.getAnnotation(XmlElement.class);

        for (QName subDataTypeName : getTypeMappingRegistry().getDataTypeNameTreeElements(fieldDataTypeName)) {
            Class<?> subObjectType = getSubObjectType(field.getType(), subDataTypeName);
            ElementDescriptor elementDescriptor = createElementDescriptor(field, subObjectType, subDataTypeName,
                    namespaceManager);
            elementMappingsBuilder.addMapping(subDataTypeName, elementDescriptor, new FieldDescriptor(field.getName(),
                    subObjectType), getValidatorFactory().createContextualValidator(subObjectType, field, xmlElement,
                    XmlDataType.class), getTextModeFromField(field));
        }
    }

    private void addElementMappingsForElementList(Field field, QName fieldDataTypeName,
            NamespaceManager namespaceManager, ElementMappingsBuilder elementMappingsBuilder) {
        XmlElementList xmlElementList = field.getAnnotation(XmlElementList.class);
        if (xmlElementList.implicit()) {
            addElementMappingsForCollectionItemsOrMapValues(elementMappingsBuilder, field, fieldDataTypeName,
                    xmlElementList.items(), namespaceManager);
        }
        ElementDescriptor elementDescriptor = xmlElementList.implicit() ? new ElementDescriptor(null, fieldDataTypeName)
                : createElementDescriptor(field, field.getDeclaringClass(), fieldDataTypeName, namespaceManager);
        elementMappingsBuilder.addMapping(fieldDataTypeName, elementDescriptor, new FieldDescriptor(field.getName(),
                Collection.class), getValidatorFactory().createContextualValidator(Collection.class, field,
                xmlElementList, XmlDataType.class), getTextModeFromField(field));
    }

    private void addElementMappingsForElementMap(Field field, QName fieldDataTypeName,
            NamespaceManager namespaceManager, ElementMappingsBuilder elementMappingsBuilder) {
        XmlElementMap xmlElementMap = field.getAnnotation(XmlElementMap.class);
        if (xmlElementMap.implicit()) {
            addElementMappingsForCollectionItemsOrMapValues(elementMappingsBuilder, field, fieldDataTypeName,
                    xmlElementMap.values(), namespaceManager);
        }
        ElementDescriptor elementDescriptor = xmlElementMap.implicit() ? new ElementDescriptor(null, fieldDataTypeName)
                : createElementDescriptor(field, field.getDeclaringClass(), fieldDataTypeName, namespaceManager);
        elementMappingsBuilder.addMapping(fieldDataTypeName, elementDescriptor, new FieldDescriptor(field.getName(),
                Map.class), getValidatorFactory().createContextualValidator(Map.class, field, xmlElementMap,
                XmlDataType.class), getTextModeFromField(field));
    }

    private void addElementMappingsForCollectionItemsOrMapValues(ElementMappingsBuilder elementMappingsBuilder,
            Field field, QName fieldDataTypeName, Annotation[] annotations, NamespaceManager namespaceManager) {
        Set<Class<?>> objectTypes = new HashSet<Class<?>>();
        for (Annotation annotation : order(annotations)) {
            QName dataTypeName = createForCollectionItemOrMapValueIfAbsent(field, annotation, annotations.length,
                    namespaceManager);
            for (QName subDataTypeName : getTypeMappingRegistry().getDataTypeNameTreeElements(dataTypeName)) {
                Class<?> subObjectType = getSubObjectType(getTypeMappingRegistry().get(dataTypeName).getObjectType(),
                        subDataTypeName);
                if (!objectTypes.contains(subObjectType)) {
                    objectTypes.add(subObjectType);
                    ElementDescriptor elementDescriptor = createElementDescriptor(annotation, subDataTypeName,
                            namespaceManager);
                    elementMappingsBuilder.addMapping(fieldDataTypeName != null ? fieldDataTypeName : subDataTypeName,
                            elementDescriptor, subObjectType, new FieldDescriptor(field.getName(),
                                    getNormalizedFieldType(field, null)), getValidatorFactory()
                                    .createContextualValidator(subObjectType, field, annotation, XmlDataType.class),
                            (TextMode) AnnotationDataProvider.get(annotation, TEXT_MODE));
                }
            }
        }
    }

    private Class<?> getSubObjectType(Class<?> objectType, QName subDataTypeName) {
        if (getTypeMappingRegistry().get(subDataTypeName) != null) {
            return getTypeMappingRegistry().get(subDataTypeName).getObjectType();
        }
        for (Class<?> subObjectType : objectType.getAnnotation(XmlDataType.class).subObjectTypes()) {
            if (subDataTypeName.equals(createComplexDataTypeName(subObjectType, NamespaceManagerFactory
                    .create(subObjectType)))) {
                return subObjectType;
            }
        }
        throw new TypeMappingException("Unknown data type name: " + subDataTypeName);
    }

    private QName createForMapKeyIfAbsent(Field field, Annotation annotation, NamespaceManager namespaceManager) {
        QName dataTypeName = getAnnotatedDataTypeName(annotation, namespaceManager);
        if (dataTypeName != null) {
            assertTypeMappingIsSimple(dataTypeName);
            return dataTypeName;
        } else {
            Class<?> objectType = getMapKeyType(annotation, field);
            if (objectType == null) {
                throw new AnnotationException("Object type not determinable for map key of field: " + field.getName()
                        + " of class " + field.getDeclaringClass().getName());
            }
            if (!(hasSimpleType(objectType))) {
                throw new AnnotationException("Object type must be simple for map key of field:" + field.getName()
                        + " of class " + field.getDeclaringClass().getName());
            }
            return createSimpleTypeMappingIfAbsent(objectType, field, annotation);
        }
    }

    private QName createForCollectionItemOrMapValueIfAbsent(Field field, Annotation annotation, int itemOrValueCount,
            NamespaceManager namespaceManager) {
        QName dataTypeName = getAnnotatedDataTypeName(annotation, namespaceManager);
        if (dataTypeName != null) {
            assertTypeMappingExists(dataTypeName);
        } else {
            boolean singleType = itemOrValueCount == 1;
            Class<?> objectType = (hasMapType(field.getType())) ? getMapValueType(annotation, field, singleType)
                    : getCollectionItemType(annotation, field, singleType);
            if (objectType == null) {
                throw new AnnotationException(
                        "Object type not determinable for collection item or map value of field: " + field.getName()
                                + " of class " + field.getDeclaringClass().getName());
            }
            dataTypeName = createIfAbsent(objectType, field, annotation);
        }
        TypeMapping<?> typeMapping = getTypeMappingRegistry().get(dataTypeName);
        if (typeMapping instanceof XmlListTypeMapping) {
            if (((XmlListTypeMapping) typeMapping).isImplicit()) {
                throw new AnnotationException("No implicit lists inside lists or maps allowed");
            }
        }
        if (typeMapping instanceof XmlMapTypeMapping) {
            if (((XmlMapTypeMapping) typeMapping).isImplicit()) {
                throw new AnnotationException("No implicit maps inside lists or maps allowed");
            }
        }
        return dataTypeName;
    }

    private boolean hasComplexType(Class<?> objectType) {
        return objectType.getAnnotation(XmlDataType.class) != null;
    }

    private AttributeDescriptor createAttributeDescriptor(Annotation annotation, String defaultName,
            NamespaceManager namespaceManager) {
        QName name = QNameParser.parse(getAnnotatedName(annotation, defaultName), false, namespaceManager);
        return new AttributeDescriptor(name);
    }

    private ElementDescriptor createElementDescriptor(Field field, Class<?> objectType, QName dataTypeName,
            NamespaceManager namespaceManager) {
        QName name = null;
        String annotatedName = getAnnotatedName(field, null);
        if (annotatedName != null) {
            name = QNameParser.parse(annotatedName, true, namespaceManager);
        } else {
            if (AnnotationDataProvider.get(field, DEFAULT_NAME, XmlElement.class) == DefaultName.TYPE_DEFAULT_NAME) {
                if (objectType.isAnnotationPresent(XmlDataType.class)) {
                    name = QNameParser.parse(objectType.getAnnotation(XmlDataType.class).defaultElementName(), true,
                            NamespaceManagerFactory.create(objectType));
                }
            }
        }
        if (name == null) {
            name = QNameParser.parse(field.getName(), true, namespaceManager);
        }
        return new ElementDescriptor(name, dataTypeName);

    }

    private ElementDescriptor createElementDescriptor(Annotation annotation, QName dataTypeName,
            NamespaceManager namespaceManager) {
        QName name = QNameParser.parse(getAnnotatedName(annotation, null), true, namespaceManager);
        return new ElementDescriptor(name, dataTypeName);
    }

    @SuppressWarnings("unchecked")
    private Field getTextContentField(Class<?> objectType) {
        List<Field> result = AnnotatedFieldsProvider.getAnnotatedFields(objectType, XmlTextContent.class);
        if (result.size() > 1) {
            throw new AnnotationException("Only one XmlTextContent annotation allowed");
        } else if (result.size() == 0) {
            return null;
        } else {
            return result.get(0);
        }
    }

    private QName createSimpleDataTypeName(Class<?> objectType, Field field, Annotation annotation) {
        if ((annotation == null || (AnnotationDataProvider.get(annotation, FORMAT) == null && AnnotationDataProvider
                .get(annotation, CONVERTER_TYPE) == null))
                && !hasCollectionType(objectType)) {
            QName name = this.dataTypeDefaultNameRegistry.get(objectType);
            if (name == null) {
                name = QName.create(DEFAULT_DATA_TYPE_NAMES_URI, objectType.getName());
            }
            return name;
        } else {
            String localName = objectType.getName() + "@" + field.getName() + "@" + field.getDeclaringClass().getName();
            return QName.create(DEFAULT_DATA_TYPE_NAMES_URI, localName);
        }
    }

    @SuppressWarnings("unchecked")
    private QName createComplexDataTypeName(Class<?> objectType, NamespaceManager namespaceManager) {
        String nameStr = AnnotationDataProvider.get(objectType, NAME, XmlDataType.class);
        if (nameStr != null) {
            return QNameParser.parse(nameStr, true, namespaceManager);
        } else {
            QName name = this.dataTypeDefaultNameRegistry.get(objectType);
            if (name == null) {
                name = QName.create(DEFAULT_DATA_TYPE_NAMES_URI, objectType.getName());
            }
            return name;
        }
    }

    private QName createCollectionDataTypeName(Field field) {
        String localName = field.getName() + "@" + field.getDeclaringClass().getName();
        return QName.create(DEFAULT_DATA_TYPE_NAMES_URI, localName);
    }

    private QName createMapDataTypeName(Field field) {
        String localName = field.getName() + "@" + field.getDeclaringClass().getName();
        return QName.create(DEFAULT_DATA_TYPE_NAMES_URI, localName);
    }

    @SuppressWarnings("unchecked")
    private String getAnnotatedName(AnnotatedElement annotatedElement, String defaultName) {
        String name = AnnotationDataProvider.get(annotatedElement, NAME, XmlElement.class, XmlAttribute.class,
                XmlElementList.class, XmlElementMap.class);
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private String getAnnotatedName(Annotation annotation, String defaultName) {
        if (annotation != null && AnnotationDataProvider.get(annotation, NAME) != null) {
            return AnnotationDataProvider.get(annotation, NAME);
        } else {
            return defaultName;
        }
    }

    private QName getAnnotatedDataTypeName(Annotation annotation, NamespaceManager namespaceManager) {
        if (annotation != null && AnnotationDataProvider.get(annotation, DATA_TYPE_NAME) != null) {
            return QNameParser.parse((String) AnnotationDataProvider.get(annotation, DATA_TYPE_NAME), true,
                    namespaceManager);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private TextMode getTextModeFromField(Field field) {
        return (TextMode) AnnotationDataProvider.get(field, AnnotationParameterNames.TEXT_MODE, XmlElement.class,
                XmlTextContent.class, ListItem.class);
    }

    private <T extends Annotation> List<T> order(T[] items) {
        List<T> result = new ArrayList<T>();
        List<T> remainingItems = new ArrayList<T>(Arrays.asList(items));
        while (!remainingItems.isEmpty()) {
            for (T item : remainingItems) {
                if (!hasSuperTypeOfAny(item, remainingItems)) {
                    result.add(item);
                    remainingItems.remove(item);
                    break;
                }
            }
        }
        return result;
    }

    private <T extends Annotation> boolean hasSuperTypeOfAny(T item, List<T> listItems) {
        for (T innerItem : listItems) {
            if (item != innerItem) {
                Class<?> itemObjectType = AnnotationDataProvider.get(item, OBJECT_TYPE);
                Class<?> innerItemObjectType = AnnotationDataProvider.get(innerItem, OBJECT_TYPE);
                if (itemObjectType.isAssignableFrom(innerItemObjectType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Class<?> getNormalizedFieldType(Field field, QName dataTypeName) {
        if (hasMapType(field.getType())) {
            return Map.class;
        } else if (hasCollectionType(field.getType())) {
            return Collection.class;
        } else if (dataTypeName != null) {
            return getTypeMappingRegistry().get(dataTypeName).getObjectType();
        } else {
            return field.getType();
        }
    }

}
