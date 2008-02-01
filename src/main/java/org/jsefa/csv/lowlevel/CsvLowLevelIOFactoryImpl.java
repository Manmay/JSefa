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

package org.jsefa.csv.lowlevel;

import org.jsefa.csv.lowlevel.config.CsvLowLevelConfiguration;

/**
 * Implementation of {@link CsvLowLevelIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CsvLowLevelIOFactoryImpl extends CsvLowLevelIOFactory {

    private final CsvLowLevelConfiguration config;

    /**
     * Creates a new <code>CsvLowLevelIOFactoryImpl</code> for <code>CsvLowLevelSerializer</code>s and
     * <code>CsvLowLevelDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return a <code>CsvLowLevelIOFactoryImpl</code> factory
     */
    public static CsvLowLevelIOFactoryImpl createFactory(CsvLowLevelConfiguration config) {
        return new CsvLowLevelIOFactoryImpl(config);
    }

    CsvLowLevelIOFactoryImpl(CsvLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public CsvLowLevelDeserializer createDeserializer() {
        return new CsvLowLevelDeserializerImpl(this.config);
    }

    /**
     * {@inheritDoc}
     */
    public CsvLowLevelSerializer createSerializer() {
        return new CsvLowLevelSerializerImpl(this.config);
    }

}
