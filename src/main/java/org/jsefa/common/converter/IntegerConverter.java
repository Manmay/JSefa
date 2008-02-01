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
 * Converter for <code>Integer</code> objects.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class IntegerConverter implements SimpleTypeConverter {
    private static final IntegerConverter INSTANCE = new IntegerConverter();

    /**
     * Returns the single <code>IntegerConverter</code>.
     * 
     * @return the single integer converter.
     */
    public static IntegerConverter create() {
        return INSTANCE;
    }

    private IntegerConverter() {

    }

    /**
     * {@inheritDoc}
     */
    public Integer fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            if (value.charAt(0) == '+') {
                return fromString(value.substring(1));
            }
            throw new ConversionException("Wrong Integer format: " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
