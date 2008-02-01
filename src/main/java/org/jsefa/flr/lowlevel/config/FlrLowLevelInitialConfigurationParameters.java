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

package org.jsefa.flr.lowlevel.config;

import org.jsefa.common.config.Configuration;
import org.jsefa.common.lowlevel.config.LowLevelInitialConfigurationParameters;

/**
 * A collection of initial configuration parameters for low level FLR.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public interface FlrLowLevelInitialConfigurationParameters extends LowLevelInitialConfigurationParameters {

    /**
     * Configuration parameter for the FlrLowLevelIOFactory class to use.
     */
    String LOW_LEVEL_IO_FACTORY_CLASS = "jsefa:flr:lowlevel:ioFactoryClass";

}
