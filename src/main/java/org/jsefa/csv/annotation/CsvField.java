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

package org.jsefa.csv.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jsefa.common.annotation.NoConverterClass;
import org.jsefa.common.converter.SimpleTypeConverter;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.csv.config.QuoteMode;

/**
 * CSV field annotation.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@Retention(RUNTIME)
@Target({ FIELD })
public @interface CsvField {

    /**
     * The position of the field with the following semantic:
     * <p>
     * If the position of a field A is less than the position of a field B, than
     * field A comes (not necessarily directly) before field B.<br>
     * The positions of all fields must specify a total order.
     */
    int pos();

    /**
     * The name of the data type of the CSV field this annotation describes. If
     * not set, it must be determinable from the type of the annotated java
     * field.
     * <p>
     * It should be explicitly set if the type mapping should not be created
     * from the annotations given in the class of the annotated field. This is
     * useful when mixing explicit type mapping creation with annotation based
     * type mapping creation.
     */
    String dataTypeName() default "";

    /**
     * The quote mode to use for the CSV field this annotation describes. If not
     * set, the default quote mode ({@link CsvConfiguration#setDefaultQuoteMode})
     * will be used.
     */
    QuoteMode quoteMode() default QuoteMode.DEFAULT;

    /**
     * The format to be used to construct a <code>SimpleTypeConverter</code>
     * for this field. The <code>SimpleTypeConverter</code> class will be
     * determined using the type of the annotated java field.
     */
    String[] format() default {};

    /**
     * Specifies the converter class to be used. In the default case the
     * converter class is determined using the type of the annotated java field.
     */
    Class<? extends SimpleTypeConverter> converterClass() default NoConverterClass.class;
}
