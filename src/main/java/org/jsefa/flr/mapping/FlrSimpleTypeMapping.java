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

package org.jsefa.flr.mapping;

import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.mapping.SimpleTypeMapping;
import org.jsefa.common.mapping.TypeMapping;

/**
 * A mapping between a java object type and a simple FLR data type.
 * 
 * @see TypeMapping
 * @author Norman Lahme-Huetig
 * 
 */
public final class FlrSimpleTypeMapping extends SimpleTypeMapping<String> {

    private final int length;

    private final char padCharacter;

    private final Align align;

    /**
     * Constructs a new <code>FlrSimpleTypeMapping</code> from the given
     * arguments.
     * 
     * @param objectType the object type
     * @param dataTypeName the data type name
     * @param simpleTypeConverter the simple type converter
     * @param length the length of the FLR field
     * @param padCharacter the pad character to fill empty space
     * @param align the alignment of the field
     */
    public FlrSimpleTypeMapping(Class objectType, String dataTypeName, SimpleTypeConverter simpleTypeConverter,
            int length, char padCharacter, Align align) {
        super(objectType, dataTypeName, simpleTypeConverter);
        this.length = length;
        this.padCharacter = padCharacter;
        this.align = align;
        finish();
    }

    /**
     * Returns the alignment.
     * 
     * @return the alignment
     */
    public Align getAlign() {
        return align;
    }

    /**
     * Returns the length of the FLR field.
     * 
     * @return the lenght
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns the pad character to fill empty space.
     * 
     * @return the pad character
     */
    public char getPadCharacter() {
        return padCharacter;
    }

}
