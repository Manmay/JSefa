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

package org.jsefa.xml.lowlevel.config;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.lowlevel.config.LowLevelInitialConfigurationParameters;

/**
 * A collection of initial configuration parameters for low level XML.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlLowLevelInitialConfigurationParameters extends LowLevelInitialConfigurationParameters {

    /**
     * Configuration parameter for the XmlLowLevelIOFactory class to use.
     */
    String LOW_LEVEL_IO_FACTORY_CLASS = "jsefa:xml:lowlevel:ioFactoryClass";

    /**
     * Configuration parameter for the line indentation to use.
     */
    String LINE_INDENTATION = "jsefa:xml:lowlevel:lineIndentation";

    /**
     * Configuration parameter for the name of the data type attribute to use.
     */
    String DATA_TYPE_ATTRIBUTE_NAME = "jsefa:xml:lowlevel:dataTypeAttributeName";

    /**
     * Configuration parameter for the namespace manager to use.
     */
    String NAMESPACE_MANAGER = "jsefa:xml:lowlevel:namespaceManager";
}
