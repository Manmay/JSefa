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

/**
 * Encapsulates the combination of a field name and a potential type of the field value.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FieldDescriptor {
    private final String name;

    private final Class<?> objectType;

    private final int hashCode;

    /**
     * Constructs a new <code>FieldDescriptor</code>.
     * 
     * @param name the name of the field
     * @param objectType the type of the field
     */
    public FieldDescriptor(String name, Class<?> objectType) {
        this.name = name;
        this.objectType = objectType;
        this.hashCode = 37 * (17 + getName().hashCode()) + getObjectType().hashCode();
    }

    /**
     * Returns the name of the field.
     * 
     * @return the name of the field
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the object type of the field.
     * 
     * @return the object type of the field
     */
    public Class<?> getObjectType() {
        return this.objectType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FieldDescriptor)) {
            return false;
        }
        FieldDescriptor other = (FieldDescriptor) obj;
        return getName().equals(other.getName()) && getObjectType().equals(other.getObjectType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName() + "@" + getObjectType().getName();
    }

}
