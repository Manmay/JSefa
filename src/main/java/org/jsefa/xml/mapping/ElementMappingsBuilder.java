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

import java.util.ArrayList;
import java.util.List;

import org.jsefa.common.mapping.FieldDescriptor;
import org.jsefa.common.validator.Validator;
import org.jsefa.xml.lowlevel.TextMode;
import org.jsefa.xml.namespace.QName;

/**
 * Builder for creating a list of {@link ElementMapping}s whereby the parameter
 * <code>elementNameIsAmbiguous</code> is determined automatically.
 * <p>
 * It is not intended to be thread safe.
 * 
 * @author Norman Lahme-Huetig
 * @author Matthias Derer
 * 
 */
public final class ElementMappingsBuilder {
    private final List<DataHolder> dataHolders;

    /**
     * Constructs a new <code>ElementMappingsBuilder</code>.
     */
    public ElementMappingsBuilder() {
        this.dataHolders = new ArrayList<DataHolder>();
    }

    /**
     * Adds an element mapping to be build.
     * 
     * @param dataTypeName the data type name
     * @param elementDescriptor the element descriptor
     * @param fieldDescriptor the field descriptor
     * @param validator the validator; may be null
     * @param textMode the text mode
     */
    public void addMapping(QName dataTypeName, ElementDescriptor elementDescriptor,
            FieldDescriptor fieldDescriptor, Validator validator, TextMode textMode) {
        addMapping(dataTypeName, elementDescriptor, fieldDescriptor.getObjectType(), fieldDescriptor,
                validator, textMode);
    }

    /**
     * Adds an element mapping to be build.
     * 
     * @param dataTypeName the data type name
     * @param elementDescriptor the element descriptor
     * @param objectType the objectType
     * @param fieldDescriptor the field descriptor; may be <code>null</code>
     * @param validator the validator; may be <code>null</code>
     * @param textMode the text mode; may be <code>null</code>
     */
    public void addMapping(QName dataTypeName, ElementDescriptor elementDescriptor, Class<?> objectType,
            FieldDescriptor fieldDescriptor, Validator validator, TextMode textMode) {
        this.dataHolders.add(new DataHolder(dataTypeName, elementDescriptor, objectType, fieldDescriptor,
                validator, textMode));
    }

    /**
     * Returns the building result, i. e. a list of <code>ElementMapping</code>s.
     * 
     * @return a list of <code>ElementMapping</code>s
     */
    public List<ElementMapping> getResult() {
        List<ElementMapping> elementMappings = new ArrayList<ElementMapping>();
        for (DataHolder dataHolder : this.dataHolders) {
            elementMappings.add(new ElementMapping(dataHolder.dataTypeName, dataHolder.elementDescriptor,
                    dataHolder.objectType, dataHolder.fieldDescriptor, dataHolder.validator,
                    isAmbiguous(dataHolder), dataHolder.textMode));
        }
        return elementMappings;
    }

    private boolean isAmbiguous(DataHolder dataHolder) {
        if (dataHolder.elementDescriptor.getName() == null) {
            return false;
        }
        for (DataHolder otherDataHolder : this.dataHolders) {
            if (otherDataHolder == dataHolder || otherDataHolder.elementDescriptor.getName() == null) {
                continue;
            }
            if (otherDataHolder.elementDescriptor.getName().equals(dataHolder.elementDescriptor.getName())) {
                if (otherDataHolder.objectType.isAssignableFrom(dataHolder.objectType)) {
                    return true;
                } else if (!dataHolder.objectType.isAssignableFrom(otherDataHolder.objectType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final class DataHolder {
        private QName dataTypeName;

        private ElementDescriptor elementDescriptor;

        private FieldDescriptor fieldDescriptor;

        private Validator validator;

        private Class<?> objectType;

        private TextMode textMode;

        private DataHolder(QName dataTypeName, ElementDescriptor elementDescriptor, Class<?> objectType,
                FieldDescriptor fieldDescriptor, Validator validator, TextMode textMode) {
            this.dataTypeName = dataTypeName;
            this.elementDescriptor = elementDescriptor;
            this.objectType = objectType;
            this.fieldDescriptor = fieldDescriptor;
            this.validator = validator;
            this.textMode = textMode;
        }
    }
}
