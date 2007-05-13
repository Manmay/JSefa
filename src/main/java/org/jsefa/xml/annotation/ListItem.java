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

import org.jsefa.common.annotation.NoClass;

/**
 * Annotation describing the object type and element name of a list item.
 * 
 * @see XmlElementList
 * @author Norman Lahme-Huetig
 * 
 */
public @interface ListItem {

    /**
     * The name of the element this annotation describes. It has the following
     * format:<br>
     * [prefix:]localname<br>
     */
    String name();

    /**
     * The name of the data type this <code>ListItem</code> describes. If not
     * set, it must be determinable from the given object type.
     * <p>
     * It should be explicitly set if the type mapping should not be created
     * from the annotations given in the class <code>objectType</code>. This
     * is useful when mixing explicit type mapping creation with annotation
     * based type mapping creation.
     */
    String dataTypeName() default "";

    /**
     * The type of the object this <code>ListItem</code> describes. From this
     * the data type must be determinable if it is not explicity given. If the
     * object type is not set, the data type name must be given explicitly.
     * <p>
     * The object type will be ignored if the data type name is given
     * explicitly.
     */
    Class objectType() default NoClass.class;

}
