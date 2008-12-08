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

package org.jsefa.xml.lowlevel;

import java.io.Reader;

import org.jsefa.common.lowlevel.LowLevelDeserializationException;
import org.jsefa.common.lowlevel.LowLevelDeserializer;
import org.jsefa.xml.lowlevel.model.XmlItem;
import org.jsefa.xml.lowlevel.model.XmlItemType;

/**
 * Low level XML Deserializer.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlLowLevelDeserializer extends LowLevelDeserializer {

    /**
     * Opens a new deserialization stream based on the given reader and system id.
     * 
     * @param reader the reader to base the stream on.
     * @param systemId the system ID of the stream as an URI string. Will be used to resolve relative URIs.

     * @throws LowLevelDeserializationException
     */
    void open(Reader reader, String systemId);

    /**
     * Returns true if another xml item can be deserialized from the stream.
     * 
     * @return true, if another xml item can be deserialized from the stream, otherwise false.
     * @throws LowLevelDeserializationException
     */
    boolean hasNext();

    /**
     * Moves to the next xml item which can be deserialized.
     * @throws LowLevelDeserializationException
     */
    void moveToNext();

    /**
     * Returns the type of the current xml item. See {@link XmlItemType} for the different values allowed.
     * 
     * @return the type of the current xml item.
     */
    XmlItemType currentType();

    /**
     * Returns the current xml item.
     * 
     * @return the current xml item.
     * @throws LowLevelDeserializationException
     */
    XmlItem current();

    /**
     * Returns the current element depth.
     * <p>
     * The depth of the root element is 0.
     * 
     * @return the current depth.
     */
    int currentDepth();

}
