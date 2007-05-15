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

package org.jsefa.rbf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsefa.DeserializationException;
import org.jsefa.Deserializer;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.rbf.mapping.NodeModel;
import org.jsefa.rbf.mapping.NodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Abstract implementation of {@link Deserializer} for RBF types.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class RbfDeserializer implements Deserializer {
    private final RbfTypeMappingRegistry typeMappingRegistry;

    private final Map<String, RbfEntryPoint> entryPointsByPrefix;

    private final RbfEntryPoint entryPoint;

    private final boolean withPrefix;

    private BufferedReader reader;

    private RbfEntryPoint currentEntryPoint;

    private String currentLine;

    private boolean linePrefetched;

    private int currentIndex;

    /**
     * Constructs a new <code>AbstractRbfDeserializer</code>.
     * 
     * @param typeMappingRegistry the type mapping registry.
     * @param entryPointsByPrefixes a map which maps prefixes to entry points
     */
    protected RbfDeserializer(RbfTypeMappingRegistry typeMappingRegistry,
            Map<String, RbfEntryPoint> entryPointsByPrefixes) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.entryPointsByPrefix = entryPointsByPrefixes;
        this.withPrefix = true;
        this.entryPoint = null;
    }

    /**
     * Constructs a new <code>AbstractRbfDeserializer</code>.
     * 
     * @param typeMappingRegistry the type mapping registry
     * @param entryPoint the entry point
     */
    protected RbfDeserializer(RbfTypeMappingRegistry typeMappingRegistry, RbfEntryPoint entryPoint) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.entryPoint = entryPoint;
        this.withPrefix = false;
        this.entryPointsByPrefix = null;
    }

    /**
     * {@inheritDoc}
     */
    public final void open(Reader reader) {
        this.currentLine = null;
        this.currentEntryPoint = null;
        this.linePrefetched = false;
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean hasNext() {
        if (this.currentEntryPoint == null) {
            return moveToNextEntryPoint();
        } else {
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final Object next() {
        if (!hasNext()) {
            return null;
        }
        try {
            return readValue(getTypeMapping(this.currentEntryPoint.getDataTypeName()));
        } finally {
            this.currentEntryPoint = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeReader) {
        if (closeReader) {
            try {
                this.reader.close();
            } catch (IOException e) {
                throw new DeserializationException(e);
            }
        }
    }

    /**
     * Reads a simple value from the stream using the given type mapping.
     * 
     * @param typeMapping the type mapping
     * @return a simple value
     */
    protected abstract Object readSimpleValue(SimpleTypeMapping typeMapping);

    /**
     * Reads the prefix of the current record from the stream.
     * 
     * @return a prefix
     */
    protected abstract String readPrefix();

    /**
     * Reads the next line.
     * 
     * @return the next line.
     */
    protected final boolean readNextLine() {
        this.currentIndex = 0;
        if (this.linePrefetched) {
            this.linePrefetched = false;
            return this.currentLine != null;
        }
        try {
            this.currentLine = this.reader.readLine();
            while (this.currentLine != null && this.currentLine.trim().length() == 0) {
                this.currentLine = this.reader.readLine();
            }
            return this.currentLine != null;
        } catch (IOException e) {
            throw new DeserializationException(e);
        }
    }

    /**
     * Returns true, if there is another character on the current line to read.
     * 
     * @return true, if there is another character on the current line to read;
     *         false otherwise.
     */
    protected final boolean hasNextChar() {
        return this.currentIndex < this.currentLine.length();
    }

    /**
     * Returns the next character of the current line and moves one character
     * forward.
     * 
     * @return a character
     */
    protected final char nextChar() {
        try {
            return this.currentLine.charAt(this.currentIndex++);
        } catch (IndexOutOfBoundsException e) {
            throw new DeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the next <code>String</code> with the given length of the
     * current line and moves forward accordingly.
     * 
     * @param length the length of the <code>String</code> to return
     * @return a <code>String</code>
     */
    protected final String nextString(int length) {
        try {
            String value = this.currentLine.substring(this.currentIndex, this.currentIndex + length);
            this.currentIndex += length;
            return value;
        } catch (IndexOutOfBoundsException e) {
            throw new DeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the current character of the current line.
     * 
     * @return a character
     */
    protected final char peekChar() {
        try {
            return this.currentLine.charAt(this.currentIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new DeserializationException("Unexpected end of line reached");
        }
    }

    /**
     * Returns the number of remaining characters in the current line.
     * 
     * @return the number of remaining characters in the current line.
     */
    protected final int remainingLineLength() {
        return this.currentLine.length() - this.currentIndex;
    }

    private Object readValue(TypeMapping typeMapping) {
        if (typeMapping instanceof SimpleTypeMapping) {
            return readSimpleValue((SimpleTypeMapping) typeMapping);
        } else if (typeMapping instanceof RbfComplexTypeMapping) {
            return readComplexValue((RbfComplexTypeMapping) typeMapping);
        } else {
            throw new UnsupportedOperationException("Unknown type mapping type");
        }
    }

    private Object readComplexValue(RbfComplexTypeMapping typeMapping) {
        Object object = typeMapping.getObjectAccessor().createObject();
        boolean isEmpty = true;
        for (String fieldName : typeMapping.getFieldNames(NodeType.FIELD)) {
            String fieldDataTypeName = typeMapping.getNodeModel(fieldName).getDataTypeName();
            Object fieldValue = readValue(getTypeMapping(fieldDataTypeName));
            if (fieldValue != null) {
                typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
            }
            isEmpty = isEmpty && (fieldValue == null);
        }
        if (!typeMapping.getFieldNames(NodeType.SUB_RECORD).isEmpty()) {
            readNextLine();
            for (String fieldName : typeMapping.getFieldNames(NodeType.SUB_RECORD)) {
                if (this.currentLine == null) {
                    break;
                }
                NodeModel subRecordNodeModel = typeMapping.getNodeModel(fieldName);
                TypeMapping subRecordTypeMapping = getTypeMapping(subRecordNodeModel.getDataTypeName());
                if (subRecordTypeMapping instanceof RbfComplexTypeMapping) {
                    if (subRecordNodeModel.getPrefix().equals(getPrefix())) {
                        Object fieldValue = readValue(subRecordTypeMapping);
                        if (fieldValue != null) {
                            typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                        }
                        isEmpty = isEmpty && (fieldValue == null);
                        readNextLine();
                    }
                } else if (subRecordTypeMapping instanceof RbfListTypeMapping) {
                    List<Object> fieldValue = new ArrayList<Object>();
                    RbfListTypeMapping subRecordListTypeMapping = (RbfListTypeMapping) subRecordTypeMapping;
                    while (subRecordListTypeMapping.getPrefixes().contains(getPrefix())) {
                        TypeMapping listItemTypeMapping = getTypeMapping(subRecordListTypeMapping.getNodeModel(
                                getPrefix()).getDataTypeName());
                        Object listItemValue = readValue(listItemTypeMapping);
                        if (listItemValue != null) {
                            fieldValue.add(listItemValue);
                        }
                        if (!readNextLine()) {
                            break;
                        }
                    }
                    if (!fieldValue.isEmpty()) {
                        typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                    } else {
                        isEmpty = true;
                    }
                }
            }
            this.linePrefetched = true;
        }
        if (!isEmpty) {
            return object;
        } else {
            return null;
        }
    }

    private TypeMapping getTypeMapping(String dataTypeName) {
        TypeMapping typeMapping = this.typeMappingRegistry.get(dataTypeName);
        if (typeMapping == null) {
            throw new DeserializationException("Unknown data type name: " + dataTypeName);
        }
        return typeMapping;

    }

    private boolean moveToNextEntryPoint() {
        if (this.withPrefix) {
            this.currentEntryPoint = null;
            while (readNextLine()) {
                String prefix = getPrefix();
                this.currentEntryPoint = this.entryPointsByPrefix.get(prefix);
                if (this.currentEntryPoint != null) {
                    return true;
                }
            }
            return false;
        } else {
            if (readNextLine()) {
                this.currentEntryPoint = this.entryPoint;
                return true;
            } else {
                return false;
            }
        }
    }

    private String getPrefix() {
        this.currentIndex = 0;
        return readPrefix();
    }

}
