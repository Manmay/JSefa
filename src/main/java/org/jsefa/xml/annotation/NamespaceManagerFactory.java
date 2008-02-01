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

import org.jsefa.xml.namespace.NamespaceManager;

/**
 * Factory for creating a {@link NamespaceManager} based on the {@link XmlNamespaces} annotation of a given class.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class NamespaceManagerFactory {
    /**
     * Creates a new <code>NamespaceManager</code> and registers the namespace declarations the given object type
     * is annotated with.
     * 
     * @param objectType the object type
     * @return a new namespace manager
     */
    public static NamespaceManager create(Class<?> objectType) {
        NamespaceManager result = NamespaceManager.create();
        XmlNamespaces namespaces = objectType.getAnnotation(XmlNamespaces.class);
        if (namespaces != null) {
            for (Namespace namespace : namespaces.value()) {
                result.registerPrefix(namespace.prefix(), namespace.uri());
            }
        }
        return result;
    }

    private NamespaceManagerFactory() {

    }
}
