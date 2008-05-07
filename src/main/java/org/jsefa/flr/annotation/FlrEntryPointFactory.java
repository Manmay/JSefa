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

package org.jsefa.flr.annotation;

import java.util.ArrayList;
import java.util.Collection;

import org.jsefa.rbf.mapping.RbfEntryPoint;

/**
 * Factory for creating {@link RbfEntryPoint}s from annotated classes using the {@link FlrDataType} annotation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrEntryPointFactory {
    /**
     * Creates entry points for the given object types.
     * 
     * @param objectTypes the object types
     * @param typeMappingFactory the type mapping factory
     * @return a <code>Collection</code> of entry points.
     */
    public static Collection<RbfEntryPoint> createEntryPoints(FlrTypeMappingFactory typeMappingFactory,
            Class<?>... objectTypes) {
        Collection<RbfEntryPoint> entryPoints = new ArrayList<RbfEntryPoint>(objectTypes.length);
        for (Class<?> objectType : objectTypes) {
            String dataTypeName = typeMappingFactory.createIfAbsent(objectType);
            String prefix = getAnnotatedPrefix(objectType, objectType.getSimpleName());
            entryPoints.add(new RbfEntryPoint(dataTypeName, prefix, null));
        }
        return entryPoints;
    }

    private static String getAnnotatedPrefix(Class<?> objectType, String defaultName) {
        return objectType.getAnnotation(FlrDataType.class).defaultPrefix();
    }

    private FlrEntryPointFactory() {

    }

}
