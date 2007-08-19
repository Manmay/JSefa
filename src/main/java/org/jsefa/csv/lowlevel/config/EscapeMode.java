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
 * Enum for the different modes of escaping.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public enum EscapeMode {

    /**
     * Denotes that an escape character should be used for escaping.
     */
    ESCAPE_CHARACTER,

    /**
     * Denotes that the character to escape should be doubled for escaping.
     */
    DOUBLING

}
