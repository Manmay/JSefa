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
public final class ElementDescriptor implements XmlNodeDescriptor {

    private final QName name;

    private final QName dataTypeName;

    private final int hashCode;

    /**
     * Constructs a new <code>ElementDescriptor</code>.
     * 
     * @param name the name of the element - may be null if the node is virtual, i. e. if the node groups other
     *                nodes without having an own representation, e. g. an implicit list.
     * @param dataTypeName the name of its data type - may be null for simplified element descriptors. If null the
     *                name must not be null.
     */
    public ElementDescriptor(QName name, QName dataTypeName) {
        if (name == null && dataTypeName == null) {
            throw new IllegalArgumentException(
                    "Both parameters (name, dataTypeName) are null but one of them must not be null!");
        }
        this.name = name;
        this.dataTypeName = dataTypeName;
        this.hashCode = calculateHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public XmlNodeType getType() {
        return XmlNodeType.ELEMENT;
    }

    /**
     * Returns the name of the element.
     * <p>
     * May be null if the node is virtual, i. e. if the node groups other nodes without having an own
     * representation, e. g. an implicit list.
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
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ElementDescriptor)) {
            return false;
        }
        final ElementDescriptor other = (ElementDescriptor) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
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
    @Override
    public String toString() {
        if (this.name == null) {
            return this.dataTypeName.toString();
        } else if (this.dataTypeName == null) {
            return this.name.toString();
        } else {
            return getName().toString() + ":" + getDataTypeName();
        }
    }

    private int calculateHashCode() {
        int resultCode = 1;
        if (this.name != null) {
            resultCode = 31 * resultCode + name.hashCode();
        }
        if (dataTypeName != null) {
            resultCode = 31 * resultCode + dataTypeName.hashCode();
        }
        return resultCode;
    }

}
