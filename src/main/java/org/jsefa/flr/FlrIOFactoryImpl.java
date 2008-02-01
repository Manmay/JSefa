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

import java.util.Collection;
import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.flr.config.FlrConfiguration;
import org.jsefa.flr.lowlevel.FlrLowLevelIOFactory;
import org.jsefa.rbf.RbfIOFactory;
import org.jsefa.rbf.mapping.RbfEntryPoint;

/**
 * Default implementation of {@link FlrIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class FlrIOFactoryImpl extends FlrIOFactory {

    private final RbfIOFactory<FlrConfiguration, FlrSerializer, FlrDeserializer> rbfIOFactory;

    private final FlrLowLevelIOFactory lowLevelIOFactory;

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s and
     * <code>FlrDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return a <code>FlrIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(FlrConfiguration config) {
        return new FlrIOFactoryImpl(config);
    }

    FlrIOFactoryImpl(FlrConfiguration config) {
        this.rbfIOFactory = createRbfIOFactory(config);
        if (this.rbfIOFactory.withPrefixes()) {
            assertEqualPrefixLength(config.getEntryPoints());
        }
        this.lowLevelIOFactory = FlrLowLevelIOFactory.createFactory(config.getLowLevelConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    public FlrSerializer createSerializer() {
        return this.rbfIOFactory.createSerializer();
    }

    /**
     * {@inheritDoc}
     */
    public FlrDeserializer createDeserializer() {
        return this.rbfIOFactory.createDeserializer();
    }

    private void assertEqualPrefixLength(Collection<RbfEntryPoint> entryPoints) {
        int length = entryPoints.iterator().next().getDesignator().length();
        for (RbfEntryPoint entryPoint : entryPoints) {
            if (entryPoint.getDesignator().length() != length) {
                throw new IOFactoryException("The prefix " + entryPoint.getDesignator() + " has not the length "
                        + length);
            }
        }
    }

    private RbfIOFactory<FlrConfiguration, FlrSerializer, FlrDeserializer> createRbfIOFactory(
            FlrConfiguration config) {
        return new RbfIOFactory<FlrConfiguration, FlrSerializer, FlrDeserializer>(config) {

            @Override
            protected FlrSerializer createSerializer(FlrConfiguration config,
                    Map<Class<?>, RbfEntryPoint> entryPointsByObjectType) {
                return new FlrSerializerImpl(config, entryPointsByObjectType, lowLevelIOFactory.createSerializer());
            }

            @Override
            protected FlrDeserializer createDeserializer(FlrConfiguration config, RbfEntryPoint entryPoint) {
                return new FlrDeserializerImpl(config, entryPoint, lowLevelIOFactory.createDeserializer());
            }

            @Override
            protected FlrDeserializer createDeserializer(FlrConfiguration config,
                    Map<String, RbfEntryPoint> entryPointsByPrefix) {
                return new FlrDeserializerImpl(config, entryPointsByPrefix, lowLevelIOFactory.createDeserializer());
            }

        };
    }
}
