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

package org.jsefa.common.annotation;

/**
 * Collection of names for annotation data (data given via annotations).
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public interface AnnotationDataNames {
    /**
     * Name of the annotation data used to denote the position of an annotated
     * java field.
     */
    String POS = "pos";

    /**
     * Name of the annotation data used to denote a name.
     */
    String NAME = "name";

    /**
     * Name of the annotation data used to denote a data type name.
     */
    String DATA_TYPE_NAME = "dataTypeName";

    /**
     * Name of the annotation data used to denote an object type.
     */
    String OBJECT_TYPE = "objectType";

    /**
     * Name of the annotation data used to denote a format.
     */
    String FORMAT = "format";

    /**
     * Name of the annotation data used to denote a converter type.
     */
    String CONVERTER_TYPE = "converterType";
}
