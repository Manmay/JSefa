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

import org.jsefa.common.mapping.NodeType;

/**
 * Enum class declaring the different xml node types.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public enum XmlNodeType implements NodeType {
    /**
     * node type for xml elements.
     */
    ELEMENT,
    /**
     * node type for xml attributes.
     */
    ATTRIBUTE,
    /**
     * node type for the content of non-simple xml elements without child elements.
     */
    TEXT_CONTENT
}
