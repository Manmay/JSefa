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

import org.jsefa.common.mapping.EntryPoint;
import org.jsefa.common.validator.Validator;

/**
 * An entry point for RBF serialization and deserialization.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class RbfEntryPoint extends EntryPoint<String, String> {

    /**
     * Constructs a new <code>RbfEntryPoint</code>.
     * 
     * @param dataTypeName the data type name
     * @param prefix the prefix as the designator
     * @param validator the validator; may be null
     */
    public RbfEntryPoint(String dataTypeName, String prefix, Validator validator) {
        super(dataTypeName, prefix, validator);
    }

}
