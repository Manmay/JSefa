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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jsefa.common.util.ReflectionUtil;

/**
 * Converter for <code>List</code> objects.<br>
 * The format consists of one <code>String</code> representing the delimiter for the list items.
 * <p>
 * It is thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public class SimpleListConverter implements SimpleTypeConverter {

    /**
     * Format <code>String</code> with "," as the list item delimiter.
     */
    private static final String[] DEFAULT_FORMAT = {","};

    private final String delimiter;

    private final SimpleTypeConverter itemTypeConverter;

    @SuppressWarnings("unchecked")
    private final Class<? extends Collection> collectionClass;

    /**
     * Creates a <code>SimpleListConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @return a simple list converter
     * @throws ConversionException if the given format is not valid.
     */
    public static SimpleListConverter create(SimpleTypeConverterConfiguration configuration) {
        return new SimpleListConverter(configuration);
    }

    /**
     * Constructs a new <code>SimpleListConverter</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     * 
     * @param configuration the configuration
     * @throws ConversionException if the given format is not valid.
     */
    protected SimpleListConverter(SimpleTypeConverterConfiguration configuration) {
        this.delimiter = getFormat(configuration)[0];
        this.itemTypeConverter = configuration.getItemTypeConverter();
        if (configuration.getObjectType().isInterface()) {
            this.collectionClass = getImplementor(configuration.getObjectType());
            if (this.collectionClass == null) {
                throw new ConversionException("Could not create a " + this.getClass().getName()
                        + " for collection type " + configuration.getObjectType().getName());
            }
        } else {
            this.collectionClass = configuration.getObjectType();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final Object fromString(String value) {
        if (value == null) {
            return null;
        }
        Collection result = ReflectionUtil.createInstance(this.collectionClass);
        if (value.length() > 0) {
            int lastIndex = 0;
            int index = value.indexOf(this.delimiter);
            while (index != -1) {
                result.add(this.itemTypeConverter.fromString(value.substring(lastIndex, index)));
                lastIndex = index + this.delimiter.length();
                index = value.indexOf(this.delimiter, lastIndex);
            }
            result.add(this.itemTypeConverter.fromString(value.substring(lastIndex)));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final String toString(Object value) {
        StringBuilder result = new StringBuilder();
        Collection list = (Collection) value;
        int listSize = list.size();
        int itemIndex = 0;
        for (Object item : list) {
            result.append(this.itemTypeConverter.toString(item));
            if (++itemIndex < listSize) {
                result.append(this.delimiter);
            }
        }
        return result.toString();
    }

    /**
     * Returns the default format which is used when no format is given.
     * 
     * @return the default format.
     */
    protected String[] getDefaultFormat() {
        return SimpleListConverter.DEFAULT_FORMAT;
    }

    /**
     * Returns a class implementing the given interface.
     * 
     * @param aCollectionInterface a {@link Collection}.
     * @return a class implementing the given interface or null.
     */
    @SuppressWarnings("unchecked")
    protected Class<? extends Collection> getImplementor(Class<?> aCollectionInterface) {
        if (List.class.isAssignableFrom(aCollectionInterface)) {
            return ArrayList.class;
        }
        if (Set.class.isAssignableFrom(aCollectionInterface)) {
            return HashSet.class;
        }
        if (Queue.class.isAssignableFrom(aCollectionInterface)) {
            return LinkedList.class;
        }
        return null;
    }

    private String[] getFormat(SimpleTypeConverterConfiguration configuration) {
        if (configuration.getFormat() == null) {
            return getDefaultFormat();
        }
        if (configuration.getFormat().length != 1) {
            throw new ConversionException("The format for a SimpleListConverter must be an array with 1 entry");
        }
        return configuration.getFormat();
    }

}
