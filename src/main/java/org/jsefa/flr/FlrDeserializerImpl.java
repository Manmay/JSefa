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

import java.util.Map;

import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.flr.config.FlrConfiguration;
import org.jsefa.flr.lowlevel.Align;
import org.jsefa.flr.lowlevel.FlrLowLevelDeserializer;
import org.jsefa.flr.mapping.FlrSimpleTypeMapping;
import org.jsefa.rbf.RbfDeserializerImpl;
import org.jsefa.rbf.mapping.RbfEntryPoint;

/**
 * Default implementation of {@link FlrDeserializer} based on {@link RbfDeserializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrDeserializerImpl extends RbfDeserializerImpl implements FlrDeserializer {

    private final FlrLowLevelDeserializer lowLevelDeserializer;

    private final int prefixLength;

    FlrDeserializerImpl(FlrConfiguration config, Map<String, RbfEntryPoint> entryPointsByPrefixes,
            FlrLowLevelDeserializer lowLevelDeserializer) {
        super(config.getTypeMappingRegistry(), entryPointsByPrefixes);
        this.prefixLength = entryPointsByPrefixes.keySet().iterator().next().length();
        this.lowLevelDeserializer = lowLevelDeserializer;
    }

    FlrDeserializerImpl(FlrConfiguration config, RbfEntryPoint entryPoint,
            FlrLowLevelDeserializer lowLevelDeserializer) {
        super(config.getTypeMappingRegistry(), entryPoint);
        this.prefixLength = 0;
        this.lowLevelDeserializer = lowLevelDeserializer;
    }

    /**
     * {@inheritDoc}
     */
    protected Object readSimpleValue(SimpleTypeMapping<?> typeMapping) {
        FlrSimpleTypeMapping flrTypeMapping = (FlrSimpleTypeMapping) typeMapping;
        String stringValue = this.lowLevelDeserializer.nextField(flrTypeMapping.getLength(), flrTypeMapping
                .getAlign(), flrTypeMapping.getPadCharacter());
        if (stringValue.length() != 0) {
            return typeMapping.getSimpleTypeConverter().fromString(stringValue);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    protected String readPrefix() {
        return this.lowLevelDeserializer.nextField(this.prefixLength, Align.LEFT, ' ');
    }

    /**
     * {@inheritDoc}
     */
    protected FlrLowLevelDeserializer getLowLevelDeserializer() {
        return this.lowLevelDeserializer;
    }

}
