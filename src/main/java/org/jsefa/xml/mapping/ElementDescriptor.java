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

import org.jsefa.xml.QName;

/**
 * Descriptor for a xml element.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ElementDescriptor extends NodeDescriptor {
    /**
     * Constructs a new <code>ElementDescriptor</code>.
     * 
     * @param name the name of the element
     * @param dataTypeName the name of its data type
     */
    public ElementDescriptor(QName name, QName dataTypeName) {
        super(name, dataTypeName, NodeType.ELEMENT);
    }

}