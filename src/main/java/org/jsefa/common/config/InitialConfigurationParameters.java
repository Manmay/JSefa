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

package org.jsefa.common.config;


/**
 * Declares initial configuration parameters common to all format types.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public interface InitialConfigurationParameters {

    /**
     * Configuration parameter for the simple type converter provider to use.
     */
    String SIMPLE_TYPE_CONVERTER_PROVIDER = "jsefa:common:simpleTypeConverterProvider";

    /**
     * Configuration parameter for the validator provider to use.
     */
    String VALIDATOR_PROVIDER = "jsefa:common:validatorProvider";

    /**
     * Configuration parameter for the object accessor provider class to use.
     */
    String OBJECT_ACCESSOR_PROVIDER_CLASS = "jsefa:common:objectAccessorProviderClass";

    /**
     * Configuration parameter for the validation mode.
     */
    String VALIDATION_MODE = "jsefa:common:validationMode";

}
