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

package org.jsefa.common.mapping;

import org.jsefa.Deserializer;
import org.jsefa.Serializer;
import org.jsefa.common.validator.Validator;

/**
 * An entry point describes an item of the respective format that should be serialized or deserialized. An entry
 * point is required for every type of object which will be passed to {@link Serializer#write} or which should be
 * returned from {@link Deserializer#next} and only for these objects (not for the objects related to these ones).
 * <p>
 * Each entry point consists of the following parts:<br>
 * 1) A data type name which unambiguously maps to a {@link TypeMapping}.<br>
 * 2) A designator that designates the item of the respective format (e. g. the name of the xml element).
 * 3) An optional {@link Validator}.
 * <p>
 * 
 * Instances of this class are immutable and thread-safe. This must be true for all subclasses, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <T> the type of the data type name
 * @param <D> the type of the designator
 * 
 */
public class EntryPoint<T, D> {

    private final T dataTypeName;

    private final D designator;
    
    private final Validator validator;

    /**
     * Constructs a entry point given data type name and a designator.
     * 
     * @param dataTypeName the data type name
     * @param designator the designator
     * @param validator the validator; may be null
     */
    public EntryPoint(T dataTypeName, D designator, Validator validator) {
        this.dataTypeName = dataTypeName;
        this.designator = designator;
        this.validator = validator;
    }

    /**
     * Returns the designator.
     * 
     * @return the designator
     */
    public final D getDesignator() {
        return this.designator;
    }

    /**
     * Returns the data type name.
     * 
     * @return the data type name
     */
    public final T getDataTypeName() {
        return this.dataTypeName;
    }
    
    /**
     * Returns the validator.
     * @return the validator or null if none exists.
     */
    public final Validator getValidator() {
        return this.validator;
    }
}
