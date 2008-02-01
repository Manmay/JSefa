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

package org.jsefa.rbf.mapping;

import org.jsefa.common.mapping.TypeMappingRegistry;

/**
 * A registry for RBF type mappings.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class RbfTypeMappingRegistry extends TypeMappingRegistry<String> {
    /**
     * Constructs a new <code>RbfTypeMappingRegistry</code>.
     */
    public RbfTypeMappingRegistry() {
    }

    /**
     * Constructs a new <code>RbfTypeMappingRegistry</code> as a copy of the given one.
     * 
     * @param other the registry that serves as a model for creating a new one
     */
    private RbfTypeMappingRegistry(RbfTypeMappingRegistry other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RbfTypeMappingRegistry createCopy() {
        return new RbfTypeMappingRegistry(this);
    }

}
