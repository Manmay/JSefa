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

package org.jsefa.xml.namespace;

/**
 * A collection of constants concerning namespaces.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface NamespaceConstants {

    /**
     * Namespace URI to use to represent that there is no namespace.
     */
    String NO_NAMESPACE_URI = "";

    /**
     * The prefix denoting the default namespace.
     */
    String DEFAULT_NAMESPACE_PREFIX = "";

    /**
     * The xml schema instance URI.
     */
    String XML_SCHEMA_INSTANCE_URI = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * The xml schema URI.
     */
    String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";
    
    /**
     * The URI of the xml namespace bound to the prefix xml.
     */
    String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";

    /**
     * The prefix xml.
     */
    String XML_NAMESPACE_PREFIX = "xml";

}
