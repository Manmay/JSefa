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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jsefa.common.util.ReflectionUtil;

/**
 * Provider for anotated fields.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class AnnotatedFieldsProvider {

    /**
     * Returns a list of fields which are annotated in the given <code>objectType</code> with one of the given
     * annotation classes. The fields are sorted according to their respective position values with the following
     * semantics:
     * <p>
     * If the position of a field A is less than the position of a field B, than field A comes (not necessarily
     * directly) before field B.<br>
     * All fields with the default position value (-1) will come after all fields with explicit positions.
     * 
     * @param objectType the object type
     * @param annotationClasses the annotation classes
     * @return a sorted list of fields
     */
    public static List<Field> getSortedAnnotatedFields(Class<?> objectType,
            Class<? extends Annotation>... annotationClasses) {
        List<Field> sortedFields = new ArrayList<Field>();
        for (Class<?> theClass : ReflectionUtil.getTypesInReverseOrder(objectType)) {
            List<Field> flrFields = getDeclaredFields(theClass, annotationClasses);
            SortedMap<Integer, Field> fieldMap = new TreeMap<Integer, Field>();
            for (Field field : flrFields) {
                Integer pos = AnnotationDataProvider.get(field, AnnotationParameterNames.POS, annotationClasses);
                if (pos != null && pos >= 0) {
                    if (fieldMap.get(pos) != null) {
                        throw new AnnotationException("There are more than one field in " + objectType.getName()
                                + " annotated with the same pos " + pos);
                    }
                    fieldMap.put(pos, field);
                }
            }
            for (Field field : fieldMap.values()) {
                sortedFields.add(field);
            }
            // add all fields with default pos to the end of the list of sorted
            // fields
            for (Field field : flrFields) {
                if (!sortedFields.contains(field)) {
                    sortedFields.add(field);
                }
            }
        }
        return sortedFields;
    }

    /**
     * Returns all fields of the given object type or one of its ancestors which are annotated with at least one of
     * the given annotation classes.<br>
     * Note: there is no commitment to a specific order for the returned list of fields.
     * 
     * @param objectType the object type
     * @param annotationClasses the annotation classes
     * @return the list of fields.
     */
    public static List<Field> getAnnotatedFields(Class<?> objectType,
            Class<? extends Annotation>... annotationClasses) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> theClass : ReflectionUtil.getTypesInReverseOrder(objectType)) {
            fields.addAll(getDeclaredFields(theClass, annotationClasses));
        }
        return fields;
    }

    private static List<Field> getDeclaredFields(Class<?> objectType,
            Class<? extends Annotation>... annotationClasses) {
        List<Field> result = new ArrayList<Field>();
        for (Field field : objectType.getDeclaredFields()) {
            for (Class<? extends Annotation> annotationClass : annotationClasses) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
        }
        return result;
    }

    private AnnotatedFieldsProvider() {

    }

}
