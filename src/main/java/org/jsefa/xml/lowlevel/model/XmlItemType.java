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

package org.jsefa.xml.lowlevel.model;

/**
 * The type of a {@link XmlItem}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public enum XmlItemType {
    /**
     * Used in cases when there is no xml item and thus no xml item type.
     */
    NONE,
    /**
     * Denotes that the type of xml item is not supported.
     */
    UNKNOWN,
    /**
     * Denotes that the xml item is the start of a xml element.
     */
    ELEMENT_START,
    /**
     * Denotes that the xml item is the end of a xml element.
     */
    ELEMENT_END,
    /**
     * Denotes that the xml item represents text content (consecutive character nodes and CDATA sections).
     */
    TEXT_CONTENT;
}
