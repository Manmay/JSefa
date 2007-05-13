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

package org.jsefa.csv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jsefa.Deserializer;
import org.jsefa.Serializer;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvTypeMappingFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.rbf.RbfEntryPoint;
import org.jsefa.rbf.RbfIOFactory;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;

/**
 * Factory for creating {@link CsvSerializer}s and {@link CsvDeserializer}s.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class CsvIOFactory extends RbfIOFactory<CsvConfiguration, CsvSerializer, CsvDeserializer> {

    /**
     * Creates a new <code>CsvIOFactory</code> for <code>CsvSerializer</code>s
     * and <code>CsvDeserializer</code>s which can handle objects of the
     * given object types.
     * 
     * @param objectTypes the object types.
     * @return a <code>XmlIOFactory</code> factory
     */
    public static CsvIOFactory createFactory(Class... objectTypes) {
        return CsvIOFactory.createFactory(new CsvConfiguration(), objectTypes);
    }

    /**
     * Creates a new <code>CsvIOFactory</code> for <code>CsvSerializer</code>s
     * and <code>CsvDeserializer</code>s which can handle objects of the
     * given object types.
     * 
     * @param config the configuration object. It will be copied so that the
     *            given one can be modified or reused.
     * @param objectTypes object types for which entry points should be created
     *            from annotations
     * @return a <code>CsvIOFactory</code> factory
     */
    public static CsvIOFactory createFactory(CsvConfiguration config, Class... objectTypes) {
        RbfTypeMappingRegistry typeMappingRegistry = new RbfTypeMappingRegistry();
        Collection<RbfEntryPoint> entryPoints = new ArrayList<RbfEntryPoint>();
        CsvTypeMappingFactory typeMappingFactory = new CsvTypeMappingFactory(config, typeMappingRegistry);
        for (Class<Object> objectType : objectTypes) {
            String dataTypeName = typeMappingFactory.createIfAbsent(objectType);
            String prefix = objectType.getAnnotation(CsvDataType.class).defaultPrefix();
            entryPoints.add(new RbfEntryPoint(dataTypeName, prefix));
        }
        return new CsvIOFactory(config, typeMappingRegistry, entryPoints);
    }

    /**
     * Creates a new <code>CsvIOFactory</code> for <code>CsvSerializer</code>s
     * and <code>CsvDeserializer</code>s which can handle objects of the
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
     * @return a <code>CsvIOFactory</code> factory
     */
    public static CsvIOFactory createFactory(CsvConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Collection<RbfEntryPoint> entryPoints) {
        return new CsvIOFactory(config, typeMappingRegistry, entryPoints);

    }

    CsvIOFactory(CsvConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Collection<RbfEntryPoint> entryPoints) {
        super(config, typeMappingRegistry, entryPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CsvSerializer createSerializer(CsvConfiguration config, RbfTypeMappingRegistry typeMappingRegistry,
            Map<Class, RbfEntryPoint> entryPointsByObjectType) {
        return new CsvSerializerImpl(config, typeMappingRegistry, entryPointsByObjectType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CsvDeserializer createDeserializer(CsvConfiguration config,
            RbfTypeMappingRegistry typeMappingRegistry, RbfEntryPoint entryPoint) {
        return new CsvDeserializerImpl(config, typeMappingRegistry, entryPoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CsvDeserializer createDeserializer(CsvConfiguration config,
            RbfTypeMappingRegistry typeMappingRegistry, Map<String, RbfEntryPoint> entryPointsByPrefix) {
        return new CsvDeserializerImpl(config, typeMappingRegistry, entryPointsByPrefix);
    }

}
