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

import java.util.HashMap;
import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.common.validator.Validator;
import org.jsefa.common.validator.traversal.TraversingValidatorFactory;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.lowlevel.XmlLowLevelIOFactory;
import org.jsefa.xml.mapping.ElementDescriptor;
import org.jsefa.xml.mapping.ElementMapping;
import org.jsefa.xml.mapping.ElementMappingsBuilder;
import org.jsefa.xml.mapping.XmlEntryPoint;
import org.jsefa.xml.mapping.XmlTypeMappingUtil;
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

    private final Map<ElementDescriptor, ElementMapping> entryElementMappingsByElementDescriptor;

    private final Map<Class<?>, ElementMapping> entryElementMappingsByObjectType;

    private final XmlLowLevelIOFactory lowLevelIOFactory;

    /**
     * Creates a new <code>XmlIOFactory</code> for <code>XmlSerializer</code>s and
     * <code>XmlDeserializer</code>s using the given configuration.
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
        this.entryElementMappingsByElementDescriptor = createEntryElementMappingsByElementDescriptor();
        this.entryElementMappingsByObjectType = createEntryElementMappingsByObjectType();
        this.lowLevelIOFactory = XmlLowLevelIOFactory.createFactory(config.getLowLevelConfiguration());
    }

    /**
     * {@inheritDoc}
     */
    public XmlSerializer createSerializer() {
        return new XmlSerializerImpl(this.config, this.entryElementMappingsByObjectType, this.lowLevelIOFactory
                .createSerializer());
    }

    /**
     * {@inheritDoc}
     */
    public XmlDeserializer createDeserializer() {
        return new XmlDeserializerImpl(this.config, entryElementMappingsByElementDescriptor,
                this.lowLevelIOFactory.createDeserializer());
    }

    private Map<ElementDescriptor, ElementMapping> createEntryElementMappingsByElementDescriptor() {
        ElementMappingsBuilder elementMappingsBuilder = new ElementMappingsBuilder();
        TraversingValidatorFactory<QName> deepValidatorFactory = new TraversingValidatorFactory<QName>(config
                .getTypeMappingRegistry(), config.getObjectAccessorProvider());
        for (XmlEntryPoint entryPoint : config.getEntryPoints()) {
            TypeMapping<?> typeMapping = config.getTypeMappingRegistry().get(entryPoint.getDataTypeName());
            if (typeMapping == null) {
                throw new IOFactoryException("Unknown data type: " + entryPoint.getDataTypeName());
            }
            ElementDescriptor elementDescriptor = new ElementDescriptor(entryPoint.getDesignator(), entryPoint
                    .getDataTypeName());
            Validator validator = deepValidatorFactory.create(entryPoint.getDataTypeName(), entryPoint
                    .getValidator());
            elementMappingsBuilder.addMapping(entryPoint.getDataTypeName(), elementDescriptor, typeMapping
                    .getObjectType(), null, validator, null);
        }
        return XmlTypeMappingUtil.createNodeMappingsByNodeDescriptorMap(elementMappingsBuilder.getResult());
    }

    private Map<Class<?>, ElementMapping> createEntryElementMappingsByObjectType() {
        Map<Class<?>, ElementMapping> elementMappings = new HashMap<Class<?>, ElementMapping>();
        for (ElementMapping elementMapping : this.entryElementMappingsByElementDescriptor.values()) {
            elementMappings.put(elementMapping.getObjectType(), elementMapping);
        }
        return elementMappings;
    }

}
