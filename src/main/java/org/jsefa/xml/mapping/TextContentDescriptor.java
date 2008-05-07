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

package org.jsefa.xml.mapping;

/**
 * Descriptor for the content of a non-simple xml element without child elements. There is only one instance of
 * this class.
 * <p>
 * The instance of this class is immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class TextContentDescriptor implements XmlNodeDescriptor {

    private static final TextContentDescriptor INSTANCE = new TextContentDescriptor();

    /**
     * Returns the single instance of this class.
     * 
     * @return the single <code>TextContentDescriptor</code>
     */
    public static TextContentDescriptor getInstance() {
        return TextContentDescriptor.INSTANCE;
    }

    private TextContentDescriptor() {

    }

    /**
     * {@inheritDoc}
     */
    public XmlNodeType getType() {
        return XmlNodeType.TEXT_CONTENT;
    }

}
