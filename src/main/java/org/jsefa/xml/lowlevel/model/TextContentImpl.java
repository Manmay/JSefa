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

package org.jsefa.xml.lowlevel.model;

/**
 * Implementation of {@link TextContent}.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class TextContentImpl implements TextContent {

    private final String content;

    /**
     * Constructs a new <code>TextContentImpl</code>.
     * 
     * @param content the content
     */
    public TextContentImpl(String content) {
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return this.content;
    }

    /**
     * {@inheritDoc}
     */
    public XmlItemType getType() {
        return XmlItemType.TEXT_CONTENT;
    }

}
