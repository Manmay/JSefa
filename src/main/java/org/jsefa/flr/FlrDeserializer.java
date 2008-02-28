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

import org.jsefa.Deserializer;
import org.jsefa.rbf.RbfDeserializer;

/**
 * Iterator-style interface for stream based FLR deserializer.
 * <p>
 * Notes:<br>
 * 1. Empty fields representing a simple value (e.g. a <code>String</code> or <code>Date</code>) will be
 * deserialized to <code>null</code> and not to an empty value.<br>
 * 2. Empty field sequences (long sequences of pad characters) representing a complex value (a value of a class
 * annotated with <code>FlrDataType</code>) will be deserialized to <code>null</code> and not to an empty
 * value.
 * 
 * @see Deserializer
 * @author Norman Lahme-Huetig
 */
public interface FlrDeserializer extends RbfDeserializer {

}
