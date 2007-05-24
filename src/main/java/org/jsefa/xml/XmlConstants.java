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

package org.jsefa.xml;

/**
 * A collection of constants concerning the XML format type.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlConstants {
    /**
     * Namespace URI to use to represent that there is no namespace.
     */
    String NO_NAMESPACE_URI = "";

    /**
     * The xml schema instance URI.
     */
    String XML_SCHEMA_INSTANCE_URI = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * The xml schema URI.
     */
    String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * The name of the attribute used to denote the data type as used in xml
     * schema instances.
     */
    QName XML_SCHEMA_DATA_TYPE_ATTRIBUTE_NAME = QName.create(XML_SCHEMA_INSTANCE_URI, "type");

    /**
     * The URI of automatically created data type names.
     */
    String DEFAULT_DATA_TYPE_NAMES_URI = "http://www.jsefa.org/xml/types/defaults/object-types";
    
    /**
     * The default line break used if none is explicitly given.
     */
    String DEFAULT_LINE_BREAK = "\n";
    
    /**
     * The default line indentation used if none is explicitly given.
     */
    String DEFAULT_LINE_INDENTATION = "  ";

}
