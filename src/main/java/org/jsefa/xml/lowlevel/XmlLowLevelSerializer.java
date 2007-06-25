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

import java.io.Writer;

import org.jsefa.SerializationException;
import org.jsefa.xml.QName;

/**
 * Low level XML Serializer.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface XmlLowLevelSerializer {

    /**
     * Opens a new serialization stream based on the given writer.
     * 
     * @param writer the writer to base the stream on
     * @throws SerializationException
     */
    void open(Writer writer);

    /**
     * Writes the XML Declaration. Note that the encoding parameter does not set
     * the actual encoding of the underlying writer.
     * 
     * @param version version of the xml document
     * @param encoding encoding of the xml declaration
     * @throws SerializationException
     */
    void writeXmlDeclaration(String version, String encoding);

    /**
     * Writes the start tag of an element.
     * 
     * @param name the name of the element
     */
    void writeStartElement(QName name);

    /**
     * Writes the start tag of an element.
     * 
     * @param name the name of the element
     * @param dataTypeName the name of its data type
     */
    void writeStartElement(QName name, QName dataTypeName);

    /**
     * Writes an attribute with the given name and value.
     * 
     * @param name the attribute name
     * @param value the attribute value
     */
    void writeAttribute(QName name, String value);

    /**
     * Writes the given text as the content of the current element if the text
     * is not empty. Otherwise nothing happens.
     * 
     * @param text the text.
     */
    void writeText(String text);

    /**
     * Writes the end tag of the current element.
     */
    void writeEndElement();

    /**
     * Closes the serialization stream. The underlying writer will be closed
     * only if <code>closeWriter</code> is true, too.
     * 
     * @param closeWriter if true, the underlying writer will be closed, too.
     * @throws SerializationException
     */
    void close(boolean closeWriter);
}
