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

package org.jsefa.xml.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jsefa.common.annotation.NoValidatorType;
import org.jsefa.common.validator.Validator;

/**
 * An annotation declaring a complex data type with its relevant sub object types. Each object type which should
 * map to a complex element and for which a type mapping should be constructed from annotations must be annotated
 * with this annotation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@Retention(RUNTIME)
@Target({TYPE})
public @interface XmlDataType {
    /**
     * The name of the data type. The name must be unique within the set of data types used within one XML
     * document. If not set a name will be created from the name of the class.
     */
    String name() default "";

    /**
     * The relevant sub object types of the one which is annotated with this annotation. Only subclasses of the
     * annotated class which are mentioned in this array are considered in the serialization/deserialization
     * process.
     */
    Class<?>[] subObjectTypes() default {};

    /**
     * The default name for elements with this data type.
     */
    String defaultElementName() default "";

    /**
     * Specifies the validator type to be used for the default case.
     */
    Class<? extends Validator> defaultValidatorType() default NoValidatorType.class;

}
