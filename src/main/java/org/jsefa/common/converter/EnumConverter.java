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
import java.util.Map.Entry;

import org.jsefa.common.util.ReflectionUtil;

/**
 * Converter for JDK 1.5 enums. <br>
 * The format is an array of mappings from the original <code>String</code> representation of an enum value to
 * the one which shall be used for serialization. Each mapping has the form <br>
 * originalRepresentation=newRepresentation<br>
 * Example: <code>{"SENIOR_DEVELOPER=senior developer", "JUNIOR_DEVELOPER=junior developer"}</code>
 * <p>
 * These mappings override the display name declarations provided by the {@link EnumConstant} annotations.
 * <p>
 * It is thread safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class EnumConverter implements SimpleTypeConverter {

    @SuppressWarnings("unchecked")
    private final Class<? extends Enum> enumType;

    private final Map<String, String> nameToAliasMap;

    private final Map<String, String> aliasToNameMap;

    /**
     * Constructs a new <code>EnumConverter</code>.
     * 
     * @param configuration the configuration
     * @return a enum converter
     */
    public static EnumConverter create(SimpleTypeConverterConfiguration configuration) {
        Class<? extends Enum<?>> enumType = configuration.getObjectType();
        Map<String, String> nameToAliasMap = new HashMap<String, String>();
        for (Field field : ReflectionUtil.getAllFields(enumType)) {
            if (field.isAnnotationPresent(EnumConstant.class)) {
                nameToAliasMap.put(field.getName(), field.getAnnotation(EnumConstant.class).value());
            }
        }
        if (configuration.getFormat() != null) {
            for (String mapping : configuration.getFormat()) {
                String[] tokens = mapping.split("=");
                nameToAliasMap.put(tokens[0], tokens[1]);
            }
        }
        return new EnumConverter(enumType, nameToAliasMap);
    }

    private EnumConverter(Class<? extends Enum<?>> enumType, Map<String, String> nameToAliasMap) {
        this.enumType = enumType;
        this.nameToAliasMap = nameToAliasMap;
        this.aliasToNameMap = new HashMap<String, String>();

        for (Entry<String, String> entry : this.nameToAliasMap.entrySet()) {
            this.aliasToNameMap.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enum fromString(String value) {
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
    @SuppressWarnings("unchecked")
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
