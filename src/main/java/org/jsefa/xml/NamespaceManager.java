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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsefa.ConfigurationException;

/**
 * A <code>NamespaceManager</code> manages namespace URIs and their prefixes.
 * It allows for <br>
 * 1. the registration of new prefix-URI-combinations, <br>
 * 2. the retrieval of the URI to a given prefix<br>
 * 3. the retrieval of the prefix to a given URI<br>
 * 4. the creation of a new prefix for a new URI
 * <p>
 * A <code>NamespaceManager</code> may have a parent
 * <code>NamespaceManager</code> to which retrieval requests are delegated if
 * it has no own registries or if it has no result to the request.
 * <p>
 * Note: Instances of this class are intentionally not thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class NamespaceManager {
    private final NamespaceManager parent;

    private final Map<String, String> preferredPrefixes;

    private Map<String, String> registeredURIs;

    private Map<String, String> registeredPrefixes;

    private boolean hasOwnRegistries;

    /**
     * Creates a new <code>NamespaceManager</code>.
     * 
     * @return a <code>NamespaceManager</code>
     */
    public static NamespaceManager create() {
        return new NamespaceManager();
    }

    /**
     * Creates a new <code>NamespaceManager</code> with the given preferred
     * prefixes. A preferred prefix for an URI will be taken when a <b>new</b>
     * prefix shall be created with {@link #createPrefix} for the same URI.
     * 
     * @param preferredPrefixes a map with prefixes as keys and URIs as values.
     * @return a <code>NamespaceManager</code>
     */
    public static NamespaceManager create(Map<String, String> preferredPrefixes) {
        NamespaceManager manager = new NamespaceManager();
        manager.preferredPrefixes.putAll(preferredPrefixes);
        return manager;
    }

    /**
     * Creates a new <code>NamespaceManager</code> with the given
     * <code>NamespaceManager</code> as its parent.
     * 
     * @param parent the parent of this namespace manager.
     * @return a <code>NamespaceManager</code>
     */
    public static NamespaceManager createWithParent(NamespaceManager parent) {
        return new NamespaceManager(parent);
    }

    private NamespaceManager() {
        createOwnRegistries();
        this.preferredPrefixes = new HashMap<String, String>();
        registerStandardPreferredPrefixes();
        this.parent = null;
    }

    private NamespaceManager(NamespaceManager parent) {
        this.parent = parent;
        this.preferredPrefixes = parent.preferredPrefixes;
    }

    /**
     * Registers a new prefix for a new namespace uri.
     * 
     * @param prefix the prefix
     * @param uri the uri
     * @throws NullPointerException if one of the arguments is null
     * @throws ConfigurationException if either the prefix or the uri is already
     *             registered.
     */
    public void register(String prefix, String uri) {
        if (prefix == null || uri == null) {
            throw new NullPointerException("Prefix and uri must nut be null");
        }
        if (hasOwnRegistries) {
            if (this.registeredPrefixes.containsKey(uri)) {
                throw new ConfigurationException("There is a already registered prefix for the uri " + uri);
            }
            if (this.registeredURIs.containsKey(prefix)) {
                throw new ConfigurationException("The prefix " + prefix + " is already bound. It is bound to "
                        + this.registeredURIs.get(prefix));
            }
        } else {
            createOwnRegistries();
        }
        this.registeredPrefixes.put(uri, prefix);
        this.registeredURIs.put(prefix, uri);
    }

    /**
     * Merges the content of the other namespace manager into this one.
     * <p>
     * This overrides existing registrations of URIs for prefixes which are
     * known to this namespace manager or to the <code>other</code> namespace
     * manager or one of its ancestors. The same is true for preferred prefixes
     * if this namespace manager has no parent.
     * 
     * @param other the other namespace manager
     */
    public void registerAll(NamespaceManager other) {
        for (String prefix : other.getAllPrefixes()) {
            register(prefix, other.getUri(prefix));
        }
        if (this.parent == null) {
            this.preferredPrefixes.putAll(other.preferredPrefixes);
        }
    }

    /**
     * Returns the namespace uri the given prefix is registered for. If this
     * namespace manager has no registration for the given prefix, than its
     * parent namespace manager is asked for it (in the case a parent exists).
     * 
     * @param prefix the prefix
     * @return the uri or null if none is registered for the given prefix.
     */
    public String getUri(String prefix) {
        if (!this.hasOwnRegistries) {
            return this.parent.getUri(prefix);
        }
        String uri = this.registeredURIs.get(prefix);
        if (uri == null && this.parent != null) {
            uri = this.parent.getUri(prefix);
        }
        return uri;
    }

    /**
     * Returns the prefix which is registered for the given namespace uri. If
     * this namespace manager has no registration for the given uri, than its
     * parent namespace manager is asked for it (in the case a parent exists).
     * 
     * @param uri the namespace uri
     * @return the prefix or null if none is registered for the given uri
     */
    public String getPrefix(String uri) {
        if (uri == null) {
            return null;
        }
        if (!this.hasOwnRegistries) {
            return this.parent.getPrefix(uri);
        }
        String prefix = this.registeredPrefixes.get(uri);
        if (prefix == null && this.parent != null) {
            prefix = this.parent.getPrefix(uri);
            if (this.registeredURIs.containsKey(prefix)) {
                prefix = null;
            }
        }
        return prefix;
    }

    /**
     * Returns a collection of prefixes for which a uri was registered using
     * this namespace manager or one of its ancestors.
     * 
     * @return a collection of prefixes.
     */
    public Collection<String> getAllPrefixes() {
        Set<String> prefixes = new HashSet<String>();
        NamespaceManager manager = this;
        while (manager != null) {
            if (manager.hasOwnRegistries) {
                prefixes.addAll(manager.registeredURIs.keySet());
            }
            manager = manager.parent;
        }
        return prefixes;
    }

    /**
     * Returns the registered prefix for the given namespace uri or creates a
     * new one and registers it. For the latter case a preferred prefix is
     * returned if it exists for the given URI and if the prefix is not already
     * bound to another URI.
     * 
     * @param uri the uri to get a prefix for
     * @param defaultAllowed true, if the prefix may be the default one.
     * @return the prefix
     */
    public String createPrefix(String uri, boolean defaultAllowed) {
        if (uri == null) {
            return null;
        }
        String prefix = getPrefix(uri);
        if (prefix == null) {
            prefix = this.preferredPrefixes.get(uri);
            if (prefix != null && getUri(prefix) != null) {
                prefix = null;
            }
            if (prefix == null) {
                prefix = "";
                if (!defaultAllowed || getUri(prefix) != null) {
                    int no = 1;
                    do {
                        prefix = "ns" + no++;
                    } while (getUri(prefix) != null);
                }
            }
            register(prefix, uri);
        }
        return prefix;
    }

    /**
     * Returns the parent namespace manager of this namespace manager - if
     * exists.
     * 
     * @return the parent namespace manager or null if none exists.
     */
    public NamespaceManager getParent() {
        return this.parent;
    }

    private void registerStandardPreferredPrefixes() {
        this.preferredPrefixes.put(XmlConstants.XML_SCHEMA_INSTANCE_URI, "xsi");
    }

    private void createOwnRegistries() {
        this.registeredPrefixes = new HashMap<String, String>();
        this.registeredURIs = new HashMap<String, String>();
        this.hasOwnRegistries = true;
    }

}
