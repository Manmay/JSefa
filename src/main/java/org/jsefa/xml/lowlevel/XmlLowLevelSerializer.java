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

import org.jsefa.common.lowlevel.LowLevelSerializationException;
import org.jsefa.common.lowlevel.LowLevelSerializer;
import org.jsefa.xml.namespace.QName;

/**
 * Low level XML Serializer.
 * 
 * @author Norman Lahme-Huetig
 * @author Matthias Derer
 * 
 */
public interface XmlLowLevelSerializer extends LowLevelSerializer {

    /**
     * Writes the XML Declaration. Note that the encoding parameter does not set the actual encoding of the
     * underlying writer.
     * 
     * @param version version of the xml document
     * @param encoding encoding of the xml declaration
     * @throws LowLevelSerializationException
     */
    void writeXmlDeclaration(String version, String encoding);
    
    /**
     * Writes the DOCTYPE declaration.
     * @param rootElementName the name of the root element.
     * @param publicId the public id - may be null
     * @param systemId the system id
     */
    void writeDocTypeDeclaration(QName rootElementName, String publicId, String systemId);

    /**
     * Writes the start tag of an element.
     * 
     * @param name the name of the element
     * @throws LowLevelSerializationException
     */
    void writeStartElement(QName name);

    /**
     * Writes the start tag of an element.
     * 
     * @param name the name of the element
     * @param dataTypeName the name of its data type
     * @throws LowLevelSerializationException
     */
    void writeStartElement(QName name, QName dataTypeName);

    /**
     * Writes an attribute with the given name and value.
     * 
     * @param name the attribute name
     * @param value the attribute value
     * @throws LowLevelSerializationException
     */
    void writeAttribute(QName name, String value);

    /**
     * Writes the given text as the content of the current element if the text is not empty. Otherwise nothing
     * happens.
     * 
     * @param text the text.
     * @param textMode the text mode (IMPLICIT/CDATA).
     * @throws LowLevelSerializationException
     */
    void writeText(String text, TextMode textMode);

    /**
     * Writes the end tag of the current element.
     * @throws LowLevelSerializationException
     */
    void writeEndElement();

}
