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
 * A mapping between an rbf record node and a java object. This mapping is used for serialization and
 * deserialization.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class RecordMapping extends NodeMapping {

    private final String prefix;

    /**
     * Constructs a new <code>RecordMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param objectType the object type
     * @param fieldName the name of the field
     * @param prefix the prefix; may be null
     */
    public RecordMapping(String dataTypeName, Class<?> objectType, String fieldName, String prefix) {
        super(dataTypeName, objectType, fieldName);
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    public NodeType getNodeType() {
        return NodeType.RECORD;
    }

    /**
     * Returns the prefix or null if none exists.
     * 
     * @return the prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

}
