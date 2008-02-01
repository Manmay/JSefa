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
 * Implementation of {@link Attribute}.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class AttributeImpl implements Attribute {

    private final QName name;

    private final String value;

    /**
     * Constructs a new <code>AttributeImpl</code>.
     * 
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public AttributeImpl(QName name, String value) {
        this.name = name;
        this.value = value;
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
    public String getValue() {
        return this.value;
    }

}
