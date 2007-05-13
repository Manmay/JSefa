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

package org.jsefa;

/**
 * Factory for creating {@link Serializer}s and {@link Deserializer}s.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface IOFactory {
    /**
     * Creates a new <code>Serializer</code>.
     * 
     * @return a <code>Serializer</code>
     */
    Serializer createSerializer();

    /**
     * Creates a new <code>Deserializer</code>.
     * 
     * @return a new <code>Deserializer</code>
     */
    Deserializer createDeserializer();

}
