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

package org.jsefa.csv.lowlevel.config;

/**
 * Enum for the different modes of quoting.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public enum QuoteMode {

    /**
     * Denotes that a field value is always surrounded by quotes.
     */
    ALWAYS,

    /**
     * Denotes that a field value is surrounded by quotes only if it contains the delimiter char or line break.
     */
    ON_DEMAND,

    /**
     * Denotes that a field value is never surrounded by quotes whereas single characters may be escaped instead.
     */
    NEVER,

    /**
     * Denotes that the quote mode which is declared as the default one should be used.
     */
    DEFAULT;
}
