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

package org.jsefa;

import java.io.Writer;

import org.jsefa.common.lowlevel.LowLevelSerializer;

/**
 * Iterator-style interface for stream based serializers.
 * 
 * @author Norman Lahme-Huetig
 */
public interface Serializer {

    /**
     * Opens a new serialization stream based on the given writer.
     * 
     * @param writer the writer to base the stream on
     * @throws SerializationException
     */
    void open(Writer writer);

    /**
     * Writes the given object.
     * 
     * @param obj the object to write
     * @throws SerializationException
     */
    void write(Object obj);
    
    /**
     * Flushes the stream.
     */
    void flush();    

    /**
     * Closes the serialization stream. The underlying writer will be closed only if <code>closeWriter</code> is
     * true, too.
     * 
     * @param closeWriter if true, the underlying writer will be closed, too.
     * @throws SerializationException
     */
    void close(boolean closeWriter);
    
    /**
     * Returns the underlying low level serializer.
     * 
     * @return a low level serializer.
     */
    LowLevelSerializer getLowLevelSerializer();    
}
