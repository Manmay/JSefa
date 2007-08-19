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

package org.jsefa.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.lowlevel.XmlLowLevelIOFactory;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.NodeModel;
import org.jsefa.xml.mapping.XmlEntryPoint;
import org.jsefa.xml.namespace.QName;

/**
 * Default implementation of {@link XmlIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public class XmlIOFactoryImpl extends XmlIOFactory {

    private final XmlConfiguration config;

    private final Map<Class<?>, NodeModel> entryNodeModels;

    private final XmlLowLevelIOFactory lowLevelIOFactory;

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s
     * and <code>XmlDeserializer</code>s using the given configuration.
     * 
     * @param config the configuration object.
     * @return a <code>XmlIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static XmlIOFactory createFactory(XmlConfiguration config) {
        return new XmlIOFactoryImpl(config);
    }

    XmlIOFactoryImpl(XmlConfiguration config) {
        this.config = config;
        this.entryNodeModels = createEntryNodeModels();
        this.lowLevelIOFactory = XmlLowLevelIOFactory.createFactory(config.getLowLevelConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    public XmlSerializer createSerializer() {
        return new XmlSerializerImpl(this.config, this.entryNodeModels, this.lowLevelIOFactory.createSerializer());
    }

    /**
     * {@inheritDoc}
     */
    public XmlDeserializer createDeserializer() {
        return new XmlDeserializerImpl(this.config, this.lowLevelIOFactory.createDeserializer());
    }

    private Map<Class<?>, NodeModel> createEntryNodeModels() {
        Map<Class<?>, NodeModel> nodeModels = new HashMap<Class<?>, NodeModel>();
        for (XmlEntryPoint entryPoint : config.getEntryPoints()) {
            TypeMapping<?> typeMapping = config.getTypeMappingRegistry().get(entryPoint.getDataTypeName());
            if (typeMapping == null) {
                throw new IOFactoryException("Unknown data type: " + entryPoint.getDataTypeName());
            }
            ElementDescriptor elementDescriptor = new ElementDescriptor(entryPoint.getDesignator(), entryPoint
                    .getDataTypeName());
            NodeModel nodeModel = new NodeModel(elementDescriptor, null);
            nodeModels.put(typeMapping.getObjectType(), nodeModel);
        }
        markAmbiguousNodeModels(nodeModels.values());
        finishNodeModels(nodeModels.values());
        return nodeModels;
    }

    private void markAmbiguousNodeModels(Collection<NodeModel> nodeModels) {
        Map<QName, Collection<NodeModel>> nameToModels = new HashMap<QName, Collection<NodeModel>>();
        for (NodeModel nodeModel : nodeModels) {
            QName name = nodeModel.getNodeDescriptor().getName();
            Collection<NodeModel> models = nameToModels.get(name);
            if (models == null) {
                models = new ArrayList<NodeModel>();
                nameToModels.put(name, models);
            }
            models.add(nodeModel);
        }
        for (QName name : nameToModels.keySet()) {
            Collection<NodeModel> models = nameToModels.get(name);
            if (models.size() > 1) {
                for (NodeModel nodeModel : models) {
                    nodeModel.setRequiresDataTypeAttribute();
                }
            }
        }
    }

    private void finishNodeModels(Collection<NodeModel> nodeModels) {
        for (NodeModel nodeModel : nodeModels) {
            nodeModel.finish();
        }
    }

}
