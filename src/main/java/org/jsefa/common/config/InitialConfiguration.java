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


/**
 * The single initial configuration for JSefa.
 * 
 * @see Configuration
 * @author Norman Lahme-Huetig
 * 
 */
public final class InitialConfiguration {

    private static final ConcurrentMap<String, Object> MAP = new ConcurrentHashMap<String, Object>();

    public static <T> T get(String parameter) {
        return (T) MAP.get(parameter);
    }

    public static <T> T get(String parameter, Object defaultValue) {
        T previousValue = (T) MAP.putIfAbsent(parameter, defaultValue);
        if (previousValue != null) {
            return previousValue;
        } else {
            return (T) defaultValue;
        }
    }

    public static void set(String parameter, Object value) {
        Object other = MAP.putIfAbsent(parameter, value);
        if (!other.equals(value)) {
            throw new InitialConfigurationException("The configuration key " + parameter + " is already bound to "
                    + value);
        }
    }
    
    private InitialConfiguration() {
        
    }
}
