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

package org.jsefa;

import org.jsefa.common.accessor.ObjectAccessorProvider;
import org.jsefa.common.accessor.ReflectionBasedObjectAccessorProvider;
import org.jsefa.common.converter.SimpleTypeConverterProvider;

/**
 * The abstract superclass for configuration object classes. It uses lazy
 * initialization for the object accessor provider and the simple type converter
 * provider.
 * <p>
 * A configuration object is used when creating a new {@link IOFactory}. One
 * configuration object can be used for the creation of multiple factories as
 * each new factory holds its own copy of the configuration object. So the
 * configuration object can be changed after creating a factory with it without
 * affecting the configuration of the factory.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class Configuration {

    private ObjectAccessorProvider objectAccessorProvider;

    private SimpleTypeConverterProvider simpleTypeConverterProvider;

    /**
     * Constructs a new <code>Configuration</code>.
     */
    protected Configuration() {
    }

    /**
     * Constructs a new <code>Configuration</code> as a copy of the given one.
     * 
     * @param other the other config
     */
    @SuppressWarnings("unchecked")
    protected Configuration(Configuration other) {
        setObjectAccessorProvider(other.getObjectAccessorProvider());
        getSimpleTypeConverterProvider().registerAll(other.getSimpleTypeConverterProvider());
    }

    /**
     * Returns the object accessor provider.
     * 
     * @return the object accessor provider
     */
    public final ObjectAccessorProvider getObjectAccessorProvider() {
        if (this.objectAccessorProvider == null) {
            this.objectAccessorProvider = new ReflectionBasedObjectAccessorProvider();
        }
        return this.objectAccessorProvider;
    }

    /**
     * Returns the simple type converter provider.
     * 
     * @return the simple type converter provider
     */
    public final SimpleTypeConverterProvider getSimpleTypeConverterProvider() {
        if (this.simpleTypeConverterProvider == null) {
            this.simpleTypeConverterProvider = new SimpleTypeConverterProvider();
        }
        return this.simpleTypeConverterProvider;
    }

    /**
     * Sets the <code>ObjectAccessorProvider</code>.
     * 
     * @param objectAccessorProvider the <code>ObjectAccessorProvider</code>
     */
    public final void setObjectAccessorProvider(ObjectAccessorProvider objectAccessorProvider) {
        this.objectAccessorProvider = objectAccessorProvider;
    }

    /**
     * Sets the <code>SimpleTypeConverterProvider</code>.
     * 
     * @param simpleTypeConverterProvider the
     *            <code>SimpleTypeConverterProvider</code>
     */
    public final void setSimpleTypeConverterProvider(SimpleTypeConverterProvider simpleTypeConverterProvider) {
        this.simpleTypeConverterProvider = simpleTypeConverterProvider;
    }

    /**
     * Creates a copy of this <code>Configuration</code>.
     * 
     * @return a copy of this <code>Configuration</code>
     */
    public abstract Configuration createCopy();

}
