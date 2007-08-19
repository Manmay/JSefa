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

package org.jsefa.xml.mapping.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsefa.xml.namespace.QName;

/**
 * Provider for default data type names.
 * <p>
 * It is thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlDataTypeDefaultNameRegistry {

    private final Map<Class<?>, QName> defaultDataTypeNames;

    /**
     * Constructs a new <code>XmlDataTypeDefaultNameRegistry</code>.
     */
    public XmlDataTypeDefaultNameRegistry() {
        this.defaultDataTypeNames = new ConcurrentHashMap<Class<?>, QName>();
        registerStandards();
    }

    /**
     * Registers the given data type name for the given object type.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     */
    public void register(Class<?> objectType, QName dataTypeName) {
        this.defaultDataTypeNames.put(objectType, dataTypeName);
    }

    /**
     * Merges the content of the other
     * <code>XmlDataTypeDefaultNameRegistry</code> into this one.
     * <p>
     * This overrides existing registrations of data type names for object types
     * which are known to both registries.
     * 
     * @param other the other data type default name registry
     */
    public void registerAll(XmlDataTypeDefaultNameRegistry other) {
        this.defaultDataTypeNames.putAll(other.defaultDataTypeNames);
    }

    /**
     * Returns the default data type name for the given object type.
     * 
     * @param objectType the object type
     * @return the default data type name
     */
    public QName get(Class<?> objectType) {
        return this.defaultDataTypeNames.get(objectType);
    }

    private void registerStandards() {
        register(String.class, XmlSchemaBuiltInDataTypeNames.STRING_DATA_TYPE_NAME);
        register(int.class, XmlSchemaBuiltInDataTypeNames.INT_DATA_TYPE_NAME);
        register(Integer.class, XmlSchemaBuiltInDataTypeNames.INTEGER_DATA_TYPE_NAME);
        register(long.class, XmlSchemaBuiltInDataTypeNames.LONG_DATA_TYPE_NAME);
        register(Long.class, XmlSchemaBuiltInDataTypeNames.LONG_DATA_TYPE_NAME);
        register(boolean.class, XmlSchemaBuiltInDataTypeNames.BOOLEAN_DATA_TYPE_NAME);
        register(Boolean.class, XmlSchemaBuiltInDataTypeNames.BOOLEAN_DATA_TYPE_NAME);
    }

}
