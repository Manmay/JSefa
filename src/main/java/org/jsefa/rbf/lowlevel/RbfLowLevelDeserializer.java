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

package org.jsefa.rbf.lowlevel;

import java.util.List;

import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.common.lowlevel.LowLevelDeserializer;
import org.jsefa.common.lowlevel.filter.Line;
import org.jsefa.common.lowlevel.filter.LineFilter;

/**
 * Low level RBF Deserializer.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface RbfLowLevelDeserializer extends LowLevelDeserializer {

    /**
     * Reads the next record from the stream. Returns true, if it could be read and false otherwise.
     * 
     * @return true, if another record could be read from the stream, otherwise false.
     * @throws LowLevelDeserializationException
     */
    boolean readNextRecord();

    /**
     * Unreads the already read record so that it can be read again with {@link #readNextRecord()}.
     */
    void unreadRecord();
    
    /**
     * Returns a list of lines stored during deserialization.
     * @see LineFilter
     * @return a list of lines
     */
    List<Line> getStoredLines();
}
