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

package org.jsefa.rbf.annotation;

import java.lang.annotation.Annotation;

/**
 * Parameter object encapsulating the annotation classes to use.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class RbfAnnotations {

    private final Class<? extends Annotation> dataTypeAnnotationClass;

    private final Class<? extends Annotation> fieldAnnotationClass;

    private final Class<? extends Annotation> subRecordAnnotationClass;

    private final Class<? extends Annotation> subRecordListAnnotationClass;

    /**
     * Constructs a new <code>RbfAnnotations</code> object.
     * 
     * @param dataTypeAnnotationClass the data type annotation class
     * @param fieldAnnotationClass the field annotation class
     * @param subRecordAnnotationClass the sub record annotation class
     * @param subRecordListAnnotationClass the sub record list annotation class
     */
    public RbfAnnotations(Class<? extends Annotation> dataTypeAnnotationClass,
            Class<? extends Annotation> fieldAnnotationClass,
            Class<? extends Annotation> subRecordAnnotationClass,
            Class<? extends Annotation> subRecordListAnnotationClass) {
        this.dataTypeAnnotationClass = dataTypeAnnotationClass;
        this.fieldAnnotationClass = fieldAnnotationClass;
        this.subRecordAnnotationClass = subRecordAnnotationClass;
        this.subRecordListAnnotationClass = subRecordListAnnotationClass;
    }

    /**
     * Returns the data type annotation class.
     * 
     * @return the data type annotation class
     */
    public Class<? extends Annotation> getDataTypeAnnotationClass() {
        return dataTypeAnnotationClass;
    }

    /**
     * Returns the field annotation class.
     * 
     * @return the field annotation class.
     */
    public Class<? extends Annotation> getFieldAnnotationClass() {
        return fieldAnnotationClass;
    }

    /**
     * Returns the sub record annotation class.
     * 
     * @return the sub record annotation class
     */
    public Class<? extends Annotation> getSubRecordAnnotationClass() {
        return subRecordAnnotationClass;
    }

    /**
     * Returns the sub record list annotation class.
     * 
     * @return the sub record list annotation class.
     */
    public Class<? extends Annotation> getSubRecordListAnnotationClass() {
        return subRecordListAnnotationClass;
    }

}
