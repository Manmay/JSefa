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
 * Collection of names for annotation parameters.
 * 
 * @author Norman Lahme-Huetig
 * @author Matthias Derer
 * 
 */
public interface AnnotationParameterNames {
    /**
     * Name of the annotation parameter used to denote the position of an annotated java field.
     */
    String POS = "pos";

    /**
     * Name of the annotation parameter used to denote a name.
     */
    String NAME = "name";
    
    /**
     * Name of the annotation parameter used to denote a default name.
     */
    String DEFAULT_NAME = "defaultName";

    /**
     * Name of the annotation parameter used to denote a data type name.
     */
    String DATA_TYPE_NAME = "dataTypeName";

    /**
     * Name of the annotation parameter used to denote an object type.
     */
    String OBJECT_TYPE = "objectType";

    /**
     * Name of the annotation parameter used to denote a format.
     */
    String FORMAT = "format";

    /**
     * Name of the annotation parameter used to denote a converter type.
     */
    String CONVERTER_TYPE = "converterType";

    /**
     * Name of the annotation parameter used to denote a text mode.
     */
    String TEXT_MODE = "textMode";
    
    /**
     * Name of the annotation parameter used to denote a list item.
     */
    String LIST_ITEM = "listItem";
    
    /**
     * Name of the annotation parameter used to denote if a value is required or not.
     */
    String VALUE_REQUIRED = "required";
    
    /**
     * Name of the annotation parameter used to denote a validator type.
     */
    String VALIDATOR_TYPE = "validatorType";
    
    /**
     * Name of the annotation parameter used to denote a default validator type.
     */
    String DEFAULT_VALIDATOR_TYPE = "defaultValidatorType";

    /**
     * Name of the annotation parameter used to denote the constraints to validate.
     */
    String CONSTRAINTS = "constraints";    
    
}
