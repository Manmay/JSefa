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

package org.jsefa.flr.lowlevel;


/**
 * Factory for creating {@link FlrLowLevelDeserializer}s and
 * {@link FlrLowLevelSerializer}s.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface FlrLowLevelIOFactory {
    /**
     * Creates a <code>FlrLowLevelDeserializer</code> configured with the
     * given configuration object.
     * 
     * @param config the configuration object
     * @return a <code>FlrLowLevelDeserializer</code>
     */
    FlrLowLevelDeserializer createDeserializer(FlrLowLevelConfiguration config);

    /**
     * Creates a <code>FlrLowLevelSerializer</code> configured with the given
     * configuration object.
     * 
     * @param config the configuration object
     * @return a <code>FlrLowLevelSerializer</code>
     */
    FlrLowLevelSerializer createSerializer(FlrLowLevelConfiguration config);
}
