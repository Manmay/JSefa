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

package org.jsefa.xml.annotation;

/**
 * Enum for the different types of default names.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public enum DefaultName {
    /**
     * Denotes that the default name should be the name of the respective field.
     */
    FIELD_NAME,

    /**
     * Denotes that the default name should be the default element name of the respective object type (see XmlDataType).
     */
    TYPE_DEFAULT_NAME
}