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

package org.jsefa.xml.lowlevel;

import java.lang.reflect.Method;

import org.jsefa.IOFactoryException;
import org.jsefa.common.config.InitialConfiguration;
import org.jsefa.common.lowlevel.LowLevelIOFactory;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.lowlevel.config.XmlLowLevelInitialConfigurationParameters;

/**
 * Factory for creating {@link XmlLowLevelDeserializer}s and {@link XmlLowLevelSerializer}s.
 * 
 * This is the abstract base class for concrete factories. Each subclass must provide a static method
 * <code>create(XmlLowLevelConfiguration config)</code> as well as implement the abstract methods.
 * <p>
 * This class provides a static factory method {@link #createFactory(XmlLowLevelConfiguration)} to create an instance of
 * a concrete <code>XmlLowLevelIOFactory</code>.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class XmlLowLevelIOFactory implements LowLevelIOFactory {

    /**
     * Creates a new <code>XmlLowLevelIOFactory</code> for <code>XmlLowLevelSerializer</code>s and
     * <code>XmlLowLevelDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return an <code>XmlLowLevelIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static XmlLowLevelIOFactory createFactory(XmlLowLevelConfiguration config) {
        Class<?> ioFactoryClass = null;
        if (ReflectionUtil.hasClass("org.jsefa.xml.lowlevel.StaxBasedXmlLowLevelIOFactory")) {
            ioFactoryClass = ReflectionUtil.getClass("org.jsefa.xml.lowlevel.StaxBasedXmlLowLevelIOFactory");
        } else if (ReflectionUtil.hasClass("org.jsefa.xml.lowlevel.XmlPullBasedXmlLowLevelIOFactory")) {
            ioFactoryClass = ReflectionUtil.getClass("org.jsefa.xml.lowlevel.XmlPullBasedXmlLowLevelIOFactory");
        }

        if (ioFactoryClass == null) {
            throw new IOFactoryException("Failed to create an XmlLowLevelIOFactory");
        }
        Class<XmlLowLevelIOFactory> factoryClass = InitialConfiguration.get(
                XmlLowLevelInitialConfigurationParameters.LOW_LEVEL_IO_FACTORY_CLASS, ioFactoryClass);
        Method createMethod = ReflectionUtil.getMethod(factoryClass, "createFactory", XmlLowLevelConfiguration.class);
        if (createMethod == null) {
            throw new IOFactoryException("Failed to create an XmlLowLevelIOFactory. The factory " + factoryClass
                    + " does not contain the required static createFactory method.");
        }
        try {
            return ReflectionUtil.callMethod(null, createMethod, config);
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an XmlLowLevelIOFactory", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract XmlLowLevelSerializer createSerializer();

    /**
     * {@inheritDoc}
     */
    public abstract XmlLowLevelDeserializer createDeserializer();
}
