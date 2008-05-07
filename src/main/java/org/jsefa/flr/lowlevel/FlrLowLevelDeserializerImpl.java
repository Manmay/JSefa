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

package org.jsefa.flr.lowlevel;

import org.jsefa.flr.lowlevel.config.FlrLowLevelConfiguration;
import org.jsefa.rbf.lowlevel.RbfLowLevelDeserializerImpl;

/**
 * Implementation of {@link FlrLowLevelDeserializer} based on {@link RbfLowLevelDeserializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class FlrLowLevelDeserializerImpl extends RbfLowLevelDeserializerImpl<FlrLowLevelConfiguration>
    implements FlrLowLevelDeserializer {
    /**
     * Constructs a new <code>FlrLowLevelDeserializerImpl</code>.
     * 
     * @param config the configuration
     */
    public FlrLowLevelDeserializerImpl(FlrLowLevelConfiguration config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    public String nextField(int length, Align align, char padCharacter) {
        return trim(this.nextString(length), align, padCharacter);
    }

    private String trim(String stringValue, Align align, char padCharacter) {
        if (stringValue == null) {
            return null;
        }
        if (align == Align.LEFT) {
            int endIndex = stringValue.length() - 1;
            while ((endIndex >= 0) && (stringValue.charAt(endIndex) == padCharacter)) {
                endIndex--;
            }
            if (endIndex < 0) {
                return "";
            } else {
                return stringValue.substring(0, endIndex + 1);
            }
        } else if (align == Align.RIGHT) {
            int startIndex = 0;
            while ((startIndex < stringValue.length()) && (stringValue.charAt(startIndex) == padCharacter)) {
                startIndex++;
            }
            if (startIndex == stringValue.length()) {
                return "";
            } else {
                return stringValue.substring(startIndex);
            }
        } else {
            throw new UnsupportedOperationException("Unknown align type: " + align);
        }
    }

}
