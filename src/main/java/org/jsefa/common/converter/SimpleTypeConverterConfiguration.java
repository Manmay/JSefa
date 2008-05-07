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
 * A configuration for a simple type converter.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class SimpleTypeConverterConfiguration {
    private final Class<?> objectType;

    private final String[] format;

    private final SimpleTypeConverter itemTypeConverter;

    /**
     * The empty configuration. This indicates that the default configuration values should be used for the
     * respective simple type converter.
     */
    public static final SimpleTypeConverterConfiguration EMPTY = new SimpleTypeConverterConfiguration(null, null,
            null);

    /**
     * Constructs a new <code>SimpleTypeConverterConfiguration</code>.
     * 
     * @param objectType the object type the converter is intended for
     * @param format the format the converter shall be initialized with
     * @return a simple type converter configuration
     */
    public static SimpleTypeConverterConfiguration create(Class<?> objectType, String[] format) {
        return new SimpleTypeConverterConfiguration(objectType, format, null);
    }

    /**
     * Constructs a new <code>SimpleTypeConverterConfiguration</code>.
     * 
     * @param objectType the object type the converter is intended for
     * @param format the format the converter shall be initialized with
     * @param itemTypeConverter the item type converter
     * @return a simple type converter configuration
     */
    public static SimpleTypeConverterConfiguration create(Class<?> objectType, String[] format,
            SimpleTypeConverter itemTypeConverter) {
        return new SimpleTypeConverterConfiguration(objectType, format, itemTypeConverter);
    }

    private SimpleTypeConverterConfiguration(Class<?> objectType, String[] format,
            SimpleTypeConverter itemTypeConverter) {
        this.objectType = objectType;
        this.format = format;
        this.itemTypeConverter = itemTypeConverter;
    }

    /**
     * /** Returns the object type the converter is intended for.
     * 
     * @param <T> the expected type
     * @return the object type
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getObjectType() {
        return (Class<T>) this.objectType;
    }

    /**
     * Returns the format the converter shall be initialized with.
     * 
     * @return the format
     */
    public String[] getFormat() {
        return this.format;
    }

    /**
     * Returns the item type converter.
     * 
     * @return the item type converter
     */
    public SimpleTypeConverter getItemTypeConverter() {
        return this.itemTypeConverter;
    }

}
