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

package org.jsefa.xml.mapping;

import org.jsefa.common.mapping.TypeMappingException;
import org.jsefa.xml.namespace.QName;

/**
 * A model of a XML node used for serialization and deserialization.
 * <p>
 * Note:<br>
 * 1. Each client must call {@link #finish()} when finishing the construction of
 * a <code>NodeModel</code>.<br>
 * 2. The construction must be performed by one thread only. <br>
 * 3. After the construction step an instance of this class is immutable and
 * thread-safe.<br>
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class NodeModel {
    private final NodeDescriptor node;

    private final QName dataTypeName;

    private final String fieldName;

    private boolean dataTypeAttributeRequired;

    private boolean finished = false;

    /**
     * Constructs a new <code>NodeModel</code> for the given node descriptor
     * and field name.
     * 
     * @param nodeDescriptor the descriptor of the node to create a
     *            serialization model for
     * @param fieldName the name of the field; may be null.
     */
    public NodeModel(NodeDescriptor nodeDescriptor, String fieldName) {
        this(nodeDescriptor, nodeDescriptor.getDataTypeName(), fieldName);
    }

    /**
     * Constructs a new <code>NodeModel</code> for the given node descriptor,
     * data type name and field. The data type name may be different from the
     * one of the node descriptor. In this case it does only indirectly
     * determine the type mapping for the node. The field may be null.
     * 
     * @param nodeModel the descriptor of the node to create a serialization
     *            model for
     * @param dataTypeName the name of the data type.
     * @param fieldName the name of the field; may be null.
     */
    public NodeModel(NodeDescriptor nodeModel, QName dataTypeName, String fieldName) {
        this.node = nodeModel;
        this.dataTypeName = dataTypeName;
        this.fieldName = fieldName;
        this.dataTypeAttributeRequired = false;
    }

    /**
     * Returns the node descriptor.
     * 
     * @return the node descriptor
     */
    public NodeDescriptor getNodeDescriptor() {
        return node;
    }

    /**
     * Returns the data type name of the node.
     * 
     * @return the data type name
     */
    public QName getDataTypeName() {
        return this.dataTypeName;
    }

    /**
     * Returns the name of the java object field the node maps to. May be null.
     * 
     * @return a field name or null.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns true, if the name of the element in its given context (position
     * in the xml document) is not sufficient to determine the data type of the
     * element, so that a data type attribute (e. g. xsi:type) is required.
     * 
     * @return true, if a data type attribute is required to determine the data
     *         type name of the element; false otherwise.
     */
    public boolean requiresDataTypeAttribute() {
        return dataTypeAttributeRequired;
    }

    /**
     * Marks the element that it needs a data type attribute.
     */
    public void setRequiresDataTypeAttribute() {
        assertNotFinished();
        this.dataTypeAttributeRequired = true;
    }

    /**
     * Finishes the construction of this <code>NodeModel</code>.
     */
    public void finish() {
        assertNotFinished();
        this.finished = true;
    }

    /**
     * Returns true if the construction of this <code>NodeModel</code> is
     * already finished; false otherwise.
     * 
     * @return true if the construction of this <code>NodeModel</code> is
     *         already finished; false otherwise
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * Asserts that this <code>NodeModel</code> is not already finished.
     * 
     * @throws TypeMappingException is this <code>NodeModel</code> is already
     *             finished
     */
    private void assertNotFinished() {
        if (this.finished) {
            throw new TypeMappingException("Node model is already finished");
        }
    }

}
