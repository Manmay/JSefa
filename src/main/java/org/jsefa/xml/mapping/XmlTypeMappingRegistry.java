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

package org.jsefa.xml.mapping;

import static org.jsefa.xml.mapping.support.XmlSchemaBuiltInDataTypeNames.BOOLEAN_DATA_TYPE_NAME;
import static org.jsefa.xml.mapping.support.XmlSchemaBuiltInDataTypeNames.INTEGER_DATA_TYPE_NAME;
import static org.jsefa.xml.mapping.support.XmlSchemaBuiltInDataTypeNames.INT_DATA_TYPE_NAME;
import static org.jsefa.xml.mapping.support.XmlSchemaBuiltInDataTypeNames.LONG_DATA_TYPE_NAME;
import static org.jsefa.xml.mapping.support.XmlSchemaBuiltInDataTypeNames.STRING_DATA_TYPE_NAME;

import org.jsefa.common.converter.BooleanConverter;
import org.jsefa.common.converter.IntegerConverter;
import org.jsefa.common.converter.LongConverter;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.SimpleTypeConverterConfiguration;
import org.jsefa.common.converter.StringConverter;
import org.jsefa.common.mapping.HierarchicalTypeMappingRegistry;
import org.jsefa.xml.namespace.QName;

/**
 * A registry for xml type mappings with standard type mappings already registered.
 * <p>
 * Instances of this class are thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlTypeMappingRegistry extends HierarchicalTypeMappingRegistry<QName> {

    /**
     * Constructs a new <code>XmlTypeMappingRegistry</code> with standard type mappings already registered.
     */
    public XmlTypeMappingRegistry() {
        registerStandards();
    }

    /**
     * Constructs a new <code>XmlTypeMappingRegistry</code> from the given one as a model so that the other one
     * and its components can be modified without affecting this configuration object.
     * 
     * @param other the registry that serves as a model for creating a new one
     */
    private XmlTypeMappingRegistry(XmlTypeMappingRegistry other) {
        super(other);
    }

    private void registerStandards() {
        registerStandard(STRING_DATA_TYPE_NAME, String.class, StringConverter.create());
        registerStandard(INT_DATA_TYPE_NAME, Integer.class, IntegerConverter.create());
        registerStandard(INTEGER_DATA_TYPE_NAME, Integer.class, IntegerConverter.create());
        registerStandard(LONG_DATA_TYPE_NAME, Long.class, LongConverter.create());
        registerStandard(BOOLEAN_DATA_TYPE_NAME, Boolean.class, BooleanConverter
                .create(SimpleTypeConverterConfiguration.EMPTY));
    }

    private void registerStandard(QName dataTypeName, Class<?> objectType, SimpleTypeConverter converter) {
        register(new XmlSimpleTypeMapping(dataTypeName, objectType, converter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlTypeMappingRegistry createCopy() {
        return new XmlTypeMappingRegistry(this);
    }

}
