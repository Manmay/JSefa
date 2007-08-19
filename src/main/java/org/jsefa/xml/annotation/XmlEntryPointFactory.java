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

package org.jsefa.xml.annotation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jsefa.xml.mapping.XmlEntryPoint;
import org.jsefa.xml.mapping.XmlTypeMappingRegistry;
import org.jsefa.xml.namespace.NamespaceManager;
import org.jsefa.xml.namespace.QName;
import org.jsefa.xml.namespace.QNameParser;

/**
 * Factory for creating {@link XmlEntryPoint}s from annotated classes.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlEntryPointFactory {
    /**
     * Creates entry points for the given object types and their descendants
     * using their annotations.
     * <p>
     * The following rules apply:
     * <p>
     * 1. For each two object types A and B in <code>objectTypes</code> with B
     * being a descendant of A and A < B (A comes before B in
     * <code>objectTypes</code>), the resulting list contains an entry point
     * with an element name taken from B and not from A.
     * <p>
     * 2. For each descendant B of an object type A in <code>objectTypes</code>
     * the resulting list contains an entry point with an element name taken
     * from A and not from B as long as rule 1 does not apply.
     * 
     * @param objectTypes the object types
     * @param typeMappingFactory the type mapping factory
     * @return a <code>Collection</code> of entry points.
     */
    public static Collection<XmlEntryPoint> createEntryPoints(XmlTypeMappingFactory typeMappingFactory,
            Class<?>... objectTypes) {
        Map<Class<?>, XmlEntryPoint> entryPoints = new HashMap<Class<?>, XmlEntryPoint>();
        XmlTypeMappingRegistry registry = typeMappingFactory.getTypeMappingRegistry();
        for (Class<?> rootObjectType : objectTypes) {
            QName rootDataTypeName = typeMappingFactory.createIfAbsent(rootObjectType);
            QName elementName = getAnnotatedElementName(rootObjectType, rootObjectType.getSimpleName());
            for (QName dataTypeName : registry.getDataTypeNameTreeElements(rootDataTypeName)) {
                Class<?> objectType = registry.get(dataTypeName).getObjectType();
                entryPoints.put(objectType, new XmlEntryPoint(dataTypeName, elementName));
            }
        }
        return entryPoints.values();
    }

    private static QName getAnnotatedElementName(Class<?> objectType, String defaultName) {
        NamespaceManager namespaceManager = NamespaceManagerFactory.create(objectType);
        String name = null;
        if (objectType.isAnnotationPresent(XmlDataType.class)) {
            name = objectType.getAnnotation(XmlDataType.class).defaultElementName();
        }
        if (name != null && name.length() != 0) {
            return QNameParser.parse(name, true, namespaceManager);
        } else {
            return QNameParser.parse(defaultName, true, namespaceManager);
        }
    }

    private XmlEntryPointFactory() {

    }

}
