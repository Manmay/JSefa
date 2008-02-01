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

package org.jsefa.xml.namespace;

/**
 * A <code>QNameParser</code> allows for creating a {@link QName} from its <code>String</code> representation.
 * <p>
 * The format is <code>prefix:localname</code><br>
 * if an explicit prefix is given and <code>localname</code> if no explicit prefix is given.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class QNameParser {
    /**
     * Creates a {@link QName} from a <code>String</code> representation using the given namespace manager for
     * namespace prefix resolving.
     * <p>
     * In the case the <code>String</code> representation contains no prefix it is assumed that the name belongs
     * to the default namespace only if the argument <code>forElement</code> is true and if a default namespace
     * is registered.
     * 
     * @param name the <code>String</code> representation of the <code>QName</code> as desribed above
     * @param forElement true, if a default namespace should be taken into account; false otherwise.
     * @param namespaceManager the namespace manager used to retrieve the URI for a given prefix
     * @return the <code>QName</code>
     */
    public static QName parse(String name, boolean forElement, NamespaceManager namespaceManager) {
        if (name == null) {
            throw new NullPointerException("Argument name must not be null");
        } else if (name.length() == 0) {
            throw new IllegalArgumentException("Argument name must not be an empty string");
        }
        int delimiterPos = name.indexOf(":");
        if (delimiterPos == -1) {
            if (forElement) {
                String defaultNamespaceURI = namespaceManager.getUri(NamespaceConstants.DEFAULT_NAMESPACE_PREFIX);
                if (defaultNamespaceURI != null) {
                    return QName.create(defaultNamespaceURI, name);
                }
            }
            return QName.create(NamespaceConstants.NO_NAMESPACE_URI, name);
        } else {
            String prefix = name.substring(0, delimiterPos);
            String uri = namespaceManager.getUri(prefix);
            if (uri == null) {
                throw new IllegalArgumentException("Undeclared namespace prefix: " + prefix);
            }
            String localName = "";
            if (delimiterPos < name.length() - 1) {
                localName = name.substring(delimiterPos + 1);
            } else {
                throw new IllegalArgumentException("Invalid QName representation: " + name);
            }
            return QName.create(uri, localName);
        }

    }

    private QNameParser() {

    }
}
