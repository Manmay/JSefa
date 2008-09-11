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

import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

import org.jsefa.common.lowlevel.LowLevelSerializationException;
import org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration;

/**
 * Abstract implementation of {@link RbfLowLevelSerializer}.
 * 
 * @param <C> the type of the RbfLowLevelConfiguration
 * @author Norman Lahme-Huetig
 * 
 */
public class RbfLowLevelSerializerImpl<C extends RbfLowLevelConfiguration> implements RbfLowLevelSerializer {

    private C config;

    private Writer writer;

    /**
     * Constructs a new <code>RbfLowLevelSerializerImpl</code>.
     * 
     * @param config the configuration object
     */
    public RbfLowLevelSerializerImpl(C config) {
        this.config = config;
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
        writeRecordDelimiter();
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeLine(String line) {
        writeString(line);
        writeLineBreak();
    }
    
    /**
     * {@inheritDoc}
     */
    public void flush() {
        try {
            ((Flushable) this.writer).flush();
        } catch (IOException e) {
            throw new LowLevelSerializationException("Error while flushing the serialization stream", e);
        } catch (ClassCastException e) {
            return;
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeWriter) {
        if (closeWriter) {
            try {
                this.writer.close();
            } catch (IOException e) {
                throw new LowLevelSerializationException("Error while closing the serialization stream", e);
            }
        }
    }

    /**
     * Returns the configuration object.
     * @return the configuration object
     */
    protected final C getConfiguration() {
        return this.config;
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
            throw new LowLevelSerializationException(e);
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
            throw new LowLevelSerializationException(e);
        }
    }

    private void writeLineBreak() {
        try {
            this.writer.write(this.config.getLineBreak());
        } catch (IOException e) {
            throw new LowLevelSerializationException(e);
        }
    }
    
    private void writeRecordDelimiter() {
        if (this.config.getSpecialRecordDelimiter() != null) {
            try {
                this.writer.write(this.config.getSpecialRecordDelimiter());
            } catch (IOException e) {
                throw new LowLevelSerializationException(e);
            }
        } else {
            writeLineBreak();
        }
    }

}
