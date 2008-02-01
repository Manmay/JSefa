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

package org.jsefa.csv.lowlevel;

import java.lang.reflect.Method;

import org.jsefa.IOFactoryException;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.lowlevel.LowLevelIOFactory;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration;
import org.jsefa.csv.lowlevel.config.CsvLowLevelInitialConfigurationParameters;

/**
 * Factory for creating {@link CsvLowLevelDeserializer}s and {@link CsvLowLevelSerializer}s.
 * 
 * This is the abstract base class for concrete factories. Each subclass must provide a static method
 * <code>create(CsvLowLevelConfiguration config)</code> as well as implement the abstract methods.
 * <p>
 * This class provides a static factory method {@link #createFactory(CsvLowLevelConfiguration)} to create an
 * instance of a concrete <code>CsvLowLevelIOFactory</code>.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class CsvLowLevelIOFactory implements LowLevelIOFactory {

    /**
     * Creates a new <code>CsvLowLevelIOFactory</code> for <code>CsvLowLevelSerializer</code>s and
     * <code>CsvLowLevelDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return an <code>CsvLowLevelIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static CsvLowLevelIOFactory createFactory(CsvLowLevelConfiguration config) {
        Class<CsvLowLevelIOFactory> factoryClass = InitialConfiguration.get(
                CsvLowLevelInitialConfigurationParameters.LOW_LEVEL_IO_FACTORY_CLASS,
                CsvLowLevelIOFactoryImpl.class);
        Method createMethod = ReflectionUtil.getMethod(factoryClass, "createFactory",
                CsvLowLevelConfiguration.class);
        if (createMethod == null) {
            throw new IOFactoryException("Failed to create an CsvLowLevelIOFactory. The factory " + factoryClass
                    + " does not contain the required static createFactory method.");
        }
        try {
            return ReflectionUtil.callMethod(null, createMethod, config);
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an CsvLowLevelIOFactory", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract CsvLowLevelSerializer createSerializer();

    /**
     * {@inheritDoc}
     */
    public abstract CsvLowLevelDeserializer createDeserializer();
}
