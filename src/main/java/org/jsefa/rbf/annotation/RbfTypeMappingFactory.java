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

import static org.jsefa.common.annotation.AnnotationParameterNames.DATA_TYPE_NAME;
import static org.jsefa.common.annotation.AnnotationParameterNames.NAME;
import static org.jsefa.rbf.annotation.RbfAnnotationDataNames.DEFAULT_PREFIX;
import static org.jsefa.rbf.annotation.RbfAnnotationDataNames.PREFIX;
import static org.jsefa.rbf.annotation.RbfAnnotationDataNames.RECORDS;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.jsefa.common.validator.Validator;
import org.jsefa.common.validator.provider.ValidatorProvider;
import org.jsefa.rbf.mapping.FieldMapping;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfFieldDescriptor;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfNodeMapping;
import org.jsefa.rbf.mapping.RbfNodeType;
import org.jsefa.rbf.mapping.RecordDescriptor;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;
import org.jsefa.rbf.mapping.RecordMapping;

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
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param validatorProvider the validator provider to use
     * @param objectAccessorProvider the object accessor provider to use
     * @param annotations the parameter objects providing the annotation classes to use.
     */
    public RbfTypeMappingFactory(RbfTypeMappingRegistry typeMappingRegistry,
            SimpleTypeConverterProvider simpleTypeConverterProvider, ValidatorProvider validatorProvider,
            ObjectAccessorProvider objectAccessorProvider, RbfAnnotations annotations) {
        super(typeMappingRegistry, simpleTypeConverterProvider, validatorProvider, objectAccessorProvider);
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
        String dataTypeName = createComplexTypeMappingIfAbsent(objectType, true);
        assertIsCycleFree(dataTypeName);
        return dataTypeName;
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

    /**
     * {@inheritDoc}
     */
    protected String getAnnotatedDataTypeName(Annotation annotation, Class<?> annotationContextClass) {
        return AnnotationDataProvider.get(annotation, DATA_TYPE_NAME);
    }

    private String createSimpleTypeMappingIfAbsent(Class<?> objectType, Field field, Annotation fieldAnnotation) {
        String dataTypeName = createSimpleDataTypeName(field);
        if (prepareToCreate(objectType, dataTypeName)) {
            SimpleTypeConverter converter = createSimpleTypeConverter(objectType, field, fieldAnnotation);
            getTypeMappingRegistry().register(createSimpleTypeMapping(objectType, dataTypeName, converter, field));
        }
        return dataTypeName;
    }

    @SuppressWarnings("unchecked")
    private String createComplexTypeMappingIfAbsent(Class<?> objectType, boolean subRecordsAllowed) {
        String dataTypeName = createComplexDataTypeName(objectType);
        if (prepareToCreate(objectType, dataTypeName)) {
            Collection<RbfNodeMapping<?>> nodeMappings = new ArrayList<RbfNodeMapping<?>>();
            nodeMappings.addAll(createFieldMappings(objectType));
            if (subRecordsAllowed) {
                nodeMappings.addAll(createRecordMappings(objectType));
            } else {
                assertNoSubRecordsDeclared(objectType);
            }
            Validator validator = getValidatorFactory().createValidator(objectType,
                    this.annotations.getFieldAnnotationClass(), this.annotations.getSubRecordAnnotationClass(),
                    this.annotations.getSubRecordListAnnotationClass());
            RbfComplexTypeMapping complexTypeMapping = new RbfComplexTypeMapping(objectType, dataTypeName,
                    getObjectAccessorProvider().get(objectType), nodeMappings, validator);
            getTypeMappingRegistry().register(complexTypeMapping);
        }
        return dataTypeName;
    }

    @SuppressWarnings("unchecked")
    private Collection<FieldMapping> createFieldMappings(Class<?> objectType) {
        Collection<FieldMapping> fieldMappings = new ArrayList<FieldMapping>();
        int relativeFieldIndex = 0;
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
                Class<?> normalizedFieldObjectType = getTypeMappingRegistry().get(fieldDataTypeName)
                        .getObjectType();
                Validator validator = getValidatorFactory().createContextualValidator(normalizedFieldObjectType,
                        field, fieldAnnotation, this.annotations.getDataTypeAnnotationClass());
                fieldMappings.add(new FieldMapping(fieldDataTypeName,
                        new RbfFieldDescriptor(relativeFieldIndex++), normalizedFieldObjectType,
                        new FieldDescriptor(field.getName(), normalizedFieldObjectType), validator));
            } else if (hasComplexType(field.getType())) {
                if (fieldDataTypeName == null) {
                    fieldDataTypeName = createComplexTypeMappingIfAbsent(field.getType(), false);
                } else {
                    assertTypeMappingExists(fieldDataTypeName);
                }
                Class<?> normalizedFieldObjectType = getTypeMappingRegistry().get(fieldDataTypeName)
                        .getObjectType();
                Validator validator = getValidatorFactory().createContextualValidator(normalizedFieldObjectType,
                        field, fieldAnnotation, this.annotations.getDataTypeAnnotationClass());
                fieldMappings.add(new FieldMapping(fieldDataTypeName,
                        new RbfFieldDescriptor(relativeFieldIndex++), normalizedFieldObjectType,
                        new FieldDescriptor(field.getName(), normalizedFieldObjectType), validator));
            } else {
                throw new TypeMappingException("Can not create a type mapping for field " + field.getName()
                        + " of class " + objectType.getName());
            }
        }
        return fieldMappings;
    }

    @SuppressWarnings("unchecked")
    private Collection<RecordMapping> createRecordMappings(Class<?> objectType) {
        Collection<RecordMapping> recordMappings = new ArrayList<RecordMapping>();
        int requiredPrefixLength = getRequiredPrefixLength(objectType);
        for (Field field : AnnotatedFieldsProvider.getSortedAnnotatedFields(objectType, this.annotations
                .getSubRecordAnnotationClass(), this.annotations.getSubRecordListAnnotationClass())) {
            if (hasCollectionType(field.getType())
                    && field.getAnnotation(this.annotations.getSubRecordListAnnotationClass()) != null) {
                String listDataTypeName = createListTypeMappingIfAbsent(field, requiredPrefixLength);
                Annotation fieldAnnotation = field.getAnnotation(this.annotations
                        .getSubRecordListAnnotationClass());

                Record[] records = getRecords(fieldAnnotation);
                for (Record record : records) {
                    String listItemDataTypeName = createIfAbsent(field, record, records);
                    String prefix = record.prefix();
                    assertPrefixHasRequiredLength(field, prefix, requiredPrefixLength);
                    Class<?> normalizedListItemObjectType = getTypeMappingRegistry().get(listItemDataTypeName)
                            .getObjectType();
                    Validator validator = getValidatorFactory().createContextualValidator(
                            normalizedListItemObjectType, field, record,
                            this.annotations.getDataTypeAnnotationClass());
                    recordMappings.add(new RecordMapping(listDataTypeName, new RecordDescriptor(prefix),
                            normalizedListItemObjectType, new FieldDescriptor(field.getName(), Collection.class),
                            true, validator));
                }

                Validator validator = getValidatorFactory().createContextualValidator(Collection.class, field,
                        fieldAnnotation, this.annotations.getDataTypeAnnotationClass());
                recordMappings
                        .add(new RecordMapping(listDataTypeName, new RecordDescriptor(null), Collection.class,
                                new FieldDescriptor(field.getName(), Collection.class), false, validator));
            } else if (hasComplexType(field.getType())) {
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
                Class<?> normalizedFieldObjectType = getTypeMappingRegistry().get(fieldDataTypeName)
                        .getObjectType();
                Annotation fieldAnnotation = field.getAnnotation(this.annotations.getSubRecordAnnotationClass());
                Validator validator = getValidatorFactory().createContextualValidator(normalizedFieldObjectType,
                        field, fieldAnnotation, this.annotations.getDataTypeAnnotationClass());

                recordMappings.add(new RecordMapping(fieldDataTypeName, new RecordDescriptor(prefix),
                        normalizedFieldObjectType,
                        new FieldDescriptor(field.getName(), normalizedFieldObjectType), false, validator));
            }
        }
        return recordMappings;
    }

    @SuppressWarnings("unchecked")
    private void assertNoSubRecordsDeclared(Class<?> objectType) {
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
        if (prepareToCreate(Collection.class, dataTypeName)) {
            if (subRecordListAnnotation == null || getRecords(subRecordListAnnotation).length == 0) {
                throw new AnnotationException("No FlrSubRecordList annotation with proper content found");
            }
            Collection<RecordMapping> recordMappings = new ArrayList<RecordMapping>();
            Record[] records = getRecords(subRecordListAnnotation);
            for (Record record : records) {
                String listItemDataTypeName = createIfAbsent(field, record, records);
                String prefix = record.prefix();
                assertPrefixHasRequiredLength(field, prefix, requiredPrefixLength);
                Class<?> normalizedListItemObjectType = getTypeMappingRegistry().get(listItemDataTypeName)
                        .getObjectType();
                Validator validator = getValidatorFactory()
                        .createContextualValidator(normalizedListItemObjectType, field, record,
                                this.annotations.getDataTypeAnnotationClass());

                recordMappings.add(new RecordMapping(listItemDataTypeName, new RecordDescriptor(prefix),
                        normalizedListItemObjectType, new FieldDescriptor(field.getName(), Collection.class),
                        false, validator));
            }
            getTypeMappingRegistry().register(
                    new RbfListTypeMapping(dataTypeName, recordMappings, getObjectAccessorProvider().get(
                            field.getType())));
        }
        return dataTypeName;
    }

    private void assertPrefixHasRequiredLength(Field field, String prefix, int requiredPrefixLength) {
        if (prefix.length() != requiredPrefixLength) {
            throw new AnnotationException("All record annotations of the list field " + field.getName()
                    + " of class " + field.getDeclaringClass().getName() + " must have a prefix with length "
                    + requiredPrefixLength);
        }
    }

    private String createIfAbsent(Field field, Record record, Record[] records) {
        String listItemDataTypeName = AnnotationDataProvider.get(record, DATA_TYPE_NAME);
        if (listItemDataTypeName == null) {
            Class<?> listItemObjectType = getCollectionItemType(record, field, records.length == 1);
            assertHasComplexType(listItemObjectType, field);
            listItemDataTypeName = createComplexTypeMappingIfAbsent(listItemObjectType, true);
        } else {
            assertTypeMappingExists(listItemDataTypeName);
        }
        return listItemDataTypeName;
    }

    private void assertHasComplexType(Class<?> listItemObjectType, Field field) {
        if (listItemObjectType == null) {
            throw new AnnotationException("Neither dataTypeName nor objectType is given for list item of field: "
                    + field.getName() + " of class " + field.getDeclaringClass().getName());
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

    private void assertIsCycleFree(String dataTypeName) {
        assertIsCycleFree(dataTypeName, new ArrayList<Class<?>>());
    }

    private void assertIsCycleFree(String dataTypeName, List<Class<?>> objectTypePath) {
        TypeMapping<String> typeMapping = getTypeMappingRegistry().get(dataTypeName);
        for (Class<?> objectTypeOnPath : objectTypePath) {
            if (objectTypeOnPath.isAssignableFrom(typeMapping.getObjectType())
                    || typeMapping.getObjectType().isAssignableFrom(objectTypeOnPath)) {
                objectTypePath.add(typeMapping.getObjectType());
                throw new TypeMappingException("Cycle in type graph detected. Path: " + objectTypePath);
            }
        }
        objectTypePath.add(typeMapping.getObjectType());
        if (typeMapping instanceof RbfComplexTypeMapping) {
            RbfComplexTypeMapping complexTypeMapping = (RbfComplexTypeMapping) typeMapping;
            for (String fieldName : complexTypeMapping.getFieldNames(RbfNodeType.FIELD)) {
                RbfNodeMapping<?> nodeMapping = complexTypeMapping.getNodeMapping(fieldName, Object.class);
                assertIsCycleFree(nodeMapping.getDataTypeName(), objectTypePath);
            }
            for (String fieldName : complexTypeMapping.getFieldNames(RbfNodeType.RECORD)) {
                RbfNodeMapping<?> nodeMapping = complexTypeMapping.getNodeMapping(fieldName, Object.class);
                assertIsCycleFree(nodeMapping.getDataTypeName());
            }
        }
        objectTypePath.remove(typeMapping.getObjectType());
    }
}
