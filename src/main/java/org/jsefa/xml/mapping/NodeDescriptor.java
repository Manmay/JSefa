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

package org.jsefa.xml.mapping;

import org.jsefa.xml.namespace.QName;

/**
 * Descriptor for a xml node consisting of a type (like attribute), name and
 * data type name.
 * 
 * <p>
 * Instances of this class are immutable and thread-safe. This must be true for
 * subclasses, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class NodeDescriptor {
    private final NodeType type;

    private final QName name;

    private final QName dataTypeName;

    private final int hashCode;

    /**
     * Constructs a new <code>NodeDescriptor</code>.
     * 
     * @param name the name of the node
     * @param dataTypeName the name of its data type
     * @param type its type
     */
    public NodeDescriptor(QName name, QName dataTypeName, NodeType type) {
        this.name = name;
        this.dataTypeName = dataTypeName;
        this.type = type;
        this.hashCode = calculateHashCode();
    }

    /**
     * Returns the type of the node.
     * 
     * @return the node type
     */
    public final NodeType getType() {
        return this.type;
    }

    /**
     * Returns the name of the node.
     * 
     * @return the node name
     */
    public final QName getName() {
        return this.name;
    }

    /**
     * Returns the name of the data type of the node.
     * 
     * @return the data type name
     */
    public final QName getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * {@inheritDoc}
     */
    public final int hashCode() {
        return this.hashCode;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NodeDescriptor)) {
            return false;
        }
        final NodeDescriptor other = (NodeDescriptor) obj;
        if (dataTypeName == null) {
            if (other.dataTypeName != null) {
                return false;
            }
        } else if (!dataTypeName.equals(other.dataTypeName)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return type == other.type;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        if (this.dataTypeName != null) {
            return getName().toString() + ":" + getDataTypeName();
        } else {
            return getName().toString();
        }
    }

    private int calculateHashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + ((dataTypeName == null) ? 0 : dataTypeName.hashCode());
        hashCode = 31 * hashCode + ((name == null) ? 0 : name.hashCode());
        hashCode = 31 * hashCode + type.hashCode();
        return hashCode;

    }

}
