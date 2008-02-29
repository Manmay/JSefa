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
 * Exception thrown when an error occurs during serialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class SerializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_MESSAGE = "Error while serializing";
    
    /**
     * Constructs a new <code>SerializationException</code> with the specified detail message.
     * 
     * @param message the detail message.
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new <code>SerializationException</code> with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new <code>SerializationException</code> with the default detail message and cause.
     * 
     * @param cause the cause
     */
    public SerializationException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
