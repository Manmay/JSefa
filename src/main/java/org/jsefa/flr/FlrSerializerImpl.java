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
import org.jsefa.flr.mapping.Align;
import org.jsefa.flr.mapping.FlrSimpleTypeMapping;
import org.jsefa.rbf.RbfEntryPoint;
import org.jsefa.rbf.RbfSerializer;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Default implementation of {@link FlrSerializer} based on
 * {@link RbfSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class FlrSerializerImpl extends RbfSerializer implements FlrSerializer {

    FlrSerializerImpl(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<Class, RbfEntryPoint> entryPoints) {
        super(typeMappingRegistry, entryPoints);
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
        if (value.length() >= flrMapping.getLength()) {
            writeString(value.substring(0, flrMapping.getLength()));
        } else {
            if (flrMapping.getAlign() == Align.LEFT) {
                writeString(value);
                writePadCharacters(value, flrMapping);
            } else {
                writePadCharacters(value, flrMapping);
                writeString(value);
            }

        }
    }

    private void writePadCharacters(String value, FlrSimpleTypeMapping flrMapping) {
        char padChar = flrMapping.getPadCharacter();
        int padCount = flrMapping.getLength() - value.length();
        for (int i = 0; i < padCount; i++) {
            writeChar(padChar);
        }
    }

}
