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

import java.io.Writer;

import org.jsefa.SerializationException;

/**
 * Low level RBF Serializer.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public interface RbfLowLevelSerializer {
    
    /**
     * Opens a new serialization stream based on the given writer.
     * 
     * @param writer the writer to base the stream on
     * @throws SerializationException
     */
    void open(Writer writer);
    
    /**
     * Finishes the current record.
     */
    void finishRecord();
    
    /**
     * Closes the serialization stream. The underlying writer will be closed
     * only if <code>closeWriter</code> is true, too.
     * 
     * @param closeWriter if true, the underlying writer will be closed, too.
     * @throws SerializationException
     */
    void close(boolean closeWriter);

}
