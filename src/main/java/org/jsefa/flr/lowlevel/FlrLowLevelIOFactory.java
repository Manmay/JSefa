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

package org.jsefa.flr.lowlevel;

import java.lang.reflect.Method;

import org.jsefa.IOFactoryException;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.lowlevel.LowLevelIOFactory;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.flr.lowlevel.config.FlrLowLevelConfiguration;
import org.jsefa.flr.lowlevel.config.FlrLowLevelInitialConfigurationParameters;

/**
 * Factory for creating {@link FlrLowLevelDeserializer}s and {@link FlrLowLevelSerializer}s.
 * 
 * This is the abstract base class for concrete factories. Each subclass must provide a static method
 * <code>create(FlrLowLevelConfiguration config)</code> as well as implement the abstract methods.
 * <p>
 * This class provides a static factory method {@link #createFactory(FlrLowLevelConfiguration)} to create an
 * instance of a concrete <code>FlrLowLevelIOFactory</code>.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class FlrLowLevelIOFactory implements LowLevelIOFactory {

    /**
     * Creates a new <code>FlrLowLevelIOFactory</code> for <code>FlrLowLevelSerializer</code>s and
     * <code>FlrLowLevelDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return an <code>FlrLowLevelIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrLowLevelIOFactory createFactory(FlrLowLevelConfiguration config) {
        Class<FlrLowLevelIOFactory> factoryClass = InitialConfiguration.get(
                FlrLowLevelInitialConfigurationParameters.LOW_LEVEL_IO_FACTORY_CLASS,
                FlrLowLevelIOFactoryImpl.class);
        Method createMethod = ReflectionUtil.getMethod(factoryClass, "createFactory",
                FlrLowLevelConfiguration.class);
        if (createMethod == null) {
            throw new IOFactoryException("Failed to create an FlrLowLevelIOFactory. The factory " + factoryClass
                    + " does not contain the required static createFactory method.");
        }
        try {
            return ReflectionUtil.callMethod(null, createMethod, config);
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an FlrLowLevelIOFactory", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract FlrLowLevelSerializer createSerializer();

    /**
     * {@inheritDoc}
     */
    public abstract FlrLowLevelDeserializer createDeserializer();
}
