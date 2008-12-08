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

package org.jsefa.xml;

import java.io.Reader;

import org.jsefa.DeserializationException;
import org.jsefa.Deserializer;

/**
 * Iterator-style interface for stream based xml deserializer.
 * 
 * @see Deserializer
 * @author Norman Lahme-Huetig
 */

public interface XmlDeserializer extends Deserializer {
    
    /**
     * Opens a new deserialization stream based on the given reader and system id.
     * 
     * @param reader the reader to base the stream on.
     * @param systemId the system ID of the stream as an URI string. Will be used to resolve relative URIs.

     * @throws DeserializationException
     */
    void open(Reader reader, String systemId);
}
