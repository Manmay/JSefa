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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.jsefa.common.util.ReflectionUtil;

/**
 * Provider for values of annotation parameters. It provides a simple solution to the following drawbacks of
 * annotations: <br>
 * 1) Annotations can not extend an interface so that it is not possible to express that several annotations do
 * provide the same kind of data.<br>
 * 2) Annotated parameter values can not be set to null.
 * <p>
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class AnnotationDataProvider {

    private static final List<?> NULL_OBJECTS = Arrays.asList(new Object[]{"", NoConverterType.class,
            NoValidatorType.class, NoClass.class});

    /**
     * Returns the value of the annotation parameter with the given name of the given annotation or null if it is
     * to be interpreted as null (as the empty <code>String</code>, an empty array or special classes used as
     * defaults for the annotation parameter value).
     * 
     * @param <T> the expected type of the data to return
     * @param annotation the annotation
     * @param annotationParameterName the annotation parameter name
     * @return the annotation parameter value.
     */
    public static <T> T get(Annotation annotation, String annotationParameterName) {
        T result = ReflectionUtil.<T> callMethod(annotation, annotationParameterName);
        if (NULL_OBJECTS.contains(result)) {
            return null;
        }
        if (Object[].class.isAssignableFrom(result.getClass())) {
            Object[] resultArray = (Object[]) result;
            if (resultArray.length == 0) {
                return null;
            }
        }
        return result;
    }

    /**
     * Returns the value of the annotation parameter with the given name of the annotation the given
     * annotatedElement is annotated with and which class is one of the given annotation classes. Returns null, if
     * the annotation parameter value is to be interpreted as null (as the empty <code>String</code> or special
     * classes used as defaults for the annotation parameter value).
     * 
     * @param <T> the expected type of the returned data
     * @param annotatedElement the annotated element
     * @param annotationParameterName the name of the annotation parameter
     * @param annotationClasses the annotation classes
     * @return the annotation parameter value
     */
    public static <T> T get(AnnotatedElement annotatedElement, String annotationParameterName,
            Class<? extends Annotation>... annotationClasses) {
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            if (hasParameter(annotationClass, annotationParameterName)) {
                if (annotatedElement.isAnnotationPresent(annotationClass)) {
                    return AnnotationDataProvider.<T> get(annotatedElement.getAnnotation(annotationClass),
                            annotationParameterName);
                }
            }
        }
        return null;
    }

    /**
     * Returns true if the annotation class has an annotation parameter with the given name; false otherwise.
     * 
     * @param annotationClass the annotation class
     * @param annotationParameterName the annotation parameter name
     * @return true if the annotation class has an annotation parameter with the given name; false otherwise.
     */
    public static boolean hasParameter(Class<? extends Annotation> annotationClass, String annotationParameterName) {
        Method method = ReflectionUtil.getMethod(annotationClass, annotationParameterName);
        return method != null;
    }

    private AnnotationDataProvider() {

    }

}
