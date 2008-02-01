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
 * Descriptor for an xml element.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ElementDescriptor implements NodeDescriptor {

    private final QName name;

    private final QName dataTypeName;

    private final int hashCode;

    /**
     * Constructs a new <code>ElementDescriptor</code>.
     * 
     * @param name the name of the element
     * @param dataTypeName the name of its data type
     */
    public ElementDescriptor(QName name, QName dataTypeName) {
        this.name = name;
        this.dataTypeName = dataTypeName;
        this.hashCode = calculateHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public NodeType getType() {
        return NodeType.ELEMENT;
    }

    /**
     * Returns the name of the element.
     * 
     * @return the element name
     */
    public QName getName() {
        return this.name;
    }

    /**
     * Returns the name of the data type of the element.
     * 
     * @return the data type name
     */
    public QName getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ElementDescriptor)) {
            return false;
        }
        final ElementDescriptor other = (ElementDescriptor) obj;
        if (!name.equals(other.name)) {
            return false;
        }
        if (dataTypeName == null) {
            if (other.dataTypeName != null) {
                return false;
            }
        } else if (!dataTypeName.equals(other.dataTypeName)) {
            return false;
        }
        return true;
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
        hashCode = 31 * hashCode + name.hashCode();
        if (dataTypeName != null) {
            hashCode = 31 * hashCode + dataTypeName.hashCode();
        }
        return hashCode;
    }

}
