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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.SerializationException;
import org.jsefa.Serializer;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.rbf.lowlevel.RbfLowLevelSerializer;
import org.jsefa.rbf.mapping.NodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;
import org.jsefa.rbf.mapping.RecordMapping;

/**
 * Abstract implementation of {@link Serializer} for RBF formats.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class RbfSerializer implements Serializer {
    private final RbfTypeMappingRegistry typeMappingRegistry;

    private final Map<Class<?>, RbfEntryPoint> entryPoints;

    private final boolean withPrefix;

    private final IdentityHashMap<Object, Object> complexObjectsOnPath;

    /**
     * Constructs a new <code>RbfSerializerImpl</code>.
     * 
     * @param typeMappingRegistry the type mapping registry
     * @param entryPoints a map which maps object types to entry points.
     */
    protected RbfSerializer(RbfTypeMappingRegistry typeMappingRegistry, Map<Class<?>, RbfEntryPoint> entryPoints) {
        this.typeMappingRegistry = typeMappingRegistry;
        this.entryPoints = entryPoints;
        this.withPrefix = (entryPoints.values().iterator().next().getDesignator().length() > 0);
        this.complexObjectsOnPath = new IdentityHashMap<Object, Object>();
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
            if (this.withPrefix) {
                writePrefix(entryPoint.getDesignator());
            }
            writeValue(object, getTypeMapping(entryPoint.getDataTypeName()));
            getLowLevelSerializer().finishRecord();
        } catch (SerializationException e) {
            throw e;
        } catch (Exception e) {
            throw new SerializationException("Error while serializing", e);
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

    /**
     * Returns the low level serializer.
     * 
     * @return the low level serializer.
     */
    protected abstract RbfLowLevelSerializer getLowLevelSerializer();

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
        for (String fieldName : typeMapping.getFieldNames(NodeType.FIELD)) {
            Object fieldValue = null;
            if (object != null) {
                fieldValue = typeMapping.getObjectAccessor().getValue(object, fieldName);
            }
            String fieldDataTypeName = typeMapping.getNodeMapping(fieldName).getDataTypeName();
            writeValue(fieldValue, getTypeMapping(fieldDataTypeName));
        }
    }

    private void writeSubRecords(Object object, RbfComplexTypeMapping typeMapping) {
        for (String fieldName : typeMapping.getFieldNames(NodeType.RECORD)) {
            Object fieldValue = typeMapping.getObjectAccessor().getValue(object, fieldName);
            if (fieldValue == null) {
                continue;
            }
            RecordMapping recordMapping = typeMapping.getNodeMapping(fieldName);
            TypeMapping<?> subRecordTypeMapping = getTypeMapping(recordMapping.getDataTypeName());
            if (subRecordTypeMapping instanceof RbfComplexTypeMapping) {
                getLowLevelSerializer().finishRecord();
                writePrefix(recordMapping.getPrefix());
                writeValue(fieldValue, subRecordTypeMapping);
            } else if (subRecordTypeMapping instanceof RbfListTypeMapping) {
                RbfListTypeMapping listTypeMapping = (RbfListTypeMapping) subRecordTypeMapping;
                for (Object listItem : (List<?>) fieldValue) {
                    getLowLevelSerializer().finishRecord();
                    RecordMapping itemRecordMapping = listTypeMapping.getRecordMapping(listItem.getClass());
                    writePrefix(itemRecordMapping.getPrefix());
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
        RbfEntryPoint entryPoint = this.entryPoints.get(originalObjectType);
        if (entryPoint == null) {
            Class<?> objectType = originalObjectType.getSuperclass();
            while (objectType != null) {
                entryPoint = this.entryPoints.get(objectType);
                if (entryPoint != null) {
                    break;
                }
                objectType = objectType.getSuperclass();
            }
        }
        if (entryPoint == null) {
            throw new SerializationException("The following class was not registered for serialization: "
                    + originalObjectType);
        }
        return entryPoint;
    }

}
