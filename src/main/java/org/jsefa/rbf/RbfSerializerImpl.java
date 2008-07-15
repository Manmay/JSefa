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

import java.io.Writer;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jsefa.SerializationException;
import org.jsefa.Serializer;
import org.jsefa.common.config.ValidationMode;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.util.ReflectionUtil;
import org.jsefa.common.validator.ValidationException;
import org.jsefa.common.validator.ValidationResult;
import org.jsefa.common.validator.Validator;
import org.jsefa.rbf.config.RbfConfiguration;
import org.jsefa.rbf.lowlevel.RbfLowLevelSerializer;
import org.jsefa.rbf.mapping.RbfNodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfNodeMapping;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;
import org.jsefa.rbf.mapping.RecordMapping;

/**
 * Abstract implementation of {@link Serializer} for RBF formats.
 * 
 * @param <L> the type of the RbfLowLevelSerializer
 * @author Norman Lahme-Huetig
 */
public abstract class RbfSerializerImpl<L extends RbfLowLevelSerializer> implements RbfSerializer {
    private final RbfTypeMappingRegistry typeMappingRegistry;

    private final Map<Class<?>, RbfEntryPoint> entryPoints;

    private final boolean withPrefix;

    private final IdentityHashMap<Object, Object> complexObjectsOnPath;

    private L lowLevelSerializer;

    private boolean validate;
    
    /**
     * Constructs a new <code>RbfSerializerImpl</code>.
     * 
     * @param config the configuration
     * @param entryPoints a map which maps object types to entry points.
     * @param lowLevelSerializer the low level serializer
     */
    protected RbfSerializerImpl(RbfConfiguration<?> config, Map<Class<?>, RbfEntryPoint> entryPoints,
            L lowLevelSerializer) {
        this.typeMappingRegistry = config.getTypeMappingRegistry();
        this.entryPoints = entryPoints;
        this.withPrefix = (entryPoints.values().iterator().next().getDesignator().length() > 0);
        this.complexObjectsOnPath = new IdentityHashMap<Object, Object>();
        this.lowLevelSerializer = lowLevelSerializer;
        this.validate = config.getValidationMode().equals(ValidationMode.SERIALIZATION)
        || config.getValidationMode().equals(ValidationMode.BOTH);
    }

    /**
     * {@inheritDoc}
     */
    public final void open(Writer writer) {
        this.complexObjectsOnPath.clear();
        try {
            getLowLevelSerializer().open(writer);
        } catch (Exception e) {
            throw new SerializationException("Error while opening the serialization stream", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void write(Object object) {
        if (object == null) {
            return;
        }
        try {
            RbfEntryPoint entryPoint = getEntryPoint(object.getClass());
            if (this.validate) {
                assertValueIsValid(object, entryPoint);
            }
            if (this.withPrefix) {
                writePrefix(entryPoint.getDesignator());
            }
            writeValue(object, getTypeMapping(entryPoint.getDataTypeName()));
            getLowLevelSerializer().finishRecord();
        } catch (SerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void flush() {
        try {
            this.lowLevelSerializer.flush();
        } catch (Exception e) {
            throw new SerializationException("Error while flushing the serialization stream");
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void close(boolean closeWriter) {
        try {
            getLowLevelSerializer().close(closeWriter);
        } catch (Exception e) {
            throw new SerializationException("Error while closing the serialization stream", e);
        }

    }

    /**
     * Returns a low level serializer.
     * 
     * @return a low level serializer.
     */
    public L getLowLevelSerializer() {
        return this.lowLevelSerializer;
    }

    /**
     * Writes the given value as a simple value using the simple type mapping.
     * 
     * @param value the value to write
     * @param mapping the simple type mapping
     */
    protected abstract void writeSimpleValue(Object value, SimpleTypeMapping<?> mapping);

    /**
     * Writes the prefix.
     * 
     * @param prefix the prefix
     */
    protected abstract void writePrefix(String prefix);

    private void writeValue(Object object, TypeMapping<?> typeMapping) {
        if (typeMapping instanceof SimpleTypeMapping) {
            writeSimpleValue(object, (SimpleTypeMapping<?>) typeMapping);
        } else if (typeMapping instanceof RbfComplexTypeMapping) {
            writeComplexValue(object, (RbfComplexTypeMapping) typeMapping);
        } else {
            throw new UnsupportedOperationException("Unknown type mapping type");
        }
    }

    private void writeComplexValue(Object object, RbfComplexTypeMapping typeMapping) {
        if (this.complexObjectsOnPath.containsKey(object)) {
            throw new SerializationException("Cycle detected while serializing " + object);
        } else if (object != null) {
            this.complexObjectsOnPath.put(object, object);
        }
        writeFields(object, typeMapping);
        if (object != null) {
            writeSubRecords(object, typeMapping);
            this.complexObjectsOnPath.remove(object);
        }
    }

    private void writeFields(Object object, RbfComplexTypeMapping typeMapping) {
        for (String fieldName : typeMapping.getFieldNames(RbfNodeType.FIELD)) {
            Object fieldValue = null;
            if (object != null) {
                fieldValue = typeMapping.getObjectAccessor().getValue(object, fieldName);
            }
            RbfNodeMapping<?> nodeMapping = typeMapping.getNodeMapping(fieldName, Object.class);
            String fieldDataTypeName = nodeMapping.getDataTypeName();
            writeValue(fieldValue, getTypeMapping(fieldDataTypeName));
        }
    }

    private void writeSubRecords(Object object, RbfComplexTypeMapping typeMapping) {
        for (String fieldName : typeMapping.getFieldNames(RbfNodeType.RECORD)) {
            Object fieldValue = typeMapping.getObjectAccessor().getValue(object, fieldName);
            if (fieldValue == null) {
                continue;
            }
            RecordMapping recordMapping = typeMapping.getNodeMapping(fieldName, Object.class);
            TypeMapping<?> subRecordTypeMapping = getTypeMapping(recordMapping.getDataTypeName());
            if (subRecordTypeMapping instanceof RbfComplexTypeMapping) {
                getLowLevelSerializer().finishRecord();
                writePrefix(recordMapping.getNodeDescriptor().getPrefix());
                writeValue(fieldValue, subRecordTypeMapping);
            } else if (subRecordTypeMapping instanceof RbfListTypeMapping) {
                RbfListTypeMapping listTypeMapping = (RbfListTypeMapping) subRecordTypeMapping;
                for (Object listItem : (Collection<?>) fieldValue) {
                    getLowLevelSerializer().finishRecord();
                    RecordMapping itemRecordMapping = listTypeMapping.getNodeMapping(listItem.getClass());
                    writePrefix(itemRecordMapping.getNodeDescriptor().getPrefix());
                    writeValue(listItem, getTypeMapping(itemRecordMapping.getDataTypeName()));
                }
            }
        }
    }

    private TypeMapping<?> getTypeMapping(String dataTypeName) {
        TypeMapping<?> typeMapping = this.typeMappingRegistry.get(dataTypeName);
        if (typeMapping == null) {
            throw new SerializationException("Unknown data type name: " + dataTypeName);
        }
        return typeMapping;
    }

    private RbfEntryPoint getEntryPoint(Class<?> originalObjectType) {
        RbfEntryPoint entryPoint = ReflectionUtil.getNearest(originalObjectType, this.entryPoints);
        if (entryPoint == null) {
            throw new SerializationException("The following class was not registered for serialization: "
                    + originalObjectType);
        }
        return entryPoint;
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
