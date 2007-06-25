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

import org.jsefa.ConfigurationException;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.flr.FlrConfiguration;
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

    /**
     * Constructs a new <code>FlrTypeMappingFactory</code>.
     * 
     * @param config the configuration object
     * @param typeMappingRegistry the type mapping registry. New types will be
     *            registered using that registry.
     */
    public FlrTypeMappingFactory(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry) {
        super(config, typeMappingRegistry, ANNOTATIONS);
    }

    /**
     * {@inheritDoc}
     */
    protected TypeMapping<String> createSimpleTypeMapping(Class objectType, String dataTypeName,
            SimpleTypeConverter converter, Field field) {
        FlrField fieldAnnotation = field.getAnnotation(FlrField.class);
        if (fieldAnnotation.length() <= 0) {
            throw new ConfigurationException("Field length of field " + field.getName() + " of class "
                    + field.getDeclaringClass().getName() + " must be > 0");
        }
        return new FlrSimpleTypeMapping(objectType, dataTypeName, converter, fieldAnnotation.length(),
                fieldAnnotation.padCharacter(), fieldAnnotation.align());
    }

}
