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

import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.lowlevel.CsvLowLevelSerializer;
import org.jsefa.csv.lowlevel.config.QuoteMode;
import org.jsefa.csv.mapping.CsvSimpleTypeMapping;
import org.jsefa.rbf.RbfSerializer;
import org.jsefa.rbf.mapping.RbfEntryPoint;

/**
 * Default implementation of {@link CsvSerializer} based on {@link RbfSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class CsvSerializerImpl extends RbfSerializer implements CsvSerializer {

    private final CsvLowLevelSerializer lowLevelSerializer;

    CsvSerializerImpl(CsvConfiguration config, Map<Class<?>, RbfEntryPoint> entryPoints,
            CsvLowLevelSerializer lowLevelSerializer) {
        super(config.getTypeMappingRegistry(), entryPoints);
        this.lowLevelSerializer = lowLevelSerializer;
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSimpleValue(Object object, SimpleTypeMapping<?> mapping) {
        CsvSimpleTypeMapping csvSimpleTypeMapping = ((CsvSimpleTypeMapping) mapping);
        String value = mapping.getSimpleTypeConverter().toString(object);
        if (value == null) {
            value = csvSimpleTypeMapping.getNoValueString();
        }
        this.lowLevelSerializer.writeField(value, csvSimpleTypeMapping.getQuoteMode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writePrefix(String prefix) {
        this.lowLevelSerializer.writeField(prefix, QuoteMode.NEVER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CsvLowLevelSerializer getLowLevelSerializer() {
        return this.lowLevelSerializer;
    }

}
