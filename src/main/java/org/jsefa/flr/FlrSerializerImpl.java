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
import org.jsefa.flr.lowlevel.Align;
import org.jsefa.flr.lowlevel.FlrLowLevelSerializer;
import org.jsefa.flr.mapping.FlrSimpleTypeMapping;
import org.jsefa.rbf.RbfSerializer;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Default implementation of {@link FlrSerializer} based on
 * {@link RbfSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class FlrSerializerImpl extends RbfSerializer implements FlrSerializer {

    private final FlrLowLevelSerializer lowLevelSerializer;

    FlrSerializerImpl(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<Class, RbfEntryPoint> entryPoints) {
        super(typeMappingRegistry, entryPoints);
        this.lowLevelSerializer = config.getLowLevelIOFactory().createSerializer(config.getLowLevelConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    protected void writeSimpleValue(Object object, SimpleTypeMapping mapping) {
        FlrSimpleTypeMapping flrMapping = (FlrSimpleTypeMapping) mapping;
        String value = flrMapping.getSimpleTypeConverter().toString(object);
        if (value == null) {
            value = "";
        }
        this.lowLevelSerializer.writeField(value, flrMapping.getLength(), flrMapping.getAlign(), flrMapping
                .getPadCharacter());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void writePrefix(String prefix) {
        this.lowLevelSerializer.writeField(prefix, prefix.length(), Align.LEFT, ' ');
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected FlrLowLevelSerializer getLowLevelSerializer() {
        return this.lowLevelSerializer;
    }

}
