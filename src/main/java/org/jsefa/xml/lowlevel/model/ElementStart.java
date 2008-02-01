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

import org.jsefa.xml.namespace.QName;

/**
 * An xml item describing the start of a xml element.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface ElementStart extends XmlItem {

    /**
     * Returns the name of the xml element.
     * 
     * @return the name of the xml element
     */
    QName getName();

    /**
     * Returns the name of the datatype of the xml element.
     * 
     * @return the name of the datatype of the xml element
     */
    QName getDataTypeName();

    /**
     * Returns the attributes of the xml element.
     * 
     * @return the attributes.
     */
    Attribute[] getAttributes();

    /**
     * Returns the depth of this element.
     * <p>
     * The depth of the root element is 0.
     * 
     * @return the depth of this element
     */
    int getDepth();
}
