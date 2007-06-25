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

import org.jsefa.ConfigurationException;
import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.Serializer;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.xml.annotation.XmlEntryPointFactory;
import org.jsefa.xml.annotation.XmlTypeMappingFactory;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.NodeModel;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;

/**
 * Factory for creating {@link XmlSerializer} and {@link XmlDeserializer}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public class XmlIOFactory implements IOFactory {

    private final XmlConfiguration config;

    private final XmlTypeMappingRegistry typeMappingRegistry;

    private final Collection<XmlEntryPoint> entryPoints;

    private final Map<Class, NodeModel> entryNodeModels;

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s
     * and <code>XmlDeserializer</code>s which can handle objects of the
     * given object types.
     * 
     * @param objectTypes the object types.
     * @return a <code>XmlIOFactory</code> factory
     */
    public static XmlIOFactory createFactory(Class... objectTypes) {
        return createFactory(new XmlConfiguration(), objectTypes);
    }

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s
     * and <code>XmlDeserializer</code>s which can handle objects of the
     * given object types.
     * 
     * @param config the configuration object. It will be copied so that the
     *            given one can be modified or reused.
     * @param objectTypes object types for which entry points should be created
     *            from annotations
     * @return a a <code>XmlIOFactory</code> factory
     */
    public static XmlIOFactory createFactory(XmlConfiguration config, Class... objectTypes) {
        XmlTypeMappingRegistry typeMappingRegistry = new XmlTypeMappingRegistry(config
                .getSimpleTypeConverterProvider());
        Collection<XmlEntryPoint> entryPoints = new ArrayList<XmlEntryPoint>();
        entryPoints.addAll(XmlEntryPointFactory.createEntryPoints(new XmlTypeMappingFactory(config,
                typeMappingRegistry), objectTypes));
        return new XmlIOFactory(config, typeMappingRegistry, entryPoints);
    }

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s
     * and <code>XmlDeserializer</code>s which can handle objects of the
     * object types defined in the given entry points.
     * 
     * @param config the configuration object. It will be copied so that the
     *            given one can be modified or reused.
     * @param typeMappingRegistry the type mapping registry with type mappings
     *            for all data types which are referred to from the given entry
     *            points.
     * @param entryPoints the entry points. An entry point is required for every
     *            type of object which will be passed to
     *            {@link Serializer#write} or which should be returned from
     *            {@link Deserializer#next} and only for these objects (not for
     *            the objects related to these ones). If more than one entry
     *            point is defined for the same data type name, then <br>
     *            a) the last one is used for serialization<br>
     *            b) all are used for deserialization whereas their respective
     *            designators are used as alternative designators for the same
     *            data type.
     * @return a <code>XmlIOFactory</code> factory
     */
    public static XmlIOFactory createFactory(XmlConfiguration config, XmlTypeMappingRegistry typeMappingRegistry,
            Collection<XmlEntryPoint> entryPoints) {
        return new XmlIOFactory(config, typeMappingRegistry, entryPoints);

    }

    XmlIOFactory(XmlConfiguration config, XmlTypeMappingRegistry typeMappingRegistry,
            Collection<XmlEntryPoint> entryPoints) {
        this.config = config.createCopy();
        this.typeMappingRegistry = new XmlTypeMappingRegistry(typeMappingRegistry);
        this.entryPoints = new ArrayList<XmlEntryPoint>(entryPoints);
        this.entryNodeModels = new HashMap<Class, NodeModel>();
        for (XmlEntryPoint entryPoint : entryPoints) {
            TypeMapping typeMapping = typeMappingRegistry.get(entryPoint.getDataTypeName());
            if (typeMapping == null) {
                throw new ConfigurationException("Unknown data type: " + entryPoint.getDataTypeName());
            }
            ElementDescriptor elementDescriptor = new ElementDescriptor(entryPoint.getDesignator(), entryPoint
                    .getDataTypeName());
            NodeModel nodeModel = new NodeModel(elementDescriptor, null);
            this.entryNodeModels.put(typeMapping.getObjectType(), nodeModel);
        }
        markAmbiguousNodeModels(this.entryNodeModels.values());
        finishNodeModels(this.entryNodeModels.values());
    }

    /**
     * {@inheritDoc}
     */
    public XmlSerializer createSerializer() {
        return new XmlSerializerImpl(this.config, this.typeMappingRegistry, this.entryNodeModels);
    }

    /**
     * {@inheritDoc}
     */
    public XmlDeserializer createDeserializer() {
        return new XmlDeserializerImpl(this.config, this.typeMappingRegistry, this.entryPoints);
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
