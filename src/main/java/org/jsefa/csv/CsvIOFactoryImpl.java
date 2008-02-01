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

package org.jsefa.csv;

import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.lowlevel.CsvLowLevelIOFactory;
import org.jsefa.rbf.RbfIOFactory;
import org.jsefa.rbf.mapping.RbfEntryPoint;

/**
 * Default implementation of {@link CsvIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public class CsvIOFactoryImpl extends CsvIOFactory {

    private final RbfIOFactory<CsvConfiguration, CsvSerializer, CsvDeserializer> rbfIOFactory;

    private final CsvLowLevelIOFactory lowLevelIOFactory;

    /**
     * Creates a new <code>CsvIOFactory</code> for <code>CsvSerializer</code>s and
     * <code>CsvDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return a <code>CsvIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static CsvIOFactory createFactory(CsvConfiguration config) {
        return new CsvIOFactoryImpl(config);
    }

    CsvIOFactoryImpl(CsvConfiguration config) {
        this.rbfIOFactory = createRbfIOFactory(config);
        this.lowLevelIOFactory = CsvLowLevelIOFactory.createFactory(config.getLowLevelConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    public CsvSerializer createSerializer() {
        return this.rbfIOFactory.createSerializer();
    }

    /**
     * {@inheritDoc}
     */
    public CsvDeserializer createDeserializer() {
        return this.rbfIOFactory.createDeserializer();
    }

    private RbfIOFactory<CsvConfiguration, CsvSerializer, CsvDeserializer> createRbfIOFactory(
            CsvConfiguration config) {
        return new RbfIOFactory<CsvConfiguration, CsvSerializer, CsvDeserializer>(config) {

            @Override
            protected CsvSerializer createSerializer(CsvConfiguration config,
                    Map<Class<?>, RbfEntryPoint> entryPointsByObjectType) {
                return new CsvSerializerImpl(config, entryPointsByObjectType, lowLevelIOFactory.createSerializer());
            }

            @Override
            protected CsvDeserializer createDeserializer(CsvConfiguration config, RbfEntryPoint entryPoint) {
                return new CsvDeserializerImpl(config, entryPoint, lowLevelIOFactory.createDeserializer());
            }

            @Override
            protected CsvDeserializer createDeserializer(CsvConfiguration config,
                    Map<String, RbfEntryPoint> entryPointsByPrefix) {
                return new CsvDeserializerImpl(config, entryPointsByPrefix, lowLevelIOFactory.createDeserializer());
            }

        };
    }
}
