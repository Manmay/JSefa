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

package org.jsefa.common.annotation;

import static org.jsefa.common.annotation.AnnotationParameterNames.CONVERTER_TYPE;
import static org.jsefa.common.annotation.AnnotationParameterNames.FORMAT;
import static org.jsefa.common.annotation.AnnotationParameterNames.LIST_ITEM;
import static org.jsefa.common.annotation.AnnotationParameterNames.OBJECT_TYPE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingException;
import org.jsefa.common.mapping.TypeMappingRegistry;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.provider.ValidatorProvider;

/**
 * Abstract super class for factories which can create {@link TypeMapping}s from annotated classes.
 * <p>
 * All subclasses should be thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <N> the type of the data type name
 * @param <R> the type of the type mapping registry
 * 
 */
public abstract class TypeMappingFactory<N, R extends TypeMappingRegistry<N>> {

    private final SimpleTypeConverterProvider simpleTypeConverterProvider;

    private final ValidatorProvider validatorProvider;

    private final ObjectAccessorProvider objectAccessorProvider;

    private final R typeMappingRegistry;

    private final ValidatorFactory validatorFactory;

    /**
     * Constructs a new <code>TypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param validatorProvider the validator provider to use
     * @param objectAccessorProvider the object accessor provider to use
     */
    public TypeMappingFactory(R typeMappingRegistry, SimpleTypeConverterProvider simpleTypeConverterProvider,
            ValidatorProvider validatorProvider, ObjectAccessorProvider objectAccessorProvider) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.simpleTypeConverterProvider = simpleTypeConverterProvider;
        this.validatorProvider = validatorProvider;
        this.objectAccessorProvider = objectAccessorProvider;
        this.validatorFactory = new ValidatorFactory(validatorProvider, objectAccessorProvider);
    }

    /**
     * Creates a type mapping for the given object type, registers it with the type mapping registry and returns
     * its data type name. The first two steps are omitted if a type mapping is already registered for the given
     * object type.
     * 
     * @param objectType the object type to create a type mapping for.
     * @return the name of the created or found data type.
     */
    public abstract N createIfAbsent(Class<?> objectType);

    /**
     * Returns the type mapping registry.
     * 
     * @return the type mapping registry.
     */
    public final R getTypeMappingRegistry() {
        return typeMappingRegistry;
    }

    /**
     * Called before creating a new type mapping with the given data type name for the given object type. Returns
     * true, if the type mapping registry has no entry for the data type name and if it is the first time this
     * method is called with the given argument; otherwise false.
     * <p>
     * The purpose of this method is to prevent from creating duplicates and from falling in an endless loop in
     * case of a cycle in the type mapping graph.
     * <p>
     * In case the method returns true, a {@link TypeMappingPlaceholder} is registered so that object type
     * information can be retrieved during the construction of a type mapping. That type mapping is replaced by the
     * real type mapping after finishing its construction.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * 
     * @return true, if no type mapping with the given name already exists or is already under construction.
     */
    protected final boolean prepareToCreate(Class<?> objectType, N dataTypeName) {
        if (this.typeMappingRegistry.get(dataTypeName) != null) {
            return false;
        }
        this.typeMappingRegistry.register(new TypeMappingPlaceholder(objectType, dataTypeName));
        return true;
    }

    /**
     * Returns the object accessor provider.
     * 
     * @return the object accessor provider.
     */
    protected final ObjectAccessorProvider getObjectAccessorProvider() {
        return objectAccessorProvider;
    }

    /**
     * Returns the simple type converter provider.
     * 
     * @return the simple type converter provider.
     */
    protected final SimpleTypeConverterProvider getSimpleTypeConverterProvider() {
        return simpleTypeConverterProvider;
    }

    /**
     * Returns the validator provider.
     * 
     * @return the validator provider
     */
    protected final ValidatorProvider getValidatorProvider() {
        return validatorProvider;
    }

    /**
     * Creates a simple type converter.
     * 
     * @param objectType the object type to create a converter for
     * @param field the field to create a converter for. May be null.
     * @param annotation the annotation providing parameters for constructing the converter. May be null.
     * @return a simple type converter
     */
    @SuppressWarnings("unchecked")
    protected final SimpleTypeConverter createSimpleTypeConverter(Class<?> objectType, Field field,
            Annotation annotation) {
        String[] format = null;
        SimpleTypeConverter itemTypeConverter = null;
        if (annotation != null) {
            format = AnnotationDataProvider.get(annotation, FORMAT);
            if (hasCollectionType(objectType)) {
                Annotation itemAnnotation = AnnotationDataProvider.get(annotation, LIST_ITEM);
                N itemDataTypeName = getAnnotatedDataTypeName(itemAnnotation, field.getDeclaringClass());
                if (itemDataTypeName != null) {
                    assertTypeMappingIsSimple(itemDataTypeName);
                    assertNoCollectionType(getTypeMappingRegistry().get(itemDataTypeName).getObjectType());
                    itemTypeConverter = ((SimpleTypeMapping) getTypeMappingRegistry().get(itemDataTypeName))
                            .getSimpleTypeConverter();
                } else {
                    Class<?> itemObjectType = getCollectionItemType(itemAnnotation, field, true);
                    assertHasSimpleType(itemObjectType);
                    assertNoCollectionType(itemObjectType);
                    itemTypeConverter = createSimpleTypeConverter(itemObjectType, null, itemAnnotation);
                }
            }
            if (AnnotationDataProvider.get(annotation, CONVERTER_TYPE) != null) {
                Class<? extends SimpleTypeConverter> converterType = AnnotationDataProvider.get(annotation,
                        CONVERTER_TYPE);
                return getSimpleTypeConverterProvider().getForConverterType(converterType, objectType, format,
                        itemTypeConverter);
            }
        }
        if (getSimpleTypeConverterProvider().hasConverterFor(objectType)) {
            return getSimpleTypeConverterProvider().getForObjectType(objectType, format, itemTypeConverter);
        }
        throw new TypeMappingException("Could not create a simple type converter for " + objectType);
    }

    /**
     * Returns the type of the items of a collection.
     * 
     * @param annotation the annotation of the field
     * @param field the field
     * @param fromFieldDeclarationAllowed true, if the field is a collection which contains only values of one type so
     *        that the type may be deduced from the actual parameter type of the parameterized type of the field; false
     *        otherwise.
     * @return an object type
     */
    protected final Class<?> getCollectionItemType(Annotation annotation, Field field,
            boolean fromFieldDeclarationAllowed) {
        Class<?> objectType = AnnotationDataProvider.get(annotation, OBJECT_TYPE);
        if (objectType == null && fromFieldDeclarationAllowed) {
            objectType = ReflectionUtil.getActualTypeParameter(field, 0);
        }
        return objectType;
    }
    
    /**
     * Returns the type of the values of a map.
     * 
     * @param annotation the annotation of the field
     * @param field the field
     * @param fromFieldDeclarationAllowed true, if the field is a map which contains only values of one type so
     *        that the type may be deduced from the actual parameter type of the parameterized type of the field; false
     *        otherwise.
     * @return an object type
     */
    protected final Class<?> getMapValueType(Annotation annotation, Field field, boolean fromFieldDeclarationAllowed) {
        Class<?> objectType = AnnotationDataProvider.get(annotation, OBJECT_TYPE);
        if (objectType == null && fromFieldDeclarationAllowed) {
            objectType = ReflectionUtil.getActualTypeParameter(field, 1);
        }
        return objectType;
    }

    /**
     * Returns the type of the keys of a map.
     * 
     * @param annotation the annotation of the field
     * @param field the field
     * @return an object type
     */
    protected final Class<?> getMapKeyType(Annotation annotation, Field field) {
        Class<?> objectType = AnnotationDataProvider.get(annotation, OBJECT_TYPE);
        if (objectType == null) {
            objectType = ReflectionUtil.getActualTypeParameter(field, 0);
        }
        return objectType;
    }

    /**
     * Returns the data type name as declared through the given annotation.
     * 
     * @param annotation the annotation
     * @param annotationContextClass the context class of the annotation. This class may be needed to interpret the
     *                annotation data.
     * @return a data type name
     */
    protected abstract N getAnnotatedDataTypeName(Annotation annotation, Class<?> annotationContextClass);

    /**
     * Returns the validator factory.
     * 
     * @return the validator factory
     */
    protected final ValidatorFactory getValidatorFactory() {
        return this.validatorFactory;
    }

    /**
     * Returns true if and only if the given object type is a simple type.
     * 
     * @param objectType the object type
     * @return true, if the object type is a simple type; false otherwise.
     */
    protected final boolean hasSimpleType(Class<?> objectType) {
        return getSimpleTypeConverterProvider().hasConverterFor(objectType);
    }

    /**
     * Returns true if and only if the given object type is a collection type.
     * 
     * @param objectType the object type
     * @return true, if the given object type is a collection type; false otherwise.
     */
    protected final boolean hasCollectionType(Class<?> objectType) {
        return Collection.class.isAssignableFrom(objectType);
    }
    
    /**
     * Returns true if and only if the given object type is a map type.
     * 
     * @param objectType the object type
     * @return true, if the given object type is a map type; false otherwise.
     */
    protected final boolean hasMapType(Class<?> objectType) {
        return Map.class.isAssignableFrom(objectType);
    }
    

    /**
     * Asserts that a type mapping exists for the given data type name.
     * 
     * @param dataTypeName the data type name.
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertTypeMappingExists(N dataTypeName) {
        if (getTypeMappingRegistry().get(dataTypeName) == null) {
            throw new AnnotationException("No type mapping registered for data type name " + dataTypeName);
        }
    }

    /**
     * Asserts that a type mapping exists for the given data type name and is simple.
     * 
     * @param dataTypeName the data type name.
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertTypeMappingIsSimple(N dataTypeName) {
        assertTypeMappingExists(dataTypeName);
        if (!(getTypeMappingRegistry().get(dataTypeName) instanceof SimpleTypeMapping)) {
            throw new AnnotationException("The dataTypeName " + dataTypeName
                    + " does not denote a simple type mapping");
        }
    }

    /**
     * Asserts that a given object type is not a collection class.
     * 
     * @param objectType the object type to check
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertNoCollectionType(Class<?> objectType) {
        if (hasCollectionType(objectType)) {
            throw new AnnotationException("No collections allowed here!");
        }
    }
    
    /**
     * Asserts that a given object type is a simple type.
     * 
     * @param objectType the object type to check
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertHasSimpleType(Class<?> objectType) {
        if (!hasSimpleType(objectType)) {
            throw new AnnotationException("Only simple types are allowed here!");
        }
    }

    /**
     * A placeholder for a type mapping used during the construction of a type mapping, i. e. between calling
     * {@link TypeMappingFactory#prepareToCreate(Class, Object)} and the registration of the type mapping.
     * <p>
     * This is used to obtain the object type for a given data type name during the construction phase.
     * 
     * @author Norman Lahme-Huetig
     * 
     */
    protected final class TypeMappingPlaceholder extends TypeMapping<N> {

        /**
         * Constructs a new <code>TypeMappingPlaceholder</code>.
         * 
         * @param objectType the object type
         * @param dataTypeName the data type name
         */
        protected TypeMappingPlaceholder(Class<?> objectType, N dataTypeName) {
            super(objectType, dataTypeName);
        }

    }

}
