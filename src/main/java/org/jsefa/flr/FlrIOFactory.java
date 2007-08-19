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

package org.jsefa.flr;

import java.lang.reflect.Method;

import org.jsefa.IOFactory;
import org.jsefa.IOFactoryException;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.flr.annotation.FlrEntryPointFactory;
import org.jsefa.flr.annotation.FlrTypeMappingFactory;
import org.jsefa.flr.config.FlrConfiguration;
import org.jsefa.flr.config.FlrInitialConfigurationParameters;

/**
 * Factory for creating {@link FlrSerializer}s and {@link FlrDeserializer}s.
 * <p>
 * This is the abstract base class for concrete factories. Each subclass must
 * provide a static method <code>create(FlrConfiguration config)</code> as
 * well as implement the abstract methods.
 * <p>
 * This class provides a static factory method
 * {@link #createFactory(FlrConfiguration)} to create an instance of a concrete
 * <code>FlrIOFactory</code>.
 * <p>
 * This class also provides static facade methods hiding the details of
 * creating entry points based on annotated object types.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class FlrIOFactory implements IOFactory {

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s
     * and <code>FlrDeserializer</code>s using the given configuration.
     * <p>
     * Note that the configuration should provide a non empty collection of
     * entry points.<br>
     * You can use the methods {@link #createFactory(Class...)} or
     * {@link #createFactory(FlrConfiguration, Class...)} if you want to get the
     * entry points automatically created from annotated classes.
     * 
     * @param config the configuration object. It will be copied so that the
     *                given one can be modified or reused.
     * @return an <code>FlrIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(FlrConfiguration config) {
        Class<FlrIOFactory> factoryClass = InitialConfiguration.get(
                FlrInitialConfigurationParameters.IO_FACTORY_CLASS, FlrIOFactoryImpl.class);
        Method createMethod = ReflectionUtil.getMethod(factoryClass, "createFactory", FlrConfiguration.class);
        if (createMethod == null) {
            throw new IOFactoryException("Failed to create a FlrIOFactory. The factory " + factoryClass
                    + " does not contain the required static createFactory method.");
        }
        try {
            return ReflectionUtil.callMethod(null, createMethod, config);
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create a FlrIOFactory", e);
        }
    }

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s
     * and <code>FlrDeserializer</code>s which can handle objects of the
     * given object types.
     * <p>
     * 
     * It creates a new {@link FlrConfiguration} with entry points generated
     * from the annotations found in the given object types.
     * 
     * @param objectTypes object types for which entry points should be created
     *                from annotations
     * @return a <code>FlrIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(Class<?>... objectTypes) {
        return createFactory(new FlrConfiguration(), objectTypes);
    }

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s
     * and <code>FlrDeserializer</code>s which can handle objects of the
     * given object types as well as those object types for which entry points
     * are defined in the <code>config</code>.
     * 
     * @param config the configuration object. It will be copied so that the
     *                given one can be modified or reused.
     * @param objectTypes object types for which entry points should be created
     *                from annotations
     * @return a a <code>FlrIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(FlrConfiguration config, Class<?>... objectTypes) {
        FlrConfiguration newConfig = config.createCopy();
        try {
            FlrTypeMappingFactory typeMappingFactory = new FlrTypeMappingFactory(newConfig
                    .getTypeMappingRegistry(), newConfig.getSimpleTypeConverterProvider(), newConfig
                    .getObjectAccessorProvider(), newConfig.getDefaultPadCharacter());
            newConfig.getEntryPoints().addAll(
                    FlrEntryPointFactory.createEntryPoints(typeMappingFactory, objectTypes));
            return createFactory(newConfig);
        } catch (IOFactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an FlrIOFactory", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract FlrSerializer createSerializer();

    /**
     * {@inheritDoc}
     */
    public abstract FlrDeserializer createDeserializer();

}
