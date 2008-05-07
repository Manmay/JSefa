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
 * Descriptor for an rbf record.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class RecordDescriptor implements RbfNodeDescriptor {

    private final String prefix;

    /**
     * Constructs a new <code>RecordDescriptor</code>.
     * @param prefix the record prefix
     */
    public RecordDescriptor(String prefix) {
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    public RbfNodeType getType() {
        return RbfNodeType.RECORD;
    }

    /**
     * @return the prefix; may be null if the node is virtual, i. e. if the node groups other
     *                nodes without having an own representation, e. g. a sub record list node.
     */
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        if (this.prefix == null) {
            return super.hashCode();
        } else {
            return this.prefix.hashCode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RecordDescriptor)) {
            return false;
        }
        RecordDescriptor other = (RecordDescriptor) obj;
        if (this.prefix == null) {
            return other.prefix == null;
        }
        return this.prefix.equals(other.prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (this.prefix == null) {
            return "VIRTUAL NODE";
        } else {
            return this.prefix;
        }
    }
}
