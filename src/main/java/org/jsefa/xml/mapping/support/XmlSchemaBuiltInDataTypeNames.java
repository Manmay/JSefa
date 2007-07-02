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

import org.jsefa.xml.namespace.NamespaceConstants;
import org.jsefa.xml.namespace.QName;

/**
 * A collection of constants for the supported build-int data type names.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlSchemaBuiltInDataTypeNames {

    /**
     * The name of the XML Schema built-in data type <code>string</code>.
     */
    QName STRING_DATA_TYPE_NAME = QName.create(NamespaceConstants.XML_SCHEMA_URI, "string");

    /**
     * The name of the XML Schema built-in data type <code>int</code>.
     */
    QName INT_DATA_TYPE_NAME = QName.create(NamespaceConstants.XML_SCHEMA_URI, "int");

    /**
     * The name of the XML Schema built-in data type <code>integer</code>.
     */
    QName INTEGER_DATA_TYPE_NAME = QName.create(NamespaceConstants.XML_SCHEMA_URI, "integer");

    /**
     * The name of the XML Schema built-in data type <code>long</code>.
     */
    QName LONG_DATA_TYPE_NAME = QName.create(NamespaceConstants.XML_SCHEMA_URI, "long");

    /**
     * The name of the XML Schema built-in data type <code>boolean</code>.
     */
    QName BOOLEAN_DATA_TYPE_NAME = QName.create(NamespaceConstants.XML_SCHEMA_URI, "boolean");

}
