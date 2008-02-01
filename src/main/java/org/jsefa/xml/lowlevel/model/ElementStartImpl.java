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

package org.jsefa.xml.lowlevel.model;

import org.jsefa.xml.namespace.QName;

/**
 * Implementation of {@link ElementStart}.
 * <p>
 * For performance reason no defensive copy of the attributes is made. A new instance of
 * <code>ElementStartImpl</code> takes control of the given array of attributes. Furthermore
 * {@link #getAttributes} directly returns this list - no copy or view is created. Thus a client must not change
 * the array of attributes after creating a new <code>ElementStartImpl</code> and must not change the result of
 * {@link #getAttributes}.
 * <p>
 * Respecting this an instance of this class is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ElementStartImpl implements ElementStart {
    private final Attribute[] attributes;

    private final QName dataTypeName;

    private final int depth;

    private final QName name;

    /**
     * Constructs a new <code>ElementStartImpl</code>.
     * <p>
     * Note: the new <code>ElementStartImpl</code> takes control of the given <code>attributes</code> list; it
     * makes no defensive copy of it.
     * 
     * @param name the name of the element
     * @param dataTypeName the data type name
     * @param attributes the list of attributes.
     * @param depth the depth of the element in the element tree.
     */
    public ElementStartImpl(QName name, QName dataTypeName, Attribute[] attributes, int depth) {
        this.name = name;
        this.dataTypeName = dataTypeName;
        this.depth = depth;
        this.attributes = attributes;
    }

    /**
     * {@inheritDoc}
     */
    public QName getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    public QName getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * {@inheritDoc}
     */
    public Attribute[] getAttributes() {
        return this.attributes;
    }

    /**
     * {@inheritDoc}
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * {@inheritDoc}
     */
    public XmlItemType getType() {
        return XmlItemType.ELEMENT_START;
    }

}
