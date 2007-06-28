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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsefa.DeserializationException;
import org.jsefa.Deserializer;
import org.jsefa.SerializationException;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.rbf.lowlevel.RbfLowLevelDeserializer;
import org.jsefa.rbf.mapping.NodeModel;
import org.jsefa.rbf.mapping.NodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfEntryPoint;
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

    private RbfEntryPoint currentEntryPoint;

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
        this.currentEntryPoint = null;
        try {
            getLowLevelDeserializer().open(reader);
        } catch (Exception e) {
            throw new DeserializationException("Error while opening the deserialization stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean hasNext() {
        try {
            if (this.currentEntryPoint == null) {
                return moveToNextEntryPoint();
            } else {
                return true;
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public final Object next() {
        try {
            if (!hasNext()) {
                return null;
            }
            try {
                return readValue(getTypeMapping(this.currentEntryPoint.getDataTypeName()));
            } finally {
                this.currentEntryPoint = null;
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException(e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeReader) {
        try {
            getLowLevelDeserializer().close(closeReader);
        } catch (Exception e) {
            throw new SerializationException("Error while closing the serialization stream");
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
     * Returns the low level deserializer.
     * 
     * @return the low level deserializer.
     */
    protected abstract RbfLowLevelDeserializer getLowLevelDeserializer();

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
        boolean hasNonEmptyFields = readFields(object, typeMapping);
        boolean hasNonEmptySubRecords = readSubRecords(object, typeMapping);
        if (hasNonEmptyFields || hasNonEmptySubRecords) {
            return object;
        } else {
            return null;
        }
    }

    private boolean readFields(Object object, RbfComplexTypeMapping typeMapping) {
        boolean hasContent = false;
        for (String fieldName : typeMapping.getFieldNames(NodeType.FIELD)) {
            String fieldDataTypeName = typeMapping.getNodeModel(fieldName).getDataTypeName();
            Object fieldValue = readValue(getTypeMapping(fieldDataTypeName));
            if (fieldValue != null) {
                typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                hasContent = true;
            }
        }
        return hasContent;
    }

    private boolean readSubRecords(Object object, RbfComplexTypeMapping typeMapping) {
        boolean hasContent = false;
        if (!typeMapping.getFieldNames(NodeType.SUB_RECORD).isEmpty()) {
            boolean hasRecord = getLowLevelDeserializer().readNextRecord();
            for (String fieldName : typeMapping.getFieldNames(NodeType.SUB_RECORD)) {
                if (!hasRecord) {
                    break;
                }
                NodeModel subRecordNodeModel = typeMapping.getNodeModel(fieldName);
                TypeMapping subRecordTypeMapping = getTypeMapping(subRecordNodeModel.getDataTypeName());
                if (subRecordTypeMapping instanceof RbfComplexTypeMapping) {
                    if (subRecordNodeModel.getPrefix().equals(readPrefix())) {
                        Object fieldValue = readValue(subRecordTypeMapping);
                        if (fieldValue != null) {
                            typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                            hasContent = true;
                        }
                        hasRecord = getLowLevelDeserializer().readNextRecord();
                    }
                } else if (subRecordTypeMapping instanceof RbfListTypeMapping) {
                    List<Object> fieldValue = new ArrayList<Object>();
                    RbfListTypeMapping subRecordListTypeMapping = (RbfListTypeMapping) subRecordTypeMapping;
                    String prefix = readPrefix();
                    while (subRecordListTypeMapping.getPrefixes().contains(prefix)) {
                        TypeMapping listItemTypeMapping = getTypeMapping(subRecordListTypeMapping.getNodeModel(prefix)
                                .getDataTypeName());
                        Object listItemValue = readValue(listItemTypeMapping);
                        if (listItemValue != null) {
                            fieldValue.add(listItemValue);
                        }
                        if (getLowLevelDeserializer().readNextRecord()) {
                            prefix = readPrefix();
                        } else {
                            break;
                        }
                    }
                    if (!fieldValue.isEmpty()) {
                        typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                        hasContent = true;
                    }
                }
            }
            getLowLevelDeserializer().unreadRecord();
        }
        return hasContent;
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
            while (getLowLevelDeserializer().readNextRecord()) {
                String prefix = readPrefix();
                this.currentEntryPoint = this.entryPointsByPrefix.get(prefix);
                if (this.currentEntryPoint != null) {
                    return true;
                }
            }
            return false;
        } else {
            if (getLowLevelDeserializer().readNextRecord()) {
                this.currentEntryPoint = this.entryPoint;
                return true;
            } else {
                this.currentEntryPoint = null;
                return false;
            }
        }
    }

}
