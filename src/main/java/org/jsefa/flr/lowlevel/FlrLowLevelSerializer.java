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

import org.jsefa.common.lowlevel.LowLevelSerializationException;
import org.jsefa.rbf.lowlevel.RbfLowLevelSerializer;

/**
 * Low level FLR Serializer.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface FlrLowLevelSerializer extends RbfLowLevelSerializer {
    /**
     * Writes the next field.
     * 
     * @param value the field value
     * @param length the length of the field
     * @param align the alignment
     * @param padCharacter the pad character
     * @throws LowLevelSerializationException
     */
    void writeField(String value, int length, Align align, char padCharacter);
}
