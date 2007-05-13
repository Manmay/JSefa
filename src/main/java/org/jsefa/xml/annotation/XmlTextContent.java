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

import org.jsefa.common.annotation.NoConverterClass;
import org.jsefa.common.converter.SimpleTypeConverter;

/**
 * An annotation stating that the annotated java field should be mapped to the
 * content of the xml node with the data type described by the class the
 * annotated field is part of.
 * <p>
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface XmlTextContent {
    /**
     * The format to be used to construct a <code>SimpleTypeConverter</code>
     * for the content. The <code>SimpleTypeConverter</code> class will be
     * determined using the type of the java field with this annotation.
     */
    String[] format() default {};

    /**
     * Specifies the converter class to be used for the xml attribute. In the
     * default case the converter class is determined using the type of the java
     * field with this annotation.
     */
    Class<? extends SimpleTypeConverter> converterClass() default NoConverterClass.class;

}
