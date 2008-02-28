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

import org.jsefa.Serializer;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.lowlevel.CsvLowLevelSerializer;
import org.jsefa.rbf.RbfSerializer;

/**
 * Iterator-style interface for stream based CSV serializer.
 * <p>
 * Note on handling of <code>null</code> values:<br>
 * 1. <code>null</code> values of simple types (e.g. <code>String</code> or <code>Date</code>) will be
 * serialized to an empty string (default) or the String explicitly configured as the null value string (see
 * {@link CsvConfiguration#setDefaultNoValueString(String)}).<br>
 * 2. <code>null</code> values of complex types (classes annotated with <code>CsvDataType</code>) will be
 * serialized to a sequence of empty CSV fields (e. g. ';;;;;').
 * 
 * @see Serializer
 * @author Norman Lahme-Huetig
 * 
 */
public interface CsvSerializer extends RbfSerializer {
    /**
     * Returns a low level CSV serializer.
     * 
     * @return a low level CSV serializer.
     */
    CsvLowLevelSerializer getLowLevelSerializer();
}
