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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jsefa.DeserializationException;
import org.jsefa.ObjectPathElement;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.common.lowlevel.InputPosition;
import org.jsefa.common.lowlevel.filter.Line;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.validator.ValidationException;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
import org.jsefa.rbf.config.RbfConfiguration;
import org.jsefa.rbf.lowlevel.RbfLowLevelDeserializer;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfFieldDescriptor;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfNodeMapping;
import org.jsefa.rbf.mapping.RbfNodeType;
import org.jsefa.rbf.mapping.RecordDescriptor;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;
import org.jsefa.rbf.mapping.RecordMapping;

/**
 * Abstract implementation of {@link RbfDeserializer} for RBF types.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public abstract class RbfDeserializerImpl implements RbfDeserializer {
    private final RbfTypeMappingRegistry typeMappingRegistry;

    private final Map<String, RbfEntryPoint> entryPointsByPrefix;

    private final RbfEntryPoint entryPoint;

    private final boolean withPrefix;

    private RbfEntryPoint currentEntryPoint;

    private boolean validate;

    /**
     * Constructs a new <code>AbstractRbfDeserializer</code>.
     * 
     * @param config the configuration
     * @param entryPointsByPrefixes a map which maps prefixes to entry points
     */
    protected RbfDeserializerImpl(RbfConfiguration<?> config, Map<String, RbfEntryPoint> entryPointsByPrefixes) {
        this.typeMappingRegistry = config.getTypeMappingRegistry();
        this.entryPointsByPrefix = entryPointsByPrefixes;
        this.withPrefix = true;
        this.entryPoint = null;
        this.validate = config.getValidationMode().equals(ValidationMode.DESERIALIZATION)
                || config.getValidationMode().equals(ValidationMode.BOTH);
    }

    /**
     * Constructs a new <code>AbstractRbfDeserializer</code>.
     * 
     * @param config the configuration
     * @param entryPoint the entry point
     */
    protected RbfDeserializerImpl(RbfConfiguration<?> config, RbfEntryPoint entryPoint) {
        this.typeMappingRegistry = config.getTypeMappingRegistry();
        this.entryPoint = entryPoint;
        this.withPrefix = false;
        this.entryPointsByPrefix = null;
        this.validate = config.getValidationMode().equals(ValidationMode.DESERIALIZATION)
                || config.getValidationMode().equals(ValidationMode.BOTH);
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
            throw new DeserializationException(e).setInputPosition(getInputPosition());
        }

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final <T> T next() {
        try {
            if (!hasNext()) {
                return null;
            }
            try {
                T result = (T) readValue(getTypeMapping(this.currentEntryPoint.getDataTypeName()));
                if (this.validate && result != null) {
                    assertValueIsValid(result, this.currentEntryPoint);
                }
                return result;
            } finally {
                this.currentEntryPoint = null;
            }
        } catch (DeserializationException e) {
            throw e;
        } catch (Exception e) {
            throw new DeserializationException(e).setInputPosition(getInputPosition());
        }

    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeReader) {
        try {
            getLowLevelDeserializer().close(closeReader);
        } catch (Exception e) {
            throw new DeserializationException("Error while closing the serialization stream", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final InputPosition getInputPosition() {
        return getLowLevelDeserializer().getInputPosition();
    }

    /**
     * {@inheritDoc}
     */
    public List<Line> getStoredLines() {
        return getLowLevelDeserializer().getStoredLines();
    }

    /**
     * Reads a simple value from the stream using the given type mapping.
     * 
     * @param typeMapping the type mapping
     * @return a simple value
     */
    protected abstract Object readSimpleValue(SimpleTypeMapping<?> typeMapping);

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

    private Object readValue(TypeMapping<?> typeMapping) {
        if (typeMapping instanceof SimpleTypeMapping) {
            return readSimpleValue((SimpleTypeMapping<?>) typeMapping);
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
        int relativeIndex = 0;
        while (true) {
            String fieldName = null;
            try {
                RbfNodeMapping<?> nodeMapping = typeMapping.getNodeMapping(new RbfFieldDescriptor(relativeIndex++));
                if (nodeMapping == null) {
                    break;
                }
                fieldName = nodeMapping.getFieldDescriptor().getName();
                Object fieldValue = readValue(getTypeMapping(nodeMapping.getDataTypeName()));
                if (fieldValue != null) {
                    typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                    hasContent = true;
                }
            } catch (Exception e) {
                throw createException(e, typeMapping, fieldName);
            }
        }
        return hasContent;
    }

    @SuppressWarnings("unchecked")
    private boolean readSubRecords(Object object, RbfComplexTypeMapping typeMapping) {
        if (typeMapping.getFieldNames(RbfNodeType.RECORD).isEmpty() || !getLowLevelDeserializer().readNextRecord()) {
            return false;
        }
        boolean hasContent = false;
        RecordDescriptor recordDescriptor = new RecordDescriptor(readPrefix());
        do {
            RecordMapping subRecordNodeMapping = typeMapping.getNodeMapping(recordDescriptor);
            if (subRecordNodeMapping == null) {
                break;
            }
            TypeMapping<?> subRecordTypeMapping = getTypeMapping(subRecordNodeMapping.getDataTypeName());
            String fieldName = subRecordNodeMapping.getFieldDescriptor().getName();
            try {
                if (subRecordTypeMapping instanceof RbfComplexTypeMapping) {
                    Object fieldValue = readValue(subRecordTypeMapping);
                    if (fieldValue != null) {
                        typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                        hasContent = true;
                    }
                    if (!getLowLevelDeserializer().readNextRecord()) {
                        return hasContent;
                    }
                    recordDescriptor = new RecordDescriptor(readPrefix());
                } else if (subRecordTypeMapping instanceof RbfListTypeMapping) {
                    RbfListTypeMapping subRecordListTypeMapping = (RbfListTypeMapping) subRecordTypeMapping;
                    Collection<Object> fieldValue = (Collection<Object>) subRecordListTypeMapping
                            .getObjectAccessor().createObject();
                    boolean hasRecord = true;
                    while (hasRecord) {
                        RecordMapping listItemRecordMapping = subRecordListTypeMapping
                                .getNodeMapping(recordDescriptor);
                        if (listItemRecordMapping == null) {
                            break;
                        }
                        TypeMapping<?> listItemTypeMapping = getTypeMapping(listItemRecordMapping.getDataTypeName());
                        Object listItemValue = readValue(listItemTypeMapping);
                        if (listItemValue != null) {
                            fieldValue.add(listItemValue);
                        }
                        hasRecord = getLowLevelDeserializer().readNextRecord();
                        if (hasRecord) {
                            recordDescriptor = new RecordDescriptor(readPrefix());
                        }
                    }
                    if (!fieldValue.isEmpty()) {
                        typeMapping.getObjectAccessor().setValue(object, fieldName, fieldValue);
                        hasContent = true;
                    }
                    if (!hasRecord) {
                        return hasContent;
                    }
                }
            } catch (Exception e) {
                throw createException(e, typeMapping, fieldName);
            }
        } while (hasContent);
        
        getLowLevelDeserializer().unreadRecord();
        return hasContent;
    }

    private TypeMapping<?> getTypeMapping(String dataTypeName) {
        TypeMapping<?> typeMapping = this.typeMappingRegistry.get(dataTypeName);
        if (typeMapping == null) {
            throw new DeserializationException("Unknown data type name: " + dataTypeName)
                    .setInputPosition(getInputPosition());
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

    private DeserializationException createException(Exception cause, TypeMapping<?> typeMapping, String fieldName) {
        ObjectPathElement elem = new ObjectPathElement(typeMapping.getObjectType(), fieldName);
        if (cause instanceof DeserializationException) {
            return ((DeserializationException) cause).add(elem);
        } else {
            return new DeserializationException(cause).setInputPosition(getInputPosition()).add(elem);
        }
    }

    private void assertValueIsValid(Object object, RbfEntryPoint entryPoint) {
        Validator validator = entryPoint.getValidator();
        if (validator != null) {
            ValidationResult result = validator.validate(object);
            if (!result.isValid()) {
                throw new ValidationException(result);
            }
        }
    }
}
