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

import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;

/**
 * XmlPull-based implementation of {@link XmlLowLevelIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Sebastian Hoß
 * @author Norman Lahme-Huetig
 * 
 */
public class XmlPullBasedXmlLowLevelIOFactory extends XmlLowLevelIOFactory {

    private final XmlLowLevelConfiguration config;

    /**
     * Creates a new <code>XmlPullBasedXmlLowLevelIOFactory</code> for <code>XmlLowLevelSerializer</code>s and
     * <code>XmlLowLevelDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return a <code>XmlPullBasedXmlLowLevelIOFactory</code> factory
     */
    public static XmlPullBasedXmlLowLevelIOFactory createFactory(final XmlLowLevelConfiguration config) {
        return new XmlPullBasedXmlLowLevelIOFactory(config);
    }

    XmlPullBasedXmlLowLevelIOFactory(final XmlLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     */
    public XmlLowLevelDeserializer createDeserializer() {
        return new XmlPullBasedXmlLowLevelDeserializer(this.config);
    }

    /**
     * {@inheritDoc}
     */
    public XmlLowLevelSerializer createSerializer() {
        return new XmlPullBasedXmlLowLevelSerializer(this.config);
    }



}
