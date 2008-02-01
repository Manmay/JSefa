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

import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.namespace.QName;

/**
 * A mapping between a java object type and a simple XML data type.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlSimpleTypeMapping extends SimpleTypeMapping<QName> {

    /**
     * Constructs a new <code>XmlSimpleTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param objectType the object type
     * @param simpleTypeConverter the simple type converter
     */
    public XmlSimpleTypeMapping(QName dataTypeName, Class<?> objectType, SimpleTypeConverter simpleTypeConverter) {
        super(objectType, dataTypeName, simpleTypeConverter);
    }

}
