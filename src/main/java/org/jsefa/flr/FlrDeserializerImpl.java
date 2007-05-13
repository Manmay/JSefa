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
import org.jsefa.rbf.RbfDeserializer;
import org.jsefa.rbf.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Default implementation of {@link FlrDeserializer} based on
 * {@link RbfDeserializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrDeserializerImpl extends RbfDeserializer implements FlrDeserializer {

    private final int prefixLength;

    FlrDeserializerImpl(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<String, RbfEntryPoint> entryPointsByPrefixes) {
        super(typeMappingRegistry, entryPointsByPrefixes);
        this.prefixLength = entryPointsByPrefixes.keySet().iterator().next().length();
    }

    FlrDeserializerImpl(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            RbfEntryPoint entryPoint) {
        super(typeMappingRegistry, entryPoint);
        this.prefixLength = 0;
    }

    /**
     * {@inheritDoc}
     */
    protected Object readSimpleValue(SimpleTypeMapping typeMapping) {
        FlrSimpleTypeMapping flrTypeMapping = (FlrSimpleTypeMapping) typeMapping;
        String stringValue = trim(nextString(flrTypeMapping.getLength()), flrTypeMapping);
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
        return nextString(this.prefixLength);
    }

    private String trim(String stringValue, FlrSimpleTypeMapping mapping) {
        if (mapping.getAlign() == Align.LEFT) {
            int endIndex = stringValue.length() - 1;
            while ((endIndex >= 0) && (stringValue.charAt(endIndex) == mapping.getPadCharacter())) {
                endIndex--;
            }
            if (endIndex < 0) {
                return "";
            } else {
                return stringValue.substring(0, endIndex + 1);
            }
        } else if (mapping.getAlign() == Align.RIGHT) {
            int startIndex = 0;
            while ((startIndex < stringValue.length())
                    && (stringValue.charAt(startIndex) == mapping.getPadCharacter())) {
                startIndex++;
            }
            if (startIndex == stringValue.length()) {
                return "";
            } else {
                return stringValue.substring(startIndex);
            }
        } else {
            throw new UnsupportedOperationException("Unknown align type: " + mapping.getAlign());
        }
    }

}
