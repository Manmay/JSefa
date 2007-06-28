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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jsefa.common.util.ReflectionUtil;

/**
 * Converter for JDK 1.5 enums.
 * <p>
 * It is thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class EnumConverter implements SimpleTypeConverter {

    private final Class<? extends Enum> enumType;

    private final Map<String, String> nameToAliasMap;

    private final Map<String, String> aliasToNameMap;

    /**
     * Constructs a new <code>EnumConverter</code>.
     * <p>
     * Note: the display name can be set with the {@link EnumConstant}
     * annotation.
     * 
     * @param enumType the enum type
     */
    public EnumConverter(Class<? extends Enum> enumType) {
        this(enumType, (String[]) null);
    }

    /**
     * Constructs a new <code>EnumConverter</code>.
     * 
     * @param enumType the enum type
     * @param format an array of mappings from the original <code>String</code>
     *            representation of an enum value to the one which shall be used
     *            for serialization. Each mapping has the form <br>
     *            originalRepresentation=newRepresentation<br>
     *            Example:
     *            <code>{"SENIOR_DEVELOPER=senior developer", "JUNIOR_DEVELOPER=junior developer"}</code>
     *            <p>
     *            These mappings override the display name declarations provided
     *            by the {@link EnumConstant} annotations.
     */
    public EnumConverter(Class<? extends Enum> enumType, String... format) {
        this.enumType = enumType;
        this.nameToAliasMap = new HashMap<String, String>();
        this.aliasToNameMap = new HashMap<String, String>();
        for (Field field : ReflectionUtil.getAllFields(enumType)) {
            if (field.isAnnotationPresent(EnumConstant.class)) {
                this.nameToAliasMap.put(field.getName(), field.getAnnotation(EnumConstant.class).value());
                this.aliasToNameMap.put(field.getAnnotation(EnumConstant.class).value(), field.getName());
            }
        }
        if (format != null) {
            for (String mapping : format) {
                String[] tokens = mapping.split("=");
                if (this.nameToAliasMap.get(tokens[0]) != null) {
                    this.aliasToNameMap.remove(this.nameToAliasMap.get(tokens[0]));
                }
                this.nameToAliasMap.put(tokens[0], tokens[1]);
                this.aliasToNameMap.put(tokens[1], tokens[0]);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        String name = this.aliasToNameMap.get(value);
        if (name != null) {
            return Enum.valueOf(this.enumType, name);
        } else {
            return Enum.valueOf(this.enumType, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString(Object value) {
        if (value == null) {
            return null;
        }
        String name = ((Enum) value).name();
        String alias = this.nameToAliasMap.get(name);
        if (alias != null) {
            return alias;
        } else {
            return name;
        }
    }

}
