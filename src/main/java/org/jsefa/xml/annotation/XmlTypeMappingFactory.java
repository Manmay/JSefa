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

import static org.jsefa.common.annotation.AnnotationDataNames.CONVERTER_TYPE;
import static org.jsefa.common.annotation.AnnotationDataNames.DATA_TYPE_NAME;
import static org.jsefa.common.annotation.AnnotationDataNames.FORMAT;
import static org.jsefa.common.annotation.AnnotationDataNames.NAME;
import static org.jsefa.common.annotation.AnnotationDataNames.OBJECT_TYPE;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.annotation.AnnotatedFieldsProvider;
import org.jsefa.common.annotation.AnnotationDataProvider;
import org.jsefa.common.annotation.AnnotationException;
import org.jsefa.common.annotation.TypeMappingFactory;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingException;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.xml.mapping.AttributeDescriptor;
import org.jsefa.xml.mapping.AttributeMapping;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.ElementMapping;
import org.jsefa.xml.mapping.ElementMappingsBuilder;
import org.jsefa.xml.mapping.NodeMapping;
import org.jsefa.xml.mapping.TextContentDescriptor;
import org.jsefa.xml.mapping.TextContentMapping;
import org.jsefa.xml.mapping.XmlComplexTypeMapping;
import org.jsefa.xml.mapping.XmlListTypeMapping;
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
 * 
 */
public final class XmlTypeMappingFactory extends TypeMappingFactory<QName, XmlTypeMappingRegistry> {

    private static final String DEFAULT_DATA_TYPE_NAMES_URI = "http://www.jsefa.org/xml/types/defaults/object-types";

    private XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry;

    /**
     * Constructs a new <code>XmlTypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param objectAccessorProvider the object accessor provider to use
     * @param dataTypeDefaultNameRegistry the data type default name registry to use
     */
    public XmlTypeMappingFactory(XmlTypeMappingRegistry typeMappingRegistry,
            SimpleTypeConverterProvider simpleTypeConverterProvider,
            ObjectAccessorProvider objectAccessorProvider,
            XmlDataTypeDefaultNameRegistry dataTypeDefaultNameRegistry) {
        super(typeMappingRegistry, simpleTypeConverterProvider, objectAccessorProvider);
        this.dataTypeDefaultNameRegistry = dataTypeDefaultNameRegistry;
    }

    /**
     * Creates a new {@link TypeMapping} for the given object type from its annotations if it is not already
     * registered in the type mapping registry given to the constructor of the class. The resulting type mapping
     * will be registered in the type mapping registry under the returned name.
     * <p>
     * Additional type mappings will be created and registered recursivly if necessary.
     * 
     * @param objectType the object type
     * @return the name of the created type mapping. The type mapping itself can be retrieved using the type
     *         mapping registry.
     */
    public QName createIfAbsent(Class<?> objectType) {
        return createIfAbsent(objectType, null, null);
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
            String[] format = null;
            SimpleTypeConverter converter = null;
            if (annotation != null) {
                format = AnnotationDataProvider.get(annotation, FORMAT);
                if (AnnotationDataProvider.get(annotation, CONVERTER_TYPE) != null) {
                    Class<? extends SimpleTypeConverter> converterType = AnnotationDataProvider.get(annotation,
                            CONVERTER_TYPE);
                    converter = getSimpleTypeConverterProvider().getForConverterType(converterType, objectType,
                            format);
                }
            }
            if (converter == null && getSimpleTypeConverterProvider().hasConverterFor(objectType)) {
                converter = getSimpleTypeConverterProvider().getForObjectType(objectType, format);
            }
            if (converter == null) {
                throw new TypeMappingException("Could not create a simple type converter for " + objectType);
            }
            XmlSimpleTypeMapping simpleTypeMapping = new XmlSimpleTypeMapping(dataTypeName, objectType, converter);
            getTypeMappingRegistry().register(simpleTypeMapping);
        }
        return dataTypeName;
    }

    private QName createComplexTypeMappingIfAbsent(Class<?> objectType) {
        NamespaceManager namespaceManager = NamespaceManagerFactory.create(objectType);
        QName dataTypeName = createComplexDataTypeName(objectType, namespaceManager);
        if (prepareToCreate(objectType, dataTypeName)) {
            for (Class<?> subObjectType : objectType.getAnnotation(XmlDataType.class).subObjectTypes()) {
                QName subDataTypeName = createComplexTypeMappingIfAbsent(subObjectType);
                getTypeMappingRegistry().registerSubtypeRelation(dataTypeName, subDataTypeName);
            }
            Collection<NodeMapping<?>> nodeMappings = new ArrayList<NodeMapping<?>>();
            nodeMappings.addAll(createAttributeMappings(objectType, namespaceManager));
            TextContentMapping textContentMapping = createTextContentMapping(objectType);
            if (textContentMapping != null) {
                nodeMappings.add(textContentMapping);
            }
            nodeMappings.addAll(createElementMappings(objectType, namespaceManager));

            XmlComplexTypeMapping mapping = new XmlComplexTypeMapping(objectType, dataTypeName,
                    getObjectAccessorProvider().get(objectType), nodeMappings);
            getTypeMappingRegistry().register(mapping);
        }
        return dataTypeName;
    }

    @SuppressWarnings("unchecked")
    private Collection<AttributeMapping> createAttributeMappings(Class<?> objectType,
            NamespaceManager namespaceManager) {
        Collection<AttributeMapping> result = new ArrayList<AttributeMapping>();
        for (Field field : AnnotatedFieldsProvider.getAnnotatedFields(objectType, XmlAttribute.class)) {
            XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
            QName fieldDataTypeName = getAnnotatedFieldDataTypeName(xmlAttribute, namespaceManager);
            if (fieldDataTypeName == null) {
                fieldDataTypeName = createSimpleTypeMappingIfAbsent(field.getType(), field, xmlAttribute);
            } else {
                assertTypeMappingExists(fieldDataTypeName);
            }
            AttributeDescriptor attributeDescriptor = createAttributeDescriptor(field, namespaceManager);
            AttributeMapping attributeMapping = new AttributeMapping(fieldDataTypeName, attributeDescriptor,
                    new FieldDescriptor(field.getName(), getTypeMappingRegistry().get(fieldDataTypeName)
                            .getObjectType()));
            result.add(attributeMapping);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private TextContentMapping createTextContentMapping(Class<?> objectType) {
        TextContentMapping textContentMapping = null;
        Field textContentField = getTextContentField(objectType);
        if (textContentField != null) {
            if (AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, XmlElement.class,
                    XmlElementList.class).size() > 0) {
                throw new AnnotationException(
                        "No element declarations allowed if a text content declaration exists");
            }
            XmlTextContent xmlTextContent = textContentField.getAnnotation(XmlTextContent.class);
            QName fieldDataTypeName = createSimpleTypeMappingIfAbsent(textContentField.getType(),
                    textContentField, xmlTextContent);
            TextContentDescriptor textContentDescriptor = TextContentDescriptor.getInstance();
            textContentMapping = new TextContentMapping(fieldDataTypeName, textContentDescriptor,
                    new FieldDescriptor(textContentField.getName(), getTypeMappingRegistry()
                            .get(fieldDataTypeName).getObjectType()));
        }
        return textContentMapping;
    }

    @SuppressWarnings("unchecked")
    private Collection<ElementMapping> createElementMappings(Class<?> objectType, NamespaceManager namespaceManager) {
        ElementMappingsBuilder elementMappingsBuilder = new ElementMappingsBuilder();
        for (Field field : AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, XmlElement.class,
                XmlElementList.class)) {
            XmlElement xmlElement = field.getAnnotation(XmlElement.class);
            if (hasSimpleType(field.getType())) {
                QName fieldDataTypeName = getAnnotatedFieldDataTypeName(xmlElement, namespaceManager);
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createSimpleTypeMappingIfAbsent(field.getType(), field, xmlElement);
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                addElementMappingsForElement(field, fieldDataTypeName, namespaceManager, elementMappingsBuilder);
            } else if (hasListType(field.getType())) {
                QName fieldDataTypeName = createListTypeMappingIfAbsent(field, namespaceManager);
                addElementMappingsForElementList(field, fieldDataTypeName, namespaceManager,
                        elementMappingsBuilder);
            } else if (hasComplexType(field.getType())) {
                QName fieldDataTypeName = getAnnotatedFieldDataTypeName(xmlElement, namespaceManager);
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createComplexTypeMappingIfAbsent(field.getType());
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                addElementMappingsForElement(field, fieldDataTypeName, namespaceManager, elementMappingsBuilder);
            } else {
                throw new TypeMappingException("Unknown data type for class " + field.getType());
            }
        }
        return elementMappingsBuilder.getResult();
    }

    private void addElementMappingsForElement(Field field, QName fieldDataTypeName,
            NamespaceManager namespaceManager, ElementMappingsBuilder elementMappingsBuilder) {
        for (QName subDataTypeName : getTypeMappingRegistry().getDataTypeNameTreeElements(fieldDataTypeName)) {
            ElementDescriptor elementDescriptor = createElementDescriptor(field, subDataTypeName, namespaceManager);
            elementMappingsBuilder.addMapping(subDataTypeName, elementDescriptor, new FieldDescriptor(field
                    .getName(), getTypeMappingRegistry().get(subDataTypeName).getObjectType()));
        }
    }

    private void addElementMappingsForElementList(Field field, QName fieldDataTypeName,
            NamespaceManager namespaceManager, ElementMappingsBuilder elementMappingsBuilder) {
        XmlElementList xmlElementList = field.getAnnotation(XmlElementList.class);
        if (xmlElementList.implicit()) {
            for (ListItem listItem : xmlElementList.items()) {
                boolean singleType = xmlElementList.items().length == 1;
                QName listItemDataTypeName = createIfAbsent(field, listItem, singleType, namespaceManager);
                for (QName subDataTypeName : getTypeMappingRegistry().getDataTypeNameTreeElements(
                        listItemDataTypeName)) {
                    ElementDescriptor elementDescriptor = createElementDescriptor(listItem, subDataTypeName,
                            namespaceManager);
                    elementMappingsBuilder.addMapping(fieldDataTypeName, elementDescriptor,
                            getTypeMappingRegistry().get(subDataTypeName).getObjectType(), new FieldDescriptor(
                                    field.getName(), List.class));
                }
            }
        } else {
            ElementDescriptor elementDescriptor = createElementDescriptor(field, fieldDataTypeName,
                    namespaceManager);
            elementMappingsBuilder.addMapping(fieldDataTypeName, elementDescriptor, new FieldDescriptor(field
                    .getName(), List.class));
        }
    }

    private QName createListTypeMappingIfAbsent(Field field, NamespaceManager namespaceManager) {
        QName dataTypeName = createListDataTypeName(field);
        if (prepareToCreate(List.class, dataTypeName)) {
            XmlElementList xmlElementList = field.getAnnotation(XmlElementList.class);
            if (xmlElementList == null || xmlElementList.items().length == 0) {
                throw new AnnotationException("No XmlElementList annotation with proper content found");
            }
            boolean singleType = xmlElementList.items().length == 1;
            ElementMappingsBuilder elementMappingsBuilder = new ElementMappingsBuilder();
            Set<Class<?>> objectTypes = new HashSet<Class<?>>();
            for (ListItem listItem : order(xmlElementList.items())) {
                QName listItemDataTypeName = createIfAbsent(field, listItem, singleType, namespaceManager);
                for (QName subDataTypeName : getTypeMappingRegistry().getDataTypeNameTreeElements(
                        listItemDataTypeName)) {
                    Class<?> subObjectType = getTypeMappingRegistry().get(subDataTypeName).getObjectType();
                    if (!objectTypes.contains(subObjectType)) {
                        objectTypes.add(subObjectType);
                        ElementDescriptor listItemElementDescriptor = createElementDescriptor(listItem,
                                subDataTypeName, namespaceManager);
                        elementMappingsBuilder.addMapping(subDataTypeName, listItemElementDescriptor,
                                subObjectType, new FieldDescriptor(field.getName(), List.class));
                    }
                }
            }
            XmlListTypeMapping listMapping = new XmlListTypeMapping(dataTypeName, xmlElementList.implicit(),
                    elementMappingsBuilder.getResult());
            getTypeMappingRegistry().register(listMapping);
        }
        return dataTypeName;
    }

    private QName createIfAbsent(Field field, ListItem listItem, boolean singleType,
            NamespaceManager namespaceManager) {
        QName listItemDataTypeName;
        if (AnnotationDataProvider.get(listItem, DATA_TYPE_NAME) != null) {
            listItemDataTypeName = QNameParser.parse(listItem.dataTypeName(), true, namespaceManager);
            assertTypeMappingExists(listItemDataTypeName);
            TypeMapping<?> listItemTypeMapping = getTypeMappingRegistry().get(listItemDataTypeName);
            if (listItemTypeMapping instanceof XmlListTypeMapping) {
                if (((XmlListTypeMapping) listItemTypeMapping).isImplicit()) {
                    throw new AnnotationException("No lists inside lists allowed");
                }
            }
        } else if (AnnotationDataProvider.get(listItem, OBJECT_TYPE) != null) {
            if (hasListType(listItem.objectType())) {
                throw new AnnotationException("No lists inside lists allowed!");
            }
            listItemDataTypeName = createIfAbsent(listItem.objectType(), field, listItem);
        } else if (singleType && ReflectionUtil.getListEntryObjectType(field) != null) {
            listItemDataTypeName = createIfAbsent(ReflectionUtil.getListEntryObjectType(field), field, listItem);
        } else {
            throw new AnnotationException("Neither dataTypeName nor objectType is given for list item of field: "
                    + field.getName() + " of class " + field.getDeclaringClass().getName());
        }
        return listItemDataTypeName;
    }

    private boolean hasComplexType(Class<?> objectType) {
        return objectType.getAnnotation(XmlDataType.class) != null;
    }

    private AttributeDescriptor createAttributeDescriptor(Field field, NamespaceManager namespaceManager) {
        QName name = QNameParser.parse(getAnnotatedName(field, field.getName()), false, namespaceManager);
        return new AttributeDescriptor(name);
    }

    private ElementDescriptor createElementDescriptor(Field field, QName dataTypeName,
            NamespaceManager namespaceManager) {
        QName name = QNameParser.parse(getAnnotatedName(field, field.getName()), true, namespaceManager);
        return new ElementDescriptor(name, dataTypeName);
    }

    private ElementDescriptor createElementDescriptor(ListItem listItem, QName dataTypeName,
            NamespaceManager namespaceManager) {
        QName name = QNameParser.parse(listItem.name(), true, namespaceManager);
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
        if (annotation == null
                || (AnnotationDataProvider.get(annotation, FORMAT) == null && AnnotationDataProvider.get(
                        annotation, CONVERTER_TYPE) == null)) {
            QName name = this.dataTypeDefaultNameRegistry.get(objectType);
            if (name == null) {
                name = QName.create(DEFAULT_DATA_TYPE_NAMES_URI, objectType.getName());
            }
            return name;
        } else {
            String localName = objectType.getName() + "@" + field.getName() + "@"
                    + field.getDeclaringClass().getName();
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

    private QName createListDataTypeName(Field field) {
        String localName = field.getName() + "@" + field.getDeclaringClass().getName();
        return QName.create(DEFAULT_DATA_TYPE_NAMES_URI, localName);
    }

    @SuppressWarnings("unchecked")
    private String getAnnotatedName(AnnotatedElement annotatedElement, String defaultName) {
        String name = AnnotationDataProvider.get(annotatedElement, NAME, XmlElement.class, XmlAttribute.class,
                XmlElementList.class);
        if (name != null) {
            return name;
        } else {
            return defaultName;
        }
    }

    private QName getAnnotatedFieldDataTypeName(Annotation annotation, NamespaceManager namespaceManager) {
        if (annotation != null && AnnotationDataProvider.get(annotation, DATA_TYPE_NAME) != null) {
            QName fieldDataTypeName = QNameParser.parse((String) AnnotationDataProvider.get(annotation,
                    DATA_TYPE_NAME), true, namespaceManager);
            return fieldDataTypeName;
        } else {
            return null;
        }
    }

    private List<ListItem> order(ListItem[] items) {
        List<ListItem> result = new ArrayList<ListItem>();
        List<ListItem> remainingItems = new ArrayList<ListItem>(Arrays.asList(items));
        while (!remainingItems.isEmpty()) {
            for (ListItem item : remainingItems) {
                if (!hasSuperTypeOfAny(item, remainingItems)) {
                    result.add(item);
                    remainingItems.remove(item);
                    break;
                }
            }
        }
        return result;
    }

    private boolean hasSuperTypeOfAny(ListItem item, List<ListItem> listItems) {
        for (ListItem innerItem : listItems) {
            if (item != innerItem) {
                if (item.objectType().isAssignableFrom(innerItem.objectType())) {
                    return true;
                }
            }
        }
        return false;
    }

}
