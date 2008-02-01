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

/**
 * An annotation stating that the annotated java field should be mapped to a xml element with list type during XML
 * serialization and deserialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface XmlElementList {
    /**
     * The name of the element this annotation describes. It has the following format:<br>
     * [prefix:]localname<br>
     */
    String name() default "";

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
     * True, if there is no embracing element around the list items; false otherwise.
     */
    boolean implicit() default false;

    /**
     * An array of {@link ListItem} annotations describing the list items.
     */
    ListItem[] items();

}
