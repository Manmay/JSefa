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

import org.jsefa.DeserializationException;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.lowlevel.CsvLowLevelDeserializer;
import org.jsefa.csv.lowlevel.config.QuoteMode;
import org.jsefa.csv.mapping.CsvSimpleTypeMapping;
import org.jsefa.rbf.RbfDeserializer;
import org.jsefa.rbf.mapping.RbfEntryPoint;

/**
 * Default implementation of {@link CsvDeserializer} based on {@link RbfDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvDeserializerImpl extends RbfDeserializer implements CsvDeserializer {

    private final CsvLowLevelDeserializer lowLevelDeserializer;

    CsvDeserializerImpl(CsvConfiguration config, Map<String, RbfEntryPoint> entryPointsByPrefixes,
            CsvLowLevelDeserializer lowLevelDeserializer) {
        super(config.getTypeMappingRegistry(), entryPointsByPrefixes);
        this.lowLevelDeserializer = lowLevelDeserializer;
    }

    CsvDeserializerImpl(CsvConfiguration config, RbfEntryPoint entryPoint,
            CsvLowLevelDeserializer lowLevelDeserializer) {
        super(config.getTypeMappingRegistry(), entryPoint);
        this.lowLevelDeserializer = lowLevelDeserializer;
    }

    /**
     * {@inheritDoc}
     */
    protected Object readSimpleValue(SimpleTypeMapping<?> typeMapping) {
        String stringValue = this.lowLevelDeserializer.nextField(((CsvSimpleTypeMapping) typeMapping)
                .getQuoteMode());
        if (stringValue == null) {
            throw new DeserializationException("Unexpected end of line reached");
        }
        if (stringValue.length() > 0) {
            return typeMapping.getSimpleTypeConverter().fromString(stringValue);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String readPrefix() {
        // will never return null as this method is called for non empty lines only
        return lowLevelDeserializer.nextField(QuoteMode.NEVER);
    }

    /**
     * {@inheritDoc}
     */
    protected CsvLowLevelDeserializer getLowLevelDeserializer() {
        return this.lowLevelDeserializer;
    }

}
