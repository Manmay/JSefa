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

import org.jsefa.ConfigurationException;

/**
 * A mapping between a java object type and a data type of the target format.
 * The latter type is denoted by its name which has a generic type.
 * <p>
 * For subclass providing methods modifying the internal structure:<br>
 * 1. Each method modifying the internal structure must call
 * {@link #assertNotFinished()} first<br>
 * 2. The client constructing the <code>TypeMapping</code> must call
 * {@link #finish()} after the last call of a modifying method.<br>
 * 3. There must be only one thread constructing the <code>TypeMapping</code>.<br>
 * 4. Instances of the class are immutable and thread-safe only after finishing
 * the construction step.
 * <p>
 * For all other subclasses:<br>
 * 1. Each constructor must call {@link #finish()} after the last write access.<br>
 * 2. Instances of such classes are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <T> the type of the data type name
 */
public abstract class TypeMapping<T> {
    private final Class objectType;

    private final T dataTypeName;

    private boolean finished;

    /**
     * Constructs a new <code>TypeMapping</code>.
     * 
     * @param objectType the object type.
     * @param dataTypeName the data type name.
     */
    public TypeMapping(Class objectType, T dataTypeName) {
        this.objectType = objectType;
        this.dataTypeName = dataTypeName;
    }

    /**
     * Returns the object type this mapping is for.
     * 
     * @return the object type
     */
    public final Class getObjectType() {
        return this.objectType;
    }

    /**
     * Returns the name of the data type this mapping is for.
     * 
     * @return the data type name
     */
    public final T getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * Finishes the construction of this <code>TypeMapping</code>.
     * <p>
     * Subclasses may override this method if the some properties of a type
     * mapping can be determined only when all input of the client is present.
     */
    public void finish() {
        assertNotFinished();
        this.finished = true;
    }

    /**
     * Returns true if the construction of this <code>TypeMapping</code> is
     * already finished; false otherwise.
     * 
     * @return true if the construction of this <code>TypeMapping</code> is
     *         already finished; false otherwise
     */
    public final boolean isFinished() {
        return this.finished;
    }

    /**
     * Asserts that this <code>TypeMapping</code> is not already finished.
     * 
     * @throws ConfigurationException is this <code>TypeMapping</code> is
     *             already finished
     */
    protected final void assertNotFinished() {
        if (this.finished) {
            throw new ConfigurationException("Type mapping is already finished");
        }
    }
}
