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

package org.jsefa.flr.annotation;

import java.lang.reflect.Field;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.annotation.AnnotationException;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.util.GeneralConstants;
import org.jsefa.flr.mapping.FlrSimpleTypeMapping;
import org.jsefa.rbf.annotation.RbfAnnotations;
import org.jsefa.rbf.annotation.RbfTypeMappingFactory;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Factory for creating {@link TypeMapping}s from annotated classes.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrTypeMappingFactory extends RbfTypeMappingFactory {

    private static final RbfAnnotations ANNOTATIONS = new RbfAnnotations(FlrDataType.class, FlrField.class,
            FlrSubRecord.class, FlrSubRecordList.class);

    private char defaultPadCharacter;

    /**
     * Constructs a new <code>FlrTypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param objectAccessorProvider the object accessor provider to use
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param defaultPadCharacter the default pad character to be used
     */
    public FlrTypeMappingFactory(RbfTypeMappingRegistry typeMappingRegistry,
            SimpleTypeConverterProvider simpleTypeConverterProvider,
            ObjectAccessorProvider objectAccessorProvider, char defaultPadCharacter) {
        super(typeMappingRegistry, simpleTypeConverterProvider, objectAccessorProvider, ANNOTATIONS);
        this.defaultPadCharacter = defaultPadCharacter;
    }

    /**
     * {@inheritDoc}
     */
    protected TypeMapping<String> createSimpleTypeMapping(Class<?> objectType, String dataTypeName,
            SimpleTypeConverter converter, Field field) {
        FlrField fieldAnnotation = field.getAnnotation(FlrField.class);
        if (fieldAnnotation.length() <= 0) {
            throw new AnnotationException("Field length of field " + field.getName() + " of class "
                    + field.getDeclaringClass().getName() + " must be > 0");
        }
        char padCharacter = fieldAnnotation.padCharacter();
        if (padCharacter == GeneralConstants.NO_CHARACTER) {
            padCharacter = this.defaultPadCharacter;
        }
        return new FlrSimpleTypeMapping(objectType, dataTypeName, converter, fieldAnnotation.length(),
                padCharacter, fieldAnnotation.align());
    }

}
