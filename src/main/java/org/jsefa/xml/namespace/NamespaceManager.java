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

import static org.jsefa.xml.namespace.NamespaceConstants.DEFAULT_NAMESPACE_PREFIX;

import java.util.HashMap;
import java.util.Map;

/**
 * A <code>NamespaceManager</code> manages namespace URIs and their prefixes.
 * It allows for <br>
 * 1. the registration of preferred prefixes for URIs (for root
 * <code>NamespaceManager</code>s only),<br>
 * 2. the registration of new prefix-URI-combinations, <br>
 * 3. the retrieval of the URI to a given prefix<br>
 * 4. the retrieval of a prefix to a given URI<br>
 * 5. the creation of a new prefix for a new URI
 * <p>
 * A <code>NamespaceManager</code> may have a parent
 * <code>NamespaceManager</code> to which retrieval requests are delegated if
 * it has no own registries or if it has no result to the request. A
 * <code>NamespaceManager</code> without a parent is called a root namespace
 * manager.
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

    private String defaultURI;

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
     * Creates a new <code>NamespaceManager</code> with the given
     * <code>NamespaceManager</code> as its parent.
     * 
     * @param parent the parent of this namespace manager.
     * @return a <code>NamespaceManager</code>
     */
    public static NamespaceManager createWithParent(NamespaceManager parent) {
        return new NamespaceManager(parent, true);
    }

    private NamespaceManager() {
        this.parent = null;
        createOwnRegistries();
        this.preferredPrefixes = new HashMap<String, String>();
    }

    private NamespaceManager(NamespaceManager other, boolean otherIsParent) {
        if (otherIsParent) {
            this.parent = other;
            this.preferredPrefixes = other.preferredPrefixes;
        } else {
            this.parent = other.parent;
            this.preferredPrefixes = other.preferredPrefixes;
            this.hasOwnRegistries = other.hasOwnRegistries;
            if (this.hasOwnRegistries) {
                this.registeredPrefixes = new HashMap<String, String>(other.registeredPrefixes);
                this.registeredURIs = new HashMap<String, String>(other.registeredURIs);
                this.defaultURI = other.defaultURI;
            }
        }
    }

    /**
     * Creates a copy of this <code>NamespaceManager</code>. The copy has its
     * own registries but has the same (identical) parent as the this
     * <code>NamespaceManager</code>.
     * 
     * @return a copy of this <code>NamespaceManager</code>
     */
    public NamespaceManager createCopy() {
        return new NamespaceManager(this, false);
    }

    /**
     * Registers the given prefix to be the preferred one for the given URI. I.
     * e. if a prefix is needed (call of {@link #getOrCreatePrefix}) for that
     * URI the given preferred one is used and not an automatically created one.
     * 
     * @param prefix the preferred prefix
     * @param uri the URI
     */
    public void registerPreferredPrefix(String prefix, String uri) {
        if (this.parent != null) {
            throw new NamespaceRegistrationException(
                    "A preferred prefix can be registered for a root namespace manager only");
        }
        this.preferredPrefixes.put(uri, prefix);

    }

    /**
     * Registers a new prefix for a namespace uri.
     * <p>
     * During serialization a registered prefix is interpretated is being known
     * at the current point in the xml document.
     * <p>
     * Note: Normally it is {@link #registerPreferredPrefix}
     * what you want to call.
     * 
     * @param prefix the prefix
     * @param uri the uri
     * @throws NullPointerException if one of the arguments is null
     * @throws NamespaceRegistrationException if <br>
     *                 1. the prefix is already bound to another uri<br>
     *                 2. the prefix is an explicit prefix and the uri is
     *                 already bound to another explicit prefix
     */
    public void register(String prefix, String uri) {
        if (prefix == null || uri == null) {
            throw new NullPointerException("Prefix and uri must not be null");
        }
        if (hasOwnRegistries) {
            if (isDefault(prefix)) {
                if (this.defaultURI != null && !uri.equals(this.defaultURI)) {
                    throw new NamespaceRegistrationException("The default prefix is already bound to the uri "
                            + this.defaultURI + " and can not be bound to " + uri);
                }
            } else {
                if (this.registeredPrefixes.containsKey(uri) && !prefix.equals(this.registeredPrefixes.get(uri))) {
                    throw new NamespaceRegistrationException("The uri " + uri + " is already bound to the prefix "
                            + this.registeredPrefixes.get(uri) + " and can not be bound to " + prefix);

                }
                if (this.registeredURIs.containsKey(prefix) && !uri.equals(this.registeredURIs.get(prefix))) {
                    throw new NamespaceRegistrationException("The prefix " + prefix
                            + " is already bound to the uri " + this.registeredURIs.get(prefix)
                            + " and can not be bound to " + uri);
                }
            }
        } else {
            createOwnRegistries();
        }
        if (isDefault(prefix)) {
            this.defaultURI = uri;
        } else {
            this.registeredPrefixes.put(uri, prefix);
            this.registeredURIs.put(prefix, uri);
        }
    }

    /**
     * Returns the namespace uri the given prefix is registered for. If this
     * namespace manager has no registration for the given prefix, than its
     * parent namespace manager is asked for it (in the case a parent exists).
     * 
     * @param prefix the prefix
     * @return the uri or null if none is registered for the given prefix
     */
    public String getUri(String prefix) {
        if (!this.hasOwnRegistries) {
            return this.parent.getUri(prefix);
        }
        String uri = null;
        if (isDefault(prefix)) {
            uri = this.defaultURI;
        } else {
            uri = this.registeredURIs.get(prefix);
        }
        if (uri == null && this.parent != null) {
            uri = this.parent.getUri(prefix);
        }
        return uri;
    }

    /**
     * Returns the prefix which is registered for the given namespace uri. If
     * this namespace manager has no registration for the given uri, than its
     * parent namespace manager is asked for it (in the case a parent exists).
     * <p>
     * If the parent namespace manager returns a prefix which is known for this
     * namespace manager (the prefix is overwritten), then null is returned.
     * 
     * @param uri the namespace uri
     * @param defaultAllowed true, if the prefix may be the default one.
     * @return the prefix or null if none is registered for the given uri.
     */
    public String getPrefix(String uri, boolean defaultAllowed) {
        if (uri == null) {
            return null;
        }
        if (!this.hasOwnRegistries) {
            return this.parent.getPrefix(uri, defaultAllowed);
        }
        if (defaultAllowed && uri.equals(this.defaultURI)) {
            return NamespaceConstants.DEFAULT_NAMESPACE_PREFIX;
        }
        String prefix = this.registeredPrefixes.get(uri);
        if (prefix == null && this.parent != null) {
            prefix = this.parent.getPrefix(uri, defaultAllowed);
            if (prefix != null) {
                if (this.registeredURIs.containsKey(prefix)) {
                    prefix = null;
                } else if (isDefault(prefix) && this.defaultURI != null) {
                    prefix = null;
                }
            }
        }
        return prefix;
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
    public String getOrCreatePrefix(String uri, boolean defaultAllowed) {
        if (uri == null) {
            return null;
        }
        String prefix = getPrefix(uri, defaultAllowed);
        if (prefix == null) {
            prefix = this.preferredPrefixes.get(uri);
            if (prefix != null && getUri(prefix) != null) {
                prefix = null;
            }
            if (prefix == null) {
                prefix = DEFAULT_NAMESPACE_PREFIX;
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

    private void createOwnRegistries() {
        this.registeredPrefixes = new HashMap<String, String>();
        this.registeredURIs = new HashMap<String, String>();
        this.hasOwnRegistries = true;
    }

    private boolean isDefault(String prefix) {
        return NamespaceConstants.DEFAULT_NAMESPACE_PREFIX.equals(prefix);
    }

}
