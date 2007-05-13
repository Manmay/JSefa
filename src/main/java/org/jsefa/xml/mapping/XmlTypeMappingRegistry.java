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

import org.jsefa.common.converter.SimpleTypeConverterProvider;
import org.jsefa.common.mapping.HierarchicalTypeMappingRegistry;
import org.jsefa.xml.QName;
import static org.jsefa.xml.XmlSchemaBuiltInDataTypeNames.*;

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
     * Constructs a new <code>XmlTypeMappingRegistry</code> with standard type
     * mappings already registered using the given
     * <code>SimpleTypeConverterProvider</code>.
     * 
     * @param simpleTypeConverterProvider the simple type converter provider to
     *            use for creating the standard type mappings
     */
    public XmlTypeMappingRegistry(SimpleTypeConverterProvider simpleTypeConverterProvider) {
        registerStandards(simpleTypeConverterProvider);
    }

    /**
     * Constructs a new <code>XmlTypeMappingRegistry</code> from the given one
     * as a model so that the other one and its components can be modified
     * without affecting this configuration object.
     * 
     * @param other the registry that serves as a model for creating a new one
     */
    public XmlTypeMappingRegistry(XmlTypeMappingRegistry other) {
        super(other);
    }

    private void registerStandards(SimpleTypeConverterProvider converterProvider) {
        registerStandard(STRING_DATA_TYPE_NAME, String.class, converterProvider);
        registerStandard(INT_DATA_TYPE_NAME, int.class, converterProvider);
        registerStandard(INTEGER_DATA_TYPE_NAME, Integer.class, converterProvider);
        registerStandard(LONG_DATA_TYPE_NAME, long.class, converterProvider);
        registerStandard(BOOLEAN_DATA_TYPE_NAME, boolean.class, converterProvider);
        registerStandard(BOOLEAN_DATA_TYPE_NAME, Boolean.class, converterProvider);
    }

    private void registerStandard(QName dataTypeName, Class objectType,
            SimpleTypeConverterProvider converterProvider) {
        register(new XmlSimpleTypeMapping(dataTypeName, objectType, converterProvider.getForValueType(objectType,
                (String[]) null)));
    }

}
