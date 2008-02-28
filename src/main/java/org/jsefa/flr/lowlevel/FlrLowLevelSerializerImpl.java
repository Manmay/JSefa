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
import org.jsefa.rbf.lowlevel.RbfLowLevelSerializerImpl;

/**
 * Implementation of {@link FlrLowLevelSerializer} based on {@link RbfLowLevelSerializerImpl}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class FlrLowLevelSerializerImpl extends RbfLowLevelSerializerImpl<FlrLowLevelConfiguration>
    implements FlrLowLevelSerializer {

    /**
     * Constructs a new <code>FlrLowLevelSerializerImpl</code>.
     * 
     * @param config the configuration
     */
    public FlrLowLevelSerializerImpl(FlrLowLevelConfiguration config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    public void writeField(String value, int length, Align align, char padCharacter) {
        if (value.length() >= length) {
            writeString(value.substring(0, length));
        } else {
            if (align == Align.LEFT) {
                writeString(value);
                writePadCharacters(value, length, padCharacter);
            } else {
                writePadCharacters(value, length, padCharacter);
                writeString(value);
            }

        }
    }

    private void writePadCharacters(String value, int length, char padCharacter) {
        int padCount = length - value.length();
        for (int i = 0; i < padCount; i++) {
            writeChar(padCharacter);
        }
    }

}
