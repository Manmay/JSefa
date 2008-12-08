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

package org.jsefa.common.lowlevel;

import java.io.Reader;

/**
 * Interface for stream based low level deserializers.
 * 
 * @author Norman Lahme-Huetig
 */
public interface LowLevelDeserializer {
    /**
     * Opens a new deserialization stream based on the given reader.
     * 
     * @param reader the reader to base the stream on.
     * @throws LowLevelDeserializationException
     */
    void open(Reader reader);

    /**
     * Closes the deserialization stream. The underlying reader will be closed only if <code>closeReader</code>
     * is true.
     * 
     * @param closeReader if true, the underlying reader will be closed, too.
     * @throws LowLevelDeserializationException
     */
    void close(boolean closeReader);
    
    /**
     * Returns information about the current position within the input stream.
     * @return the input position or null if no position information is available
     */
    InputPosition getInputPosition();
}
