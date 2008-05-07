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
 * Descriptor for an rbf field.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class RbfFieldDescriptor implements RbfNodeDescriptor {

    private final int index;
    
    /**
     * Constructs a new <code>RbfFieldDescriptor</code>.
     * @param index the relative index of the field
     */
    public RbfFieldDescriptor(int index) {
        this.index = index;
    }

    /**
     * {@inheritDoc}
     */
    public RbfNodeType getType() {
        return RbfNodeType.FIELD;
    }
    
    /**
     * @return the relative index of the field
     */
    public int getIndex() {
        return this.index;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.index;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RbfFieldDescriptor)) {
            return false;
        }
        RbfFieldDescriptor other = (RbfFieldDescriptor) obj;
        return other.index == this.index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Integer.toString(this.index);
    }
}
