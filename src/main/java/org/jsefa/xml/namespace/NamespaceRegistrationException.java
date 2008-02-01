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

package org.jsefa.xml.namespace;

/**
 * Exception thrown in case of a namespace registration error.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class NamespaceRegistrationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>NamespaceRegistrationException</code> with the specified detail message.
     * 
     * @param message the detail message.
     */
    public NamespaceRegistrationException(String message) {
        super(message);
    }

    /**
     * Constructs a new <code>NamespaceRegistrationException</code> with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public NamespaceRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
