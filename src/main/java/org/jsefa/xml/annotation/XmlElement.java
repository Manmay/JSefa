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
import org.jsefa.common.converter.SimpleTypeConverter;

/**
 * An annotation stating that the annotated java field should be mapped to a xml element during XML serialization
 * and deserialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface XmlElement {

    /**
     * The name of the element this annotation describes. It has the following format:<br>
     * [prefix:]localname<br>
     */
    String name() default "";

    /**
     * The name of the data type of the element this annotation describes. If not set, it must be determinable from
     * the type of the annotated field.
     * <p>
     * It should be explicitly set if the type mapping should not be created from the annotations given in the
     * class of the annotated field. This is useful when mixing explicit type mapping creation with annotation
     * based type mapping creation.
     */
    String dataTypeName() default "";

    /**
     * The position of the xml element with the following semantic:
     * <p>
     * If the position of a element A is less than the position of a element B, than element A comes (not
     * necessarily directly) before element B.<br>
     * All elements with the default position value (-1) will come after all elements with explicitly given
     * positions.
     */
    int pos() default -1;

    /**
     * The format to be used to construct a <code>SimpleTypeConverter</code> for the xml element which must have
     * a simple data type (no children, no data holding attributes). The <code>SimpleTypeConverter</code> class
     * will be determined using the type of the java field with this annotation.
     */
    String[] format() default {};

    /**
     * Specifies the converter type to be used for the xml element which must have a simple data type (no children,
     * no data holding attributes). In the default case the converter type is determined using the type of the java
     * field with this annotation.
     */
    Class<? extends SimpleTypeConverter> converterType() default NoConverterType.class;

}
