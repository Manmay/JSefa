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

package org.jsefa.common.validator;

import java.util.regex.Pattern;

import org.jsefa.common.converter.IntegerConverter;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.converter.StringConverter;

/**
 * An accessor to the configured constraints.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

class ConstraintsAccessor {

    private final ValidatorConfiguration config;

    private final SimpleTypeConverter converter;

    static ConstraintsAccessor create(ValidatorConfiguration config) {
        return new ConstraintsAccessor(config, StringConverter.create());
    }

    static ConstraintsAccessor create(ValidatorConfiguration config, SimpleTypeConverter converter) {
        return new ConstraintsAccessor(config, converter);
    }

    ConstraintsAccessor(ValidatorConfiguration config, SimpleTypeConverter converter) {
        this.config = config;
        this.converter = converter;
    }

    @SuppressWarnings("unchecked")
    <T> T get(String name, boolean required) {
        String value = getString(name, required);
        if (value != null && value.length() > 0) {
            return (T) this.converter.fromString(value);
        } else {
            return null;
        }
    }

    String getString(String name, boolean required) {
        String value = config.getConstraints().get(name);
        if (value == null && required) {
            throw new ValidatorCreationException("No value for constraint " + name + " given");
        }
        return value;
    }

    Integer getInteger(String name, boolean required) {
        return IntegerConverter.create().fromString(getString(name, required));
    }

    Pattern getPattern(String name, boolean required) {
        String patternString = getString(name, required);
        if (patternString == null) {
            return null;
        }
        try {
            return Pattern.compile(patternString);
        } catch (Exception e) {
            throw new ValidatorCreationException("Wrong value for constraint " + name + " given: " + patternString);
        }
    }

}
