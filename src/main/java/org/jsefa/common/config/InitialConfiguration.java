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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jsefa.common.util.OnDemandObjectProvider;

/**
 * The single initial configuration for JSefa. See sub interfaces of {@link InitialConfigurationParameters} for the
 * available parameters.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class InitialConfiguration {

    private static final ConcurrentMap<String, Object> MAP = new ConcurrentHashMap<String, Object>();

    /**
     * Returns the value of the given parameter. If none is configured for the parameter, the
     * <code>defaultValue</code> retrieved from the <code>OnDemandObjectProvider</code> is registered and
     * returned.
     * 
     * @param parameter the parameter
     * @param <T> the expected type of the parameter value
     * @param defaultValueProvider the provider of the default value to use if none is configured for the parameter
     *                in question.
     * @return the value of the parameter
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String parameter, OnDemandObjectProvider defaultValueProvider) {
        T previousValue = (T) MAP.get(parameter);
        if (previousValue != null) {
            return previousValue;
        } else {
            T defaultValue = (T) defaultValueProvider.get();
            previousValue = (T) MAP.putIfAbsent(parameter, defaultValue);
            if (previousValue != null) {
                return previousValue;
            } else {
                return (T) defaultValue;
            }
        }
    }

    /**
     * Returns the value of the given parameter. If none is configured for the parameter, the
     * <code>defaultValue</code> is registered and returned.
     * 
     * @param parameter the parameter
     * @param <T> the expected type of the parameter value
     * @param defaultValue the default value to use if none is configured for the parameter in question.
     * @return the value of the parameter
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String parameter, Object defaultValue) {
        T previousValue = (T) MAP.putIfAbsent(parameter, defaultValue);
        if (previousValue != null) {
            return previousValue;
        } else {
            return (T) defaultValue;
        }
    }

    /**
     * Sets the parameter value of the given parameter to the given value if the parameter is not already bound to
     * another value. Otherwise an {@link InitialConfigurationException} will be thrown.
     * 
     * @param parameter the parameter
     * @param value the value
     * @throws InitialConfigurationException if the parameter is already bound to another value.
     */
    public static void set(String parameter, Object value) {
        Object other = MAP.putIfAbsent(parameter, value);
        if (other != null && !other.equals(value)) {
            throw new InitialConfigurationException("The configuration parameter " + parameter
                    + " is already bound to " + value);
        }
    }

    private InitialConfiguration() {

    }
}
