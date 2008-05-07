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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jsefa.common.annotation.NoConverterType;
import org.jsefa.common.annotation.NoValidatorType;
import org.jsefa.common.annotation.SimpleListItem;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.common.validator.Validator;

/**
 * An annotation stating that the annotated java field should be mapped to a xml attribute during XML serialization
 * and deserialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface XmlAttribute {
    /**
     * The name of the xml attribute this annotation describes. It has the following format:<br>
     * [prefix:]localname<br>
     */
    String name() default "";

    /**
     * The name of the data type of the attribute this annotation describes. If not set, it must be determinable
     * from the type of the annotated field.
     * <p>
     * It should be explicitly set if the type mapping should not be created from the annotations given in the
     * class of the annotated field. This is useful when mixing explicit type mapping creation with annotation
     * based type mapping creation.
     */
    String dataTypeName() default "";

    /**
     * The format to be used to construct a <code>SimpleTypeConverter</code> for the xml attribute. The
     * <code>SimpleTypeConverter</code> class will be determined using the type of the java field with this
     * annotation.
     */
    String[] format() default {};

    /**
     * Specifies the converter type to be used for the xml attribute. In the default case the converter type is
     * determined using the type of the java field with this annotation.
     */
    Class<? extends SimpleTypeConverter> converterType() default NoConverterType.class;

    /**
     * In case of a simple type list this annotation parameter provides details about the list items.
     */
    SimpleListItem listItem() default @SimpleListItem();

    /**
     * True, if a value is required; false otherwise.
     */
    boolean required() default false;

    /**
     * Specifies the validator type to be used. In the default case the validator type is determined using the type
     * of the java field with this annotation.
     */
    Class<? extends Validator> validatorType() default NoValidatorType.class;

    /**
     * The constraints to validate. Each constraint is a String of the form 'name=value' where name is the name of
     * the constraint and value is its value. The allowed set of constraints depend on the validator type. 
     */
    String[] constraints() default {};    
}
