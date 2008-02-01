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
 * Implementation of {@link ElementEnd}.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class ElementEndImpl implements ElementEnd {
    private final int depth;

    private final QName name;

    /**
     * Constructs a new <code>ElementEndImpl</code>.
     * 
     * @param name the name of the element
     * @param depth the depth of the element
     */
    public ElementEndImpl(QName name, int depth) {
        this.name = name;
        this.depth = depth;
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
    public int getDepth() {
        return this.depth;
    }

    /**
     * {@inheritDoc}
     */
    public XmlItemType getType() {
        return XmlItemType.ELEMENT_END;
    }

}
