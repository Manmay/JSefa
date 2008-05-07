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

import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.validator.Validator;
import org.jsefa.xml.lowlevel.TextMode;
import org.jsefa.xml.namespace.QName;

/**
 * A mapping between an XML element node and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * @author Matthias Derer
 * 
 */
public final class ElementMapping extends XmlNodeMapping<ElementDescriptor> {

    private final boolean elementNameIsAmbiguous;

    private final TextMode textMode;

    /**
     * Constructs a new <code>ElementMapping</code>.
     * 
     * @param dataTypeName the name of the data type. This may be the data type name of the element node itself or
     *                of its implicit parent node.
     * @param elementDescriptor the descriptor of the element
     * @param objectType the type of the object. May be different from the object type contained in the field
     *                descriptor.
     * @param fieldDescriptor the descriptor of the field
     * @param validator the validator; may be null
     * @param elementNameIsAmbiguous true, if the name of the element in its given context (position in the xml
     *                document) is ambiguous, i. e. there is sibling element with the same name; false otherwise.
     * @param textMode defines the text mode in case of an element with text content (simple or not); may be
     *                <code>null</code>.
     */
    public ElementMapping(QName dataTypeName, ElementDescriptor elementDescriptor, Class<?> objectType,
            FieldDescriptor fieldDescriptor, Validator validator, boolean elementNameIsAmbiguous, TextMode textMode) {
        super(dataTypeName, elementDescriptor, objectType, fieldDescriptor, validator);
        this.elementNameIsAmbiguous = elementNameIsAmbiguous;
        this.textMode = textMode;
    }

    /**
     * @return true, if the name of the element in its given context (position in the xml document) is ambiguous,
     *         i. e. there is sibling element with the same name; false otherwise.
     */
    public boolean elementNameIsAmbiguous() {
        return elementNameIsAmbiguous;
    }

    /**
     * @return the text mode if this element allows for text content or null.
     */
    public TextMode getTextMode() {
        return textMode;
    }

    /**
     * @return true, if the data type name is not the same as the one of the element descriptor; false otherwise.
     */
    @Override
    public boolean isIndirectMapping() {
        return !getDataTypeName().equals(getNodeDescriptor().getDataTypeName());
    }

}
