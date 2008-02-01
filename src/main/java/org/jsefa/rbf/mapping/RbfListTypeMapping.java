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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsefa.common.mapping.TypeMapping;

/**
 * A mapping between a java object type and a RBF list data type.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */

public final class RbfListTypeMapping extends TypeMapping<String> {

    private final Map<String, RecordMapping> recordMappingsByPrefix;

    private final Map<Class<?>, RecordMapping> recordMappingsByObjectType;

    /**
     * Constructs a new <code>RbfListTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param recordMappings the record mappings
     */
    public RbfListTypeMapping(String dataTypeName, Collection<RecordMapping> recordMappings) {
        super(List.class, dataTypeName);
        this.recordMappingsByPrefix = createRecordMappingsByPrefixMap(recordMappings);
        this.recordMappingsByObjectType = createRecordMappingsByObjectTypeMap(recordMappings);
    }

    /**
     * Returns the record mapping for the given object type.
     * 
     * @param objectType the object type
     * @return a record mapping
     */
    public RecordMapping getRecordMapping(Class<?> objectType) {
        return this.recordMappingsByObjectType.get(objectType);
    }

    /**
     * Returns the record mapping for the given prefix.
     * 
     * @param prefix the prefix
     * @return a record mapping
     */
    public RecordMapping getRecordMapping(String prefix) {
        return this.recordMappingsByPrefix.get(prefix);
    }

    /**
     * Returns the set of prefixes.
     * 
     * @return the set of prefixes.
     */
    public Set<String> getPrefixes() {
        return this.recordMappingsByPrefix.keySet();
    }

    private Map<String, RecordMapping> createRecordMappingsByPrefixMap(Collection<RecordMapping> recordMappings) {
        Map<String, RecordMapping> result = new HashMap<String, RecordMapping>();
        for (RecordMapping recordMapping : recordMappings) {
            result.put(recordMapping.getPrefix(), recordMapping);
        }
        return result;
    }

    private Map<Class<?>, RecordMapping> createRecordMappingsByObjectTypeMap(
            Collection<RecordMapping> recordMappings) {
        Map<Class<?>, RecordMapping> result = new HashMap<Class<?>, RecordMapping>();
        for (RecordMapping recordMapping : recordMappings) {
            result.put(recordMapping.getObjectType(), recordMapping);
        }
        return result;
    }
}
