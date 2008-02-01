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

import org.jsefa.Serializer;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;

/**
 * Iterator-style interface for stream based xml serializer.
 * <p>
 * Notes on handling of <code>null</code> values:<br>
 * 1. A field mapped to an attribute, a simple type element, complex type element or list type element with no
 * value (<code>null</code>) will not be serialized, i. e. no element or attribute will be created for this
 * field.<br>
 * 2. An object mapped to a complex element with all fields having a null value will be serialized to an empty
 * element. This includes objects mapped to complex elements with a text content. For those objects the
 * serialzation result is the same for a null and for an empty text content, but the deserialization will create an
 * empty <code>String</code> in both cases.
 * 
 * @see Serializer
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlSerializer extends Serializer {
    /**
     * Returns a low level xml serializer.
     * 
     * @return a low level xml serializer.
     */
    XmlLowLevelSerializer getLowLevelSerializer();
}
