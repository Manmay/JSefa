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
 * Descriptor for an xml attribute.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class AttributeDescriptor implements XmlNodeDescriptor {

    private final QName name;

    private final int hashCode;

    /**
     * Constructs a new <code>AttributeDescriptor</code>.
     * 
     * @param name the name of the attribute
     */
    public AttributeDescriptor(QName name) {
        this.name = name;
        this.hashCode = calculateHashCode();
    }

    /**
     * {@inheritDoc}
     */
    public XmlNodeType getType() {
        return XmlNodeType.ATTRIBUTE;
    }

    /**
     * Returns the name of the attribute.
     * 
     * @return the attribute name
     */
    public QName getName() {
        return this.name;
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
        if (!(obj instanceof AttributeDescriptor)) {
            return false;
        }
        final AttributeDescriptor other = (AttributeDescriptor) obj;
        return name.equals(other.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName().toString();
    }

    private int calculateHashCode() {
        return name.hashCode();
    }

}
