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

package org.jsefa.flr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jsefa.IOFactoryException;
import org.jsefa.Deserializer;
import org.jsefa.Serializer;
import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrTypeMappingFactory;
import org.jsefa.rbf.RbfIOFactory;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Factory for creating {@link FlrSerializer}s and {@link FlrDeserializer}s.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrIOFactory extends RbfIOFactory<FlrConfiguration, FlrSerializer, FlrDeserializer> {

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s
     * and <code>FlrDeserializer</code>s which can handle objects of the
     * given object types.
     * 
     * @param objectTypes the object types.
     * @return a <code>XmlIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(Class... objectTypes) {
        return FlrIOFactory.createFactory(new FlrConfiguration(), objectTypes);
    }

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s
     * and <code>FlrDeserializer</code>s which can handle objects of the
     * given object types.
     * 
     * @param config the configuration object. It will be copied so that the
     *            given one can be modified or reused.
     * @param objectTypes object types for which entry points should be created
     *            from annotations
     * @return a <code>FlrIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(FlrConfiguration config, Class... objectTypes) {
        try {
            RbfTypeMappingRegistry typeMappingRegistry = new RbfTypeMappingRegistry();
            Collection<RbfEntryPoint> entryPoints = new ArrayList<RbfEntryPoint>();
            FlrTypeMappingFactory typeMappingFactory = new FlrTypeMappingFactory(typeMappingRegistry, config
                    .getSimpleTypeConverterProvider(), config.getObjectAccessorProvider());
            for (Class<Object> objectType : objectTypes) {
                String dataTypeName = typeMappingFactory.createIfAbsent(objectType);
                String prefix = objectType.getAnnotation(FlrDataType.class).defaultPrefix();
                entryPoints.add(new RbfEntryPoint(dataTypeName, prefix));
            }
            return new FlrIOFactory(config, typeMappingRegistry, entryPoints);
        } catch (IOFactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an FlrIOFactory", e);
        }
    }

    /**
     * Creates a new <code>FlrIOFactory</code> for <code>FlrSerializer</code>s
     * and <code>FlrDeserializer</code>s which can handle objects of the
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
     * @return a <code>FlrIOFactory</code> factory
     * @throws IOFactoryException
     */
    public static FlrIOFactory createFactory(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Collection<RbfEntryPoint> entryPoints) {
        try {
            return new FlrIOFactory(config, typeMappingRegistry, entryPoints);
        } catch (IOFactoryException e) {
            throw e;
        } catch (Exception e) {
            throw new IOFactoryException("Failed to create an FlrIOFactory", e);
        }
    }

    FlrIOFactory(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Collection<RbfEntryPoint> entryPoints) {
        super(config, typeMappingRegistry, entryPoints);
        if (prefixRequired(entryPoints)) {
            assertEqualPrefixLength(entryPoints);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FlrSerializer createSerializer(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<Class, RbfEntryPoint> entryPointsByObjectType) {
        return new FlrSerializerImpl(config, typeMappingRegistry, entryPointsByObjectType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FlrDeserializer createDeserializer(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            RbfEntryPoint entryPoint) {
        return new FlrDeserializerImpl(config, typeMappingRegistry, entryPoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FlrDeserializer createDeserializer(FlrConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<String, RbfEntryPoint> entryPointsByPrefix) {
        return new FlrDeserializerImpl(config, typeMappingRegistry, entryPointsByPrefix);
    }

    private void assertEqualPrefixLength(Collection<RbfEntryPoint> entryPoints) {
        int length = entryPoints.iterator().next().getDesignator().length();
        for (RbfEntryPoint entryPoint : entryPoints) {
            if (entryPoint.getDesignator().length() != length) {
                throw new IOFactoryException("The prefix " + entryPoint.getDesignator() + " has not the length "
                        + length);
            }
        }
    }

}
