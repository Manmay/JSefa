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

package org.jsefa.csv.annotation;

import java.lang.reflect.Field;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.provider.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.util.GeneralConstants;
import org.jsefa.common.validator.provider.ValidatorProvider;
import org.jsefa.csv.lowlevel.config.QuoteMode;
import org.jsefa.csv.mapping.CsvSimpleTypeMapping;
import org.jsefa.rbf.annotation.RbfAnnotations;
import org.jsefa.rbf.annotation.RbfTypeMappingFactory;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Factory for creating CSV {@link TypeMapping}s from annotated classes.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class CsvTypeMappingFactory extends RbfTypeMappingFactory {

    private static final RbfAnnotations ANNOTATIONS = new RbfAnnotations(CsvDataType.class, CsvField.class,
            CsvSubRecord.class, CsvSubRecordList.class);

    private QuoteMode defaultQuoteMode;

    private String defaultNoValueString;

    /**
     * Constructs a new <code>CsvTypeMappingFactory</code>.
     * 
     * @param typeMappingRegistry the type mapping registry. New types will be registered using that registry.
     * @param simpleTypeConverterProvider the simple type converter provider to use
     * @param validatorProvider the validator provider to use
     * @param objectAccessorProvider the object accessor provider to use
     * @param defaultQuoteMode the default quote mode to use
     * @param defaultNoValueString the default no value string to use
     */
    public CsvTypeMappingFactory(RbfTypeMappingRegistry typeMappingRegistry,
            SimpleTypeConverterProvider simpleTypeConverterProvider,
            ValidatorProvider validatorProvider, ObjectAccessorProvider objectAccessorProvider,
            QuoteMode defaultQuoteMode, String defaultNoValueString) {
        super(typeMappingRegistry, simpleTypeConverterProvider, validatorProvider, objectAccessorProvider,
                ANNOTATIONS);
        this.defaultQuoteMode = defaultQuoteMode;
        this.defaultNoValueString = defaultNoValueString;
    }

    /**
     * {@inheritDoc}
     */
    protected TypeMapping<String> createSimpleTypeMapping(Class<?> objectType, String dataTypeName,
            SimpleTypeConverter converter, Field field) {
        CsvField fieldAnnotation = field.getAnnotation(CsvField.class);
        QuoteMode quoteMode = fieldAnnotation.quoteMode();
        if (quoteMode.equals(QuoteMode.DEFAULT)) {
            quoteMode = this.defaultQuoteMode;
        }
        String noValueString = fieldAnnotation.noValue();
        if (noValueString.equals(GeneralConstants.DEFAULT_STRING)) {
            noValueString = this.defaultNoValueString;
        }
        return new CsvSimpleTypeMapping(objectType, dataTypeName, converter, quoteMode, noValueString);
    }

}
