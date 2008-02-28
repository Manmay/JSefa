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

package org.jsefa.flr;

import org.jsefa.Serializer;
import org.jsefa.flr.lowlevel.FlrLowLevelSerializer;
import org.jsefa.rbf.RbfSerializer;

/**
 * Iterator-style interface for stream based FLR serializer.
 * <p>
 * Note on handling of <code>null</code> values:<br>
 * 1. <code>null</code> values of simple types (e.g. <code>String</code> or <code>Date</code>) will be
 * serialized to a sequence of pad characters with the respective field length.<br>
 * 2. <code>null</code> values of complex types (classes annotated with <code>FlrDataType</code>) will be
 * serialized to a sequence of sequences of pad characters according to the fields of the complex type.
 * 
 * @see Serializer
 * @author Norman Lahme-Huetig
 * 
 */
public interface FlrSerializer extends RbfSerializer {
    /**
     * Returns a low level FLR serializer.
     * 
     * @return a low level FLR serializer.
     */
    FlrLowLevelSerializer getLowLevelSerializer();

}
