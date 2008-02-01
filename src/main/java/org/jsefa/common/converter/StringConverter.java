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

package org.jsefa.common.converter;

/**
 * Converter for <code>String</code> objects.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class StringConverter implements SimpleTypeConverter {
    private static final StringConverter INSTANCE = new StringConverter();

    /**
     * Returns the single <code>StringConverter</code>.
     * 
     * @return the single string converter.
     */
    public static StringConverter create() {
        return INSTANCE;
    }

    private StringConverter() {

    }

    /**
     * {@inheritDoc}
     */
    public Object fromString(String value) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Object value) {
        return (String) value;
    }

}
