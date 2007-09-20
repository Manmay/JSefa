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

package org.jsefa.rbf.annotation;

import static org.jsefa.common.annotation.AnnotationDataNames.CONVERTER_TYPE;
import static org.jsefa.common.annotation.AnnotationDataNames.DATA_TYPE_NAME;
import static org.jsefa.common.annotation.AnnotationDataNames.FORMAT;
import static org.jsefa.common.annotation.AnnotationDataNames.NAME;
import static org.jsefa.common.annotation.AnnotationDataNames.OBJECT_TYPE;
import static org.jsefa.rbf.annotation.RbfAnnotationDataNames.DEFAULT_PREFIX;
import static org.jsefa.rbf.annotation.RbfAnnotationDataNames.PREFIX;
import static org.jsefa.rbf.annotation.RbfAnnotationDataNames.RECORDS;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.annotation.AnnotatedFieldsProvider;
import org.jsefa.common.annotation.AnnotationDataProvider;
import org.jsefa.common.annotation.AnnotationException;
import org.jsefa.common.annotation.TypeMappingFactory;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingException;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.rbf.mapping.NodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Factory for creating {@link TypeMapping}s from annotated classes.
 * <p>
 * It is thread-safe and all subclasses must be thread-safe, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class RbfTypeMappingFactory extends TypeMappingFactory<String, RbfTypeMappingRegistry> {

    private final RbfAnnotations annotations;

    /**
     * Constructs a new <code>AbstractRbfTypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be
     *                registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to
     *                use
     * @param objectAccessorProvider the object accessor provider to use
     * @param annotations the parameter objects providing the annotation classes
     *                to use.
     */
    public RbfTypeMappingFactory(RbfTypeMappingRegistry typeMappingRegistry,
            SimpleTypeConverterProvider simpleTypeConverterProvider,
            ObjectAccessorProvider objectAccessorProvider, RbfAnnotations annotations) {
        super(typeMappingRegistry, simpleTypeConverterProvider, objectAccessorProvider);
        this.annotations = annotations;
    }

    /**
     * Creates a type mapping for the given object type and returns its name.
     * 
     * @param objectType the object type.
     * @return the type mapping name.
     */
    public final String createIfAbsent(Class<?> objectType) {
        if (!hasComplexType(objectType)) {
            throw new AnnotationException("The class " + objectType + " has no data type annotation");
        }
        return createComplexTypeMappingIfAbsent(objectType, true);
    }

    /**
     * Creates a new simple type mapping.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param converter the converter
     * @param field the field
     * @return the simple type mapping.
     */
    protected abstract TypeMapping<String> createSimpleTypeMapping(Class<?> objectType, String dataTypeName,
            SimpleTypeConverter converter, Field field);

    private String createSimpleTypeMappingIfAbsent(Class<?> objectType, Field field, Annotation fieldAnnotation) {
        String dataTypeName = createSimpleDataTypeName(field);
        if (getTypeMappingRegistry().get(dataTypeName) == null) {
            String[] format = null;
            SimpleTypeConverter converter = null;
            if (fieldAnnotation != null) {
                format = AnnotationDataProvider.get(fieldAnnotation, FORMAT);
                if (AnnotationDataProvider.get(fieldAnnotation, CONVERTER_TYPE) != null) {
                    Class<? extends SimpleTypeConverter> converterType = AnnotationDataProvider.get(
                            fieldAnnotation, CONVERTER_TYPE);
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
            getTypeMappingRegistry().register(createSimpleTypeMapping(objectType, dataTypeName, converter, field));
        }
        return dataTypeName;
    }

    private String createComplexTypeMappingIfAbsent(Class<?> objectType, boolean subRecordsAllowed) {
        String dataTypeName = createComplexDataTypeName(objectType);
        if (getTypeMappingRegistry().get(dataTypeName) == null) {
            RbfComplexTypeMapping complexTypeMapping = new RbfComplexTypeMapping(objectType, dataTypeName,
                    getObjectAccessorProvider().get(objectType));
            getTypeMappingRegistry().register(complexTypeMapping);
            addFields(complexTypeMapping);
            if (subRecordsAllowed) {
                addSubRecords(complexTypeMapping);
            } else {
                assertNoSubRecordsDeclared(complexTypeMapping);
            }
            complexTypeMapping.finish();
        }
        return dataTypeName;
    }

    @SuppressWarnings("unchecked")
    private void addFields(RbfComplexTypeMapping complexTypeMapping) {
        Class<?> objectType = complexTypeMapping.getObjectType();
        for (Field field : AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, this.annotations
                .getFieldAnnotationClass())) {
            String fieldDataTypeName = AnnotationDataProvider.get(field, DATA_TYPE_NAME, this.annotations
                    .getFieldAnnotationClass());
            Annotation fieldAnnotation = field.getAnnotation(this.annotations.getFieldAnnotationClass());
            if (hasSimpleType(field.getType())) {
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createSimpleTypeMappingIfAbsent(field.getType(), field, fieldAnnotation);
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                complexTypeMapping.register(field.getName(), NodeType.FIELD, fieldDataTypeName);
            } else if (hasComplexType(field.getType())) {
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createComplexTypeMappingIfAbsent(field.getType(), false);
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                complexTypeMapping.register(field.getName(), NodeType.FIELD, fieldDataTypeName);
            } else {
                throw new TypeMappingException("Can not create a type mapping for field " + field.getName()
                        + " of class " + objectType.getName());
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void addSubRecords(RbfComplexTypeMapping complexTypeMapping) {
        Class<?> objectType = complexTypeMapping.getObjectType();
        int requiredPrefixLength = getRequiredPrefixLength(objectType);
        for (Field field : AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, this.annotations
                .getSubRecordAnnotationClass(), this.annotations.getSubRecordListAnnotationClass())) {
            if (hasComplexType(field.getType())) {
                String fieldDataTypeName = AnnotationDataProvider.get(field, DATA_TYPE_NAME, this.annotations
                        .getSubRecordAnnotationClass());
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createComplexTypeMappingIfAbsent(field.getType(), true);
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                String prefix = AnnotationDataProvider.get(field, PREFIX, this.annotations
                        .getSubRecordAnnotationClass());
                if (prefix.length() != requiredPrefixLength) {
                    throw new AnnotationException("The object type " + field.getType()
                            + " must have a prefix with length " + requiredPrefixLength);
                }
                complexTypeMapping.registerWithPrefix(field.getName(), NodeType.SUB_RECORD, fieldDataTypeName,
                        prefix);
            } else if (hasListType(field.getType())) {
                String listDataTypeName = createListTypeMappingIfAbsent(field, requiredPrefixLength);
                complexTypeMapping.register(field.getName(), NodeType.SUB_RECORD, listDataTypeName);
            } else {
                throw new TypeMappingException("Object type not supported for sub record: " + objectType.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void assertNoSubRecordsDeclared(RbfComplexTypeMapping complexTypeMapping) {
        Class<?> objectType = complexTypeMapping.getObjectType();
        int counter = AnnotatedFieldsProvider
                .getSortedAnnotatedFields(objectType, this.annotations.getSubRecordAnnotationClass(),
                        this.annotations.getSubRecordListAnnotationClass()).size();
        if (counter > 0) {
            throw new TypeMappingException("No sub records nor sub record lists allowed within embedded type: "
                    + objectType.getName());
        }
    }

    private String createListTypeMappingIfAbsent(Field field, int requiredPrefixLength) {
        Annotation subRecordListAnnotation = field.getAnnotation(this.annotations
                .getSubRecordListAnnotationClass());
        String dataTypeName = createListDataTypeName(field);
        if (getTypeMappingRegistry().get(dataTypeName) == null) {
            if (subRecordListAnnotation == null || getRecords(subRecordListAnnotation).length == 0) {
                throw new AnnotationException("No FlrSubRecordList annotation with proper content found");
            }
            RbfListTypeMapping listTypeMapping = new RbfListTypeMapping(dataTypeName);
            getTypeMappingRegistry().register(listTypeMapping);
            Record[] records = getRecords(subRecordListAnnotation);
            for (Record record : records) {
                String listItemDataTypeName = AnnotationDataProvider.get(record, DATA_TYPE_NAME);
                Class<?> listItemObjectType = AnnotationDataProvider.get(record, OBJECT_TYPE);
                if (listItemDataTypeName == null) {
                    if (listItemObjectType == null && records.length == 1) {
                        listItemObjectType = ReflectionUtil.getListEntryObjectType(field);
                    }
                    assertHasSimpleType(listItemObjectType, field);
                    listItemDataTypeName = createComplexTypeMappingIfAbsent(listItemObjectType, true);
                } else {
                    assertTypeMappingExists(listItemDataTypeName);
                    listItemObjectType = getTypeMappingRegistry().get(listItemDataTypeName).getObjectType();
                }
                String prefix = record.prefix();
                if (prefix.length() != requiredPrefixLength) {
                    throw new AnnotationException("All record annotations of the list field " + field.getName()
                            + " of class " + field.getDeclaringClass().getName()
                            + " must have a prefix with length " + requiredPrefixLength);
                }
                listTypeMapping.register(listItemDataTypeName, prefix, listItemObjectType);
            }
            listTypeMapping.finish();
        }
        return dataTypeName;
    }

    private void assertHasSimpleType(Class<?> listItemObjectType, Field field) {
        if (listItemObjectType == null) {
            throw new AnnotationException("Neither dataTypeName nor objectType is given for list item of field: "
                    + field.getName() + " of class " + field.getDeclaringClass().getName());
        }
        if (hasListType(listItemObjectType)) {
            throw new TypeMappingException("No lists inside lists allowed!");
        }
        if (!hasComplexType(listItemObjectType)) {
            throw new AnnotationException("The sub record object type " + listItemObjectType.getName()
                    + " must have a data type annotation");
        }
    }

    private Record[] getRecords(Annotation annotation) {
        return AnnotationDataProvider.get(annotation, RECORDS);
    }

    private boolean hasComplexType(Class<?> objectType) {
        return objectType.isAnnotationPresent(this.annotations.getDataTypeAnnotationClass());
    }

    private String createSimpleDataTypeName(Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }

    private String createComplexDataTypeName(Class<?> objectType) {
        Annotation dataType = objectType.getAnnotation(this.annotations.getDataTypeAnnotationClass());
        if (dataType != null && AnnotationDataProvider.get(dataType, NAME) != null) {
            return AnnotationDataProvider.get(dataType, NAME);
        } else {
            return objectType.getName();
        }
    }

    private String createListDataTypeName(Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }

    @SuppressWarnings("unchecked")
    private int getRequiredPrefixLength(Class<?> objectType) {
        if (AnnotatedFieldsProvider
                .getSortedAnnotatedFields(objectType, this.annotations.getSubRecordAnnotationClass(),
                        this.annotations.getSubRecordListAnnotationClass()).size() > 0) {
            Annotation dataTypeAnnotation = objectType
                    .getAnnotation(this.annotations.getDataTypeAnnotationClass());
            String defaultPrefix = AnnotationDataProvider.get(dataTypeAnnotation, DEFAULT_PREFIX);
            if (defaultPrefix.length() == 0) {
                throw new AnnotationException("A prefix must be defined for object type " + objectType.getName());
            }
            return defaultPrefix.length();
        } else {
            return 0;
        }
    }

}
