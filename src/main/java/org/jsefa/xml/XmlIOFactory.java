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

package org.jsefa.xml;

import java.lang.reflect.Method;

import org.jsefa.IOFactory;
import org.jsefa.IOFactoryException;
import org.jsefa.common.annotation.ValidatorFactory;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.xml.annotation.XmlEntryPointFactory;
import org.jsefa.xml.annotation.XmlTypeMappingFactory;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.config.XmlInitialConfigurationParameters;

/**
 * Factory for creating {@link XmlSerializer}s and {@link XmlDeserializer}s.
 * <p>
 * This is the abstract base class for concrete factories. Each subclass must provide a static method
 * <code>create(XmlConfiguration config)</code> as well as implement the abstract methods.
 * <p>
 * This class provides a static factory method {@link #createFactory(XmlConfiguration)} to create an instance of a
 * concrete <code>XmlIOFactory</code>.
 * <p>
 * This class also provides static facade methods hiding the details of creating entry points based on annotated
 * object types.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class XmlIOFactory implements IOFactory {

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s and
     * <code>XmlDeserializer</code>s using the given configuration.
     * <p>
     * Note that the configuration should provide a non empty collection of entry points.<br>
     * You can use the methods {@link #createFactory(Class...)} or
     * {@link #createFactory(XmlConfiguration, Class...)} if you want to get the entry points automatically created
     * from annotated classes.
     * 
     * @param config the configuration object. It will be copied so that the given one can be modified or reused.
     * @return an <code>XmlIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static XmlIOFactory createFactory(XmlConfiguration config) {
        Class<XmlIOFactory> factoryClass = InitialConfiguration.get(
                XmlInitialConfigurationParameters.IO_FACTORY_CLASS, XmlIOFactoryImpl.class);
        Method createMethod = ReflectionUtil.getMethod(factoryClass, "createFactory", XmlConfiguration.class);
        if (createMethod == null) {
            throw new IOFactoryException("Failed to create an XmlIOFactory. The factory " + factoryClass
                    + " does not contain the required static createFactory method.");
        }
        try {
            return ReflectionUtil.callMethod(null, createMethod, config);
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an XmlIOFactory", e);
        }
    }

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s and
     * <code>XmlDeserializer</code>s which can handle objects of the given object types.
     * <p>
     * 
     * It creates a new {@link XmlConfiguration} with entry points generated from the annotations found in the
     * given object types.
     * 
     * @param objectTypes object types for which entry points should be created from annotations
     * @return a <code>XmlIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static XmlIOFactory createFactory(Class<?>... objectTypes) {
        return createFactory(new XmlConfiguration(), objectTypes);
    }

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s and
     * <code>XmlDeserializer</code>s which can handle objects of the given object types as well as those object
     * types for which entry points are defined in the <code>config</code>.
     * 
     * @param config the configuration object. It will be copied so that the given one can be modified or reused.
     * @param objectTypes object types for which entry points should be created from annotations
     * @return a a <code>XmlIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static XmlIOFactory createFactory(XmlConfiguration config, Class<?>... objectTypes) {
        XmlConfiguration newConfig = config.createCopy();
        try {
            XmlTypeMappingFactory typeMappingFactory = new XmlTypeMappingFactory(newConfig
                    .getTypeMappingRegistry(), newConfig.getSimpleTypeConverterProvider(), newConfig
                    .getValidatorProvider(), newConfig.getObjectAccessorProvider(), newConfig
                    .getDataTypeDefaultNameRegistry());
            ValidatorFactory validatorFactory = new ValidatorFactory(newConfig.getValidatorProvider(), newConfig
                    .getObjectAccessorProvider());
            XmlEntryPointFactory entryPointFactory = new XmlEntryPointFactory(typeMappingFactory, validatorFactory);
            newConfig.getEntryPoints().addAll(entryPointFactory.createEntryPoints(objectTypes));
            return createFactory(newConfig);
        } catch (IOFactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an XmlIOFactory", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract XmlSerializer createSerializer();

    /**
     * {@inheritDoc}
     */
    public abstract XmlDeserializer createDeserializer();

}
