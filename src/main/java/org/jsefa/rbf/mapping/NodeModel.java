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
 * A model of a node used for serialization and deserialization.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class NodeModel {

    private final String dataTypeName;

    private final String fieldName;

    private final String prefix;

    /**
     * Constructs a new <code>NodeModel</code> for the given data type name
     * and field name.
     * 
     * @param dataTypename the data type name
     * @param fieldName the name of the field; may be null.
     */
    public NodeModel(String dataTypename, String fieldName) {
        this(dataTypename, fieldName, null);
    }

    /**
     * Constructs a new <code>NodeModel</code> for the given data type name,
     * prefix, and field name.
     * 
     * @param dataTypeName the data type name
     * @param fieldName the name of the field; may be null
     * @param prefix the prefix; may be null
     */
    public NodeModel(String dataTypeName, String fieldName, String prefix) {
        this.dataTypeName = dataTypeName;
        this.fieldName = fieldName;
        this.prefix = prefix;
    }

    /**
     * Returns the data type name.
     * 
     * @return the data type name
     */
    public String getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * Returns the prefix or null if none exists.
     * 
     * @return the prefix
     */
    public String getPrefix() {
        return this.prefix;
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
