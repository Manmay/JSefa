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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.IOFactoryException;
import org.jsefa.Serializer;
import org.jsefa.common.config.Configuration;
import org.jsefa.common.mapping.TypeMapping;
import org.jsefa.rbf.mapping.NodeType;
import org.jsefa.rbf.mapping.RbfComplexTypeMapping;
import org.jsefa.rbf.mapping.RbfEntryPoint;
import org.jsefa.rbf.mapping.RbfListTypeMapping;
import org.jsefa.rbf.mapping.RbfTypeMappingRegistry;
import org.jsefa.rbf.mapping.RecordMapping;

/**
 * Abstract super class for RBF factories.
 * <p>
 * Instances of this class are immutable and thread-safe. This must be true for all subclasses, too.
 * 
 * @author Norman Lahme-Huetig
 * 
 * @param <C> the configuration type
 * @param <S> the serializer type
 * @param <D> the deserializer type
 */
public abstract class RbfIOFactory<C extends Configuration<RbfTypeMappingRegistry, RbfEntryPoint>,
    S extends Serializer, D extends Deserializer> implements IOFactory {

    private final C config;

    private final RbfEntryPoint entryPoint;

    private final ConcurrentMap<String, RbfEntryPoint> entryPointsByPrefix;

    private final ConcurrentMap<Class<?>, RbfEntryPoint> entryPointsByObjectType;

    private final boolean withPrefix;

    /**
     * Constructs a new <code>RbfIOFactory</code>.
     * 
     * @param config the configuration
     */
    @SuppressWarnings("unchecked")
    protected RbfIOFactory(C config) {
        if (config.getEntryPoints().size() == 0) {
            throw new IOFactoryException("No entry points given");
        }
        this.withPrefix = prefixRequired(config.getEntryPoints());
        this.config = (C) config.createCopy();
        this.entryPointsByObjectType = new ConcurrentHashMap<Class<?>, RbfEntryPoint>();

        if (this.withPrefix) {
            this.entryPointsByPrefix = new ConcurrentHashMap<String, RbfEntryPoint>();
            for (RbfEntryPoint entryPoint : config.getEntryPoints()) {
                Class objectType = getObjectType(entryPoint.getDataTypeName());
                assertPrefixDeclared(entryPoint, objectType);
                this.entryPointsByObjectType.put(objectType, entryPoint);
                this.entryPointsByPrefix.put(entryPoint.getDesignator(), entryPoint);
            }
            assertPrefixContentualUniqueness(config.getEntryPoints());
            this.entryPoint = null;
        } else {
            this.entryPoint = config.getEntryPoints().iterator().next();
            Class objectType = getObjectType(entryPoint.getDataTypeName());
            this.entryPointsByObjectType.put(objectType, entryPoint);
            this.entryPointsByPrefix = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public final S createSerializer() {
        return createSerializer(this.config, this.entryPointsByObjectType);
    }

    /**
     * {@inheritDoc}
     */
    public final D createDeserializer() {
        if (this.withPrefix) {
            return createDeserializer(this.config, this.entryPointsByPrefix);
        } else {
            return createDeserializer(this.config, this.entryPoint);
        }
    }

    /**
     * Returns true if prefixes are used and false otherwise.
     * 
     * @return true if prefixes are used; otherwise false
     */
    public boolean withPrefixes() {
        return prefixRequired(this.config.getEntryPoints());
    }

    /**
     * Creates a new <code>Serializer</code>.
     * 
     * @param config the configuration
     * @param entryPointsByObjectType a map from object types to entry points.
     * @return a <code>Serializer</code>
     */
    protected abstract S createSerializer(C config, Map<Class<?>, RbfEntryPoint> entryPointsByObjectType);

    /**
     * Creates a new <code>Deserializer</code>.
     * 
     * @param config the configuration
     * @param entryPoint the entry point
     * @return a <code>Deserializer</code>
     */
    protected abstract D createDeserializer(C config, RbfEntryPoint entryPoint);

    /**
     * Creates a new A<code>Deserializer</code>.
     * 
     * @param config the configuration
     * @param entryPointsByPrefix a map from prefixes to entry points
     * @return a <code>Deserializer</code>
     */
    protected abstract D createDeserializer(C config, Map<String, RbfEntryPoint> entryPointsByPrefix);

    /**
     * Returns true if and only if a prefix is required for the given entry points.
     * 
     * @param entryPoints the entry points
     * @return true, if a prefix is required; false otherwise.
     */
    protected final boolean prefixRequired(Collection<RbfEntryPoint> entryPoints) {
        if (entryPoints.size() > 1) {
            return true;
        } else {
            String prefix = entryPoints.iterator().next().getDesignator();
            return prefix != null && prefix.length() > 0;
        }
    }

    private Class<?> getObjectType(String dataTypeName) {
        TypeMapping<?> typeMapping = this.config.getTypeMappingRegistry().get(dataTypeName);
        if (typeMapping == null) {
            throw new IOFactoryException("Unknown data type: " + dataTypeName);
        }
        return typeMapping.getObjectType();

    }

    private void assertPrefixDeclared(RbfEntryPoint entryPoint, Class<?> objectType) {
        String prefix = entryPoint.getDesignator();
        if (prefix == null || prefix.length() == 0) {
            throw new IOFactoryException("prefix not given but required for object type " + objectType.getName());
        }
    }

    /**
     * Assert that all prefixes are contextual unique.
     * <p>
     * Suppose a tree of prefix where<br>
     * 1. the root is an artificial prefix which is always unique <br>
     * 2. the ordered children of the root are the prefixes of the entry points in their respective order <br>
     * 3. the ordered children of a prefix n which is not covered by 1. or 2. is the ordered list of prefixes
     * associated with the sub records or sub record lists of the complex type associated with the prefix n.
     * <p>
     * For a given prefix n the set S(n) is defined as the set of prefixes containing exactly <br>
     * 1. the sibling prefixes "to the left" of n as well as their descendants, and<br>
     * 2. n itself if n is associated to a list type mapping.
     * <p>
     * Then a prefix n is called "contextual unique" if it is not contained in S(n).
     * 
     * @param entryPoints the entry points.
     */
    private void assertPrefixContentualUniqueness(Collection<RbfEntryPoint> entryPoints) {
        Set<String> usedPrefixes = new HashSet<String>();
        for (RbfEntryPoint entryPoint : entryPoints) {
            assertPrefixContextualUniqueness(entryPoint.getDesignator(), entryPoint.getDataTypeName(),
                    usedPrefixes);
        }
    }

    private void assertPrefixContextualUniqueness(String prefix, String dataTypeName,
            Set<String> siblingUsedPrefixes) {
        if (siblingUsedPrefixes.contains(prefix)) {
            throw new IOFactoryException("The prefix " + prefix
                    + " is not contextual unique. The context is defined by the following list: "
                    + siblingUsedPrefixes);
        }
        Set<String> usedPrefixes = new HashSet<String>();
        TypeMapping<?> typeMapping = this.config.getTypeMappingRegistry().get(dataTypeName);
        if (typeMapping instanceof RbfComplexTypeMapping) {
            RbfComplexTypeMapping complexTypeMapping = (RbfComplexTypeMapping) typeMapping;
            for (String fieldName : complexTypeMapping.getFieldNames(NodeType.RECORD)) {
                RecordMapping recordMapping = complexTypeMapping.getNodeMapping(fieldName);
                assertPrefixContextualUniqueness(recordMapping.getPrefix(), recordMapping.getDataTypeName(),
                        usedPrefixes);
            }
        }
        if (typeMapping instanceof RbfListTypeMapping) {
            usedPrefixes.add(prefix);
            RbfListTypeMapping listTypeMapping = (RbfListTypeMapping) typeMapping;
            for (String itemPrefix : listTypeMapping.getPrefixes()) {
                RecordMapping recordMapping = listTypeMapping.getRecordMapping(itemPrefix);
                assertPrefixContextualUniqueness(recordMapping.getPrefix(), recordMapping.getDataTypeName(),
                        usedPrefixes);
            }
        }
        usedPrefixes.add(prefix);
        siblingUsedPrefixes.addAll(usedPrefixes);
    }

}
