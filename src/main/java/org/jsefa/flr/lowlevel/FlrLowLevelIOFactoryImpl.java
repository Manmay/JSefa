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

package org.jsefa.flr.lowlevel;

import org.jsefa.flr.lowlevel.config.FlrLowLevelConfiguration;

/**
 * Implementation of {@link FlrLowLevelIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class FlrLowLevelIOFactoryImpl extends FlrLowLevelIOFactory {

    private final FlrLowLevelConfiguration config;

    /**
     * Creates a new <code>FlrLowLevelIOFactoryImpl</code> for <code>FlrLowLevelSerializer</code>s and
     * <code>FlrLowLevelDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return a <code>FlrLowLevelIOFactoryImpl</code> factory
     */
    public static FlrLowLevelIOFactoryImpl createFactory(FlrLowLevelConfiguration config) {
        return new FlrLowLevelIOFactoryImpl(config);
    }

    FlrLowLevelIOFactoryImpl(FlrLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public FlrLowLevelDeserializer createDeserializer() {
        return new FlrLowLevelDeserializerImpl(this.config);
    }

    /**
     * {@inheritDoc}
     */
    public FlrLowLevelSerializer createSerializer() {
        return new FlrLowLevelSerializerImpl(this.config);
    }

}
