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

package org.jsefa.xml.lowlevel.stax;

import org.jsefa.xml.lowlevel.XmlLowLevelDeserializer;
import org.jsefa.xml.lowlevel.XmlLowLevelIOFactory;
import org.jsefa.xml.lowlevel.XmlLowLevelSerializer;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;

/**
 * Stax-based implementation of {@link XmlLowLevelIOFactory}.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class StaxBasedXmlLowLevelIOFactory implements XmlLowLevelIOFactory {

    private static final StaxBasedXmlLowLevelIOFactory INSTANCE = new StaxBasedXmlLowLevelIOFactory();

    /**
     * Returns the single <code>StaxDriver</code>.
     * 
     * @return the single <code>StaxDriver</code>.
     */
    public static StaxBasedXmlLowLevelIOFactory getInstance() {
        return StaxBasedXmlLowLevelIOFactory.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public XmlLowLevelDeserializer createDeserializer(XmlLowLevelConfiguration config) {
        return new StaxBasedXmlLowLevelDeserializer(config);
    }

    /**
     * {@inheritDoc}
     */
    public XmlLowLevelSerializer createSerializer(XmlLowLevelConfiguration config) {
        return new StaxBasedXmlLowLevelSerializer(config);
    }

}
