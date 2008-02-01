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

package org.jsefa.xml.config;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.config.InitialConfigurationParameters;
import org.jsefa.xml.lowlevel.config.XmlLowLevelInitialConfigurationParameters;

/**
 * A collection of initial configuration parameters for XML.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlInitialConfigurationParameters extends InitialConfigurationParameters {
    /**
     * Configuration parameter for the XmlIOFactory class to use.
     */
    String IO_FACTORY_CLASS = "jsefa:xml:ioFactoryClass";

    /**
     * Configuration parameter for the XmlDataTypeDefaultNameRegistry.
     */
    String DATA_TYPE_DEFAULT_NAME_REGISTRY = "jsefa:xml:dataTypeDefaultNameRegistry";

    /**
     * Configuration parameter for the XmlLowLevelIOFactory class to use.
     */
    String LOW_LEVEL_IO_FACTORY_CLASS = XmlLowLevelInitialConfigurationParameters.LOW_LEVEL_IO_FACTORY_CLASS;

    /**
     * Configuration parameter for the line indentation to use.
     */
    String LINE_INDENTATION = XmlLowLevelInitialConfigurationParameters.LINE_INDENTATION;

    /**
     * Configuration parameter for the name of the data type attribute to use.
     */
    String DATA_TYPE_ATTRIBUTE_NAME = XmlLowLevelInitialConfigurationParameters.DATA_TYPE_ATTRIBUTE_NAME;

    /**
     * Configuration parameter for the namespace manager to use.
     */
    String NAMESPACE_MANAGER = XmlLowLevelInitialConfigurationParameters.NAMESPACE_MANAGER;
}
