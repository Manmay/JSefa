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

package org.jsefa.csv;

import org.jsefa.Deserializer;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.rbf.RbfDeserializer;

/**
 * Iterator-style interface for stream based CSV deserializer.
 * <p>
 * Notes:<br>
 * 1. A field with no value (an empty field) may be represented by the empty String (default) or any other String
 * (see {@link CsvConfiguration#getDefaultNoValueString})<br>
 * 2. Empty fields representing a simple value (e.g. a <code>String</code> or <code>Date</code>) will be
 * deserialized to <code>null</code> and not to an empty value.<br>
 * 3. Empty field sequences (e.g. ';;;' with ';' as the delimiter) representing a complex value (a value of a class
 * annotated with <code>CsvDataType</code>) will be deserialized to <code>null</code> and not to an empty
 * value.
 * 
 * @see Deserializer
 * @author Norman Lahme-Huetig
 */

public interface CsvDeserializer extends RbfDeserializer {

}
