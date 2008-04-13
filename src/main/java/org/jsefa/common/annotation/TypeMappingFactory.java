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

import static org.jsefa.common.annotation.AnnotationDataNames.CONVERTER_TYPE;
import static org.jsefa.common.annotation.AnnotationDataNames.FORMAT;
import static org.jsefa.common.annotation.AnnotationDataNames.LIST_ITEM;
import static org.jsefa.common.annotation.AnnotationDataNames.OBJECT_TYPE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.mapping.TypeMappingException;
import org.jsefa.common.mapping.TypeMappingRegistry;
import org.jsefa.common.util.ReflectionUtil;

/**
 * Abstract super class for factories which can create {@link TypeMapping}s from annotated classes.
 * <p>
 * All subclasses should be thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <D> the type of the data type name
 * @param <R> the type of the type mapping registry
 * 
 */
public abstract class TypeMappingFactory<D, R extends TypeMappingRegistry<D>> {

    private final SimpleTypeConverterProvider simpleTypeConverterProvider;

    private final ObjectAccessorProvider objectAccessorProvider;

    private final R typeMappingRegistry;

    /**
     * Constructs a new <code>TypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param objectAccessorProvider the object accessor provider to use
     */
    public TypeMappingFactory(R typeMappingRegistry, SimpleTypeConverterProvider simpleTypeConverterProvider,
            ObjectAccessorProvider objectAccessorProvider) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.simpleTypeConverterProvider = simpleTypeConverterProvider;
        this.objectAccessorProvider = objectAccessorProvider;
    }

    /**
     * Creates a type mapping for the given object type, registers it with the type mapping registry and returns
     * its data type name. The first two steps are omitted if a type mapping is already registered for the given
     * object type.
     * 
     * @param objectType the object type to create a type mapping for.
     * @return the name of the created or found data type.
     */
    public abstract D createIfAbsent(Class<?> objectType);

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
    protected final boolean prepareToCreate(Class<?> objectType, D dataTypeName) {
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
     * Creates a simple type converter.
     * 
     * @param objectType the object type to create a converter for
     * @param field the field to create a converter for. May be null.
     * @param annotation the annotation providing parameters for constructing the converter. May be null.
     * @return a simple type converter
     */
    @SuppressWarnings("unchecked")
    protected SimpleTypeConverter createSimpleTypeConverter(Class<?> objectType, Field field, Annotation annotation) {
        String[] format = null;
        SimpleTypeConverter itemTypeConverter = null;
        if (annotation != null) {
            format = AnnotationDataProvider.get(annotation, FORMAT);
            if (hasListType(objectType)) {
                Annotation itemAnnotation = AnnotationDataProvider.get(annotation, LIST_ITEM);
                D itemDataTypeName = getAnnotatedDataTypeName(itemAnnotation, field.getDeclaringClass());
                if (itemDataTypeName != null) {
                    assertTypeMappingIsSimple(itemDataTypeName);
                    assertNoNestedList(getTypeMappingRegistry().get(itemDataTypeName).getObjectType());
                    itemTypeConverter = ((SimpleTypeMapping) getTypeMappingRegistry().get(itemDataTypeName))
                            .getSimpleTypeConverter();
                } else {
                    Class<?> itemObjectType = getListItemObjectType(itemAnnotation, field, true);
                    assertNoNestedList(itemObjectType);
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
     * Returns the type of the items of a list.
     * 
     * @param annotation the annotation of the field
     * @param field the field
     * @param fromListDeclarationAllowed true, if the field is a list which contains only items of one type so
     *                that the type may be deduced from the generic parameter argument of the field; false otherwise.
     * @return an object type
     */
    protected Class<?> getListItemObjectType(Annotation annotation, Field field, boolean fromListDeclarationAllowed) {
        Class<?> objectType = AnnotationDataProvider.get(annotation, OBJECT_TYPE);
        if (objectType == null && fromListDeclarationAllowed) {
            objectType = ReflectionUtil.getListEntryObjectType(field);
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
    protected abstract D getAnnotatedDataTypeName(Annotation annotation, Class<?> annotationContextClass);

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
     * Returns true if and only if the given object type is a list type.
     * 
     * @param objectType the object type
     * @return true, if the given object type is a list type; false otherwise.
     */
    protected final boolean hasListType(Class<?> objectType) {
        return List.class.isAssignableFrom(objectType);
    }

    /**
     * Asserts that a type mapping exists for the given data type name.
     * 
     * @param dataTypeName the data type name.
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertTypeMappingExists(D dataTypeName) {
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
    protected final void assertTypeMappingIsSimple(D dataTypeName) {
        assertTypeMappingExists(dataTypeName);
        if (!(getTypeMappingRegistry().get(dataTypeName) instanceof SimpleTypeMapping)) {
            throw new AnnotationException("The dataTypeName " + dataTypeName
                    + " does not denote a simple type mapping");
        }
    }

    /**
     * Asserts that a list item is not itself a list.
     * 
     * @param listItemObjectType the object type of the list item.
     * @throws AnnotationException if the assertion fails.
     */
    protected final void assertNoNestedList(Class<?> listItemObjectType) {
        if (hasListType(listItemObjectType)) {
            throw new AnnotationException("No lists inside lists allowed!");
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
    protected final class TypeMappingPlaceholder extends TypeMapping<D> {

        /**
         * Constructs a new <code>TypeMappingPlaceholder</code>.
         * 
         * @param objectType the object type
         * @param dataTypeName the data type name
         */
        protected TypeMappingPlaceholder(Class<?> objectType, D dataTypeName) {
            super(objectType, dataTypeName);
        }

    }

}
