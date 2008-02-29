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

import java.util.LinkedList;
import java.util.List;

import org.jsefa.common.lowlevel.InputPosition;

/**
 * Exception thrown when an error occurs during deserialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class DeserializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_MESSAGE = "Error while deserializing";
    
    private InputPosition inputPosition;
    
    private LinkedList<ObjectPathElement> objectPath;

    /**
     * Constructs a new <code>DeserializationException</code> with the specified detail message.
     * 
     * @param message the detail message.
     */
    public DeserializationException(String message) {
        super(message);
        this.objectPath = new LinkedList<ObjectPathElement>();
    }

    /**
     * Constructs a new <code>DeserializationException</code> with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
        this.objectPath = new LinkedList<ObjectPathElement>();
    }
    
    /**
     * Constructs a new <code>DeserializationException</code> with the default detail message and cause.
     * 
     * @param cause the cause
     */
    public DeserializationException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
        this.objectPath = new LinkedList<ObjectPathElement>();
    }

    /**
     * Sets the position within the input stream where the error occurred.
     * @param inputPosition the input position
     * @return this
     */
    public DeserializationException setInputPosition(InputPosition inputPosition) {
        this.inputPosition = inputPosition;
        return this;
    }
    
    /**
     * Adds the given object path element to the front of the object path.
     * @param element an object path element
     * @return this
     */
    public DeserializationException add(ObjectPathElement element) {
        this.objectPath.addFirst(element);
        return this;
    }

    /**
     * Returns the position within the input stream where the error occurred.
     * @return an input position
     */
    public InputPosition getInputPosition() {
        return this.inputPosition;
    }
    
    /**
     * Returns the object path.
     * @return the object path
     */
    public List<ObjectPathElement> getObjectPath() {
        return this.objectPath;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        StringBuilder result = new StringBuilder(super.getMessage());
        result.append("\nPosition: ");
        result.append(this.inputPosition);
        if (!this.objectPath.isEmpty()) {
            result.append("\nObject Path: ");
            for (int i = 0; i < this.objectPath.size(); i++) {
                ObjectPathElement elem = this.objectPath.get(i);
                if (i > 0) {
                    result.append(".");
                }
                result.append(elem);
            }
        }
        return result.toString();
    }

}
