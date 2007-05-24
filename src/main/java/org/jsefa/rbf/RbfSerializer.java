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

import java.io.IOException;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.SerializationException;
import org.jsefa.Serializer;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.rbf.mapping.NodeModel;
import org.jsefa.rbf.mapping.NodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Abstract implementation of {@link Serializer} for RBF formats.
 * 
 * @author Norman Lahme-Huetig
 */
public abstract class RbfSerializer implements Serializer {
    private final RbfTypeMappingRegistry typeMappingRegistry;

    private final Map<Class, RbfEntryPoint> entryPoints;

    private final boolean withPrefix;

    private final String lineBreak;

    private Writer writer;

    private int columnIndex;

    private IdentityHashMap<Object, Object> complexObjectsOnPath;

    /**
     * Constructs a new <code>RbfSerializerImpl</code>.
     * 
     * @param typeMappingRegistry the type mapping registry
     * @param entryPoints a map which maps object types to entry points.
     * @param lineBreak the line break <code>String</code>
     */
    protected RbfSerializer(RbfTypeMappingRegistry typeMappingRegistry, Map<Class, RbfEntryPoint> entryPoints,
            String lineBreak) {
        this.entryPoints = entryPoints;
        this.typeMappingRegistry = typeMappingRegistry;
        this.lineBreak = lineBreak;
        this.withPrefix = (entryPoints.values().iterator().next().getDesignator().length() > 0);
        this.complexObjectsOnPath = new IdentityHashMap<Object, Object>();
    }

    /**
     * {@inheritDoc}
     */
    public final void open(Writer writer) {
        this.writer = writer;
        this.complexObjectsOnPath.clear();
    }

    /**
     * {@inheritDoc}
     */
    public final void write(Object object) {
        if (object == null) {
            return;
        }
        RbfEntryPoint entryPoint = this.entryPoints.get(object.getClass());
        if (entryPoint == null) {
            Class objectType = object.getClass().getSuperclass();
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
                    + object.getClass());
        }
        this.columnIndex = 0;
        if (this.withPrefix) {
            writeString(entryPoint.getDesignator());
            this.columnIndex++;
        }
        writeValue(object, getTypeMapping(entryPoint.getDataTypeName()));
        terminateLine();
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
     * Writes the given value as a simple value using the simple type mapping.
     * 
     * @param value the value to write
     * @param mapping the simple type mapping
     */
    protected abstract void writeSimpleValue(Object value, SimpleTypeMapping mapping);

    /**
     * Called before terminating a line. Override to perform extra action on
     * line termination.
     */
    protected void beforeEOL() {

    }

    /**
     * Returns the index of the current column (beginning with 0).
     * 
     * @return the index of the current column
     */
    protected final int getColumnIndex() {
        return this.columnIndex;
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

    private void writeValue(Object object, TypeMapping typeMapping) {
        if (typeMapping instanceof SimpleTypeMapping) {
            writeSimpleValue(object, (SimpleTypeMapping) typeMapping);
            this.columnIndex++;
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
        for (String fieldName : typeMapping.getFieldNames(NodeType.FIELD)) {
            Object fieldValue = null;
            if (object != null) {
                fieldValue = typeMapping.getObjectAccessor().getValue(object, fieldName);
            }
            String fieldDataTypeName = typeMapping.getNodeModel(fieldName).getDataTypeName();
            writeValue(fieldValue, getTypeMapping(fieldDataTypeName));
        }
        if (object == null) {
            return;
        }
        for (String fieldName : typeMapping.getFieldNames(NodeType.SUB_RECORD)) {
            Object fieldValue = typeMapping.getObjectAccessor().getValue(object, fieldName);
            if (fieldValue == null) {
                continue;
            }
            NodeModel nodeModel = typeMapping.getNodeModel(fieldName);
            TypeMapping subRecordTypeMapping = getTypeMapping(nodeModel.getDataTypeName());
            if (subRecordTypeMapping instanceof RbfComplexTypeMapping) {
                terminateLine();
                writeString(nodeModel.getPrefix());
                this.columnIndex++;
                writeValue(fieldValue, subRecordTypeMapping);
            } else if (subRecordTypeMapping instanceof RbfListTypeMapping) {
                RbfListTypeMapping listTypeMapping = (RbfListTypeMapping) subRecordTypeMapping;
                for (Object listItem : (List) fieldValue) {
                    terminateLine();
                    NodeModel itemNodeModel = listTypeMapping.getNodeModel(listItem.getClass());
                    writeString(itemNodeModel.getPrefix());
                    this.columnIndex++;
                    writeValue(listItem, getTypeMapping(itemNodeModel.getDataTypeName()));
                }
            }
        }
        this.complexObjectsOnPath.remove(object);
    }

    private TypeMapping getTypeMapping(String dataTypeName) {
        TypeMapping typeMapping = this.typeMappingRegistry.get(dataTypeName);
        if (typeMapping == null) {
            throw new SerializationException("Unknown data type name: " + dataTypeName);
        }
        return typeMapping;
    }

    private void terminateLine() {
        beforeEOL();
        writeNewLine();
        this.columnIndex = 0;
    }

}
