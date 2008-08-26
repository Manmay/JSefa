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
 * A qualified name consisting of a namespace uri and a local name.
 * <p>
 * Both must not be null. Use {@link NamespaceConstants#NO_NAMESPACE_URI} to describe that no namespace is given.
 * <p>
 * Instances of this class are immutable and thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class QName {

    private final String uri;

    private final String localName;

    private final int hashCode;

    /**
     * Creates a new <code>QName</code> with no namespace URI.
     * <p>
     * 
     * @param localName the local name
     * @return the name
     */
    public static QName create(String localName) {
        return new QName(NamespaceConstants.NO_NAMESPACE_URI, localName);
    }

    /**
     * Creates a new <code>QName</code>.
     * <p>
     * Use {@link NamespaceConstants#NO_NAMESPACE_URI} for the argument <code>uri</code> if no namespace is
     * given.
     * 
     * @param uri the namespace uri (not null)
     * @param localName the local name
     * @return the name
     */
    public static QName create(String uri, String localName) {
        return new QName(uri, localName);
    }

    private QName(String uri, String localName) {
        if (uri == null || localName == null) {
            throw new IllegalArgumentException("Both uri and local name must not be null!");
        }
        this.uri = uri;
        this.localName = localName;
        this.hashCode = 37 * (17 + this.uri.hashCode()) + this.localName.hashCode();
    }

    /**
     * Returns the local name.
     * 
     * @return the local name
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Returns the namespace uri.
     * 
     * @return the namespace uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof QName)) {
            return false;
        }
        QName other = (QName) obj;
        return (this.localName.equals(other.localName)) && (this.uri.equals(other.uri));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * Returns a <code>String</code> representation of this qualified name with the following format:
     * <p>
     * [uri#]localName.
     * 
     * @return the <code>String</code> representation of this qualified name
     */
    @Override
    public String toString() {
        if (NamespaceConstants.NO_NAMESPACE_URI.equals(this.uri)) {
            return this.localName;
        } else {
            return this.uri + "#" + this.localName;
        }
    }

}
