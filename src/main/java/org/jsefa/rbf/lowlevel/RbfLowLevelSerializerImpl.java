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

package org.jsefa.rbf.lowlevel;

import java.io.IOException;
import java.io.Writer;

import org.jsefa.SerializationException;

/**
 * Abstract implementation of {@link RbfLowLevelSerializer}.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class RbfLowLevelSerializerImpl implements RbfLowLevelSerializer {
    private final String lineBreak;

    private Writer writer;
    
    /**
     * Constructs a new <code>RbfLowLevelSerializerImpl</code>.
     * @param lineBreak the line break to use
     */
    public RbfLowLevelSerializerImpl(String lineBreak) {
        this.lineBreak = lineBreak;
    }

    /**
     * {@inheritDoc}
     */
    public final void open(Writer writer) {
        this.writer = writer;
        afterOpen();
    }

    /**
     * {@inheritDoc}
     */
    public final void finishRecord() {
        beforeFinishRecord();
        writeNewLine();
    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeWriter) {
        if (closeWriter) {
            try {
                this.writer.close();
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }
    }
    
    /**
     * Called after opening a new input stream. Override to perform extra action.
     */
    protected void afterOpen() {
        
    }
    
    /**
     * Called before a record is finished. Override to perform extra action.
     */
    protected void beforeFinishRecord() {
        
    }

    /**
     * Writes the given character to the stream.
     * 
     * @param character the character.
     */
    protected final void writeChar(int character) {
        try {
            this.writer.write(character);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Writes the given <code>String</code> value as it is to the stream.
     * 
     * @param value the <code>String</code> value.
     */
    protected final void writeString(String value) {
        try {
            this.writer.write(value);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
    
    private void writeNewLine() {
        try {
            this.writer.write(this.lineBreak);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }    
    

}
