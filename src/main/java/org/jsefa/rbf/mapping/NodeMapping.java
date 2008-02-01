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

package org.jsefa.rbf.mapping;

/**
 * A mapping between an rbf node and a java object. This mapping is used for serialization and deserialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class NodeMapping {

    private final String dataTypeName;

    private final Class<?> objectType;

    private final String fieldName;

    /**
     * Constructs a new <code>NodeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param objectType the object type
     * @param fieldName the name of the field
     */
    public NodeMapping(String dataTypeName, Class<?> objectType, String fieldName) {
        this.dataTypeName = dataTypeName;
        this.objectType = objectType;
        this.fieldName = fieldName;
    }

    /**
     * Returns the node type.
     * 
     * @return the node type
     */
    public abstract NodeType getNodeType();

    /**
     * Returns the data type name.
     * 
     * @return the data type name
     */
    public String getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * Returns the object type.
     * 
     * @return the object type
     */
    public Class<?> getObjectType() {
        return this.objectType;
    }

    /**
     * Returns the field name.
     * 
     * @return the field name
     */
    public String getFieldName() {
        return this.fieldName;
    }

}
