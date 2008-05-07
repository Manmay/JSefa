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

import org.jsefa.common.accessor.ObjectAccessor;
import org.jsefa.common.mapping.ListTypeMapping;
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

public final class RbfListTypeMapping extends ListTypeMapping<String, RecordDescriptor, RecordMapping> {

    /**
     * Constructs a new <code>RbfListTypeMapping</code>.
     * 
     * @param dataTypeName the data type name
     * @param recordMappings the record mappings
     * @param objectAccessor the object accessor
     */
    public RbfListTypeMapping(String dataTypeName, Collection<RecordMapping> recordMappings,
            ObjectAccessor objectAccessor) {
        super(Collection.class, dataTypeName, recordMappings, objectAccessor);
    }

}
