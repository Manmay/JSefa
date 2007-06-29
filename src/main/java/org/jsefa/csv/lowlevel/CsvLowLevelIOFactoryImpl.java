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


/**
 * Implementation of {@link CsvLowLevelIOFactory}.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CsvLowLevelIOFactoryImpl implements CsvLowLevelIOFactory {

    private static final CsvLowLevelIOFactoryImpl INSTANCE = new CsvLowLevelIOFactoryImpl();

    /**
     * Returns the single <code>CsvLowLevelIOFactoryImpl</code>.
     * 
     * @return the single <code>CsvLowLevelIOFactoryImpl</code>.
     */
    public static CsvLowLevelIOFactoryImpl getInstance() {
        return CsvLowLevelIOFactoryImpl.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public CsvLowLevelDeserializer createDeserializer(CsvLowLevelConfiguration config) {
        return new CsvLowLevelDeserializerImpl(config);
    }

    /**
     * {@inheritDoc}
     */
    public CsvLowLevelSerializer createSerializer(CsvLowLevelConfiguration config) {
        return new CsvLowLevelSerializerImpl(config);
    }

}