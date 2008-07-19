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

package org.jsefa.common.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * A validation result.
 * 
 * @author Norman Lahme-Huetig
 */
public final class ValidationResult implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private final Collection<ValidationError> errors;
    
    /**
     * The single <code>ValidationResult</code> to be returned if a given value is valid.
     */
    @SuppressWarnings("unchecked")
    public static final ValidationResult VALID = new ValidationResult(Collections.EMPTY_LIST);
    
    /**
     * Creates a <code>ValidationResult</code>. If no error is given, {@link #VALID} is returned.
     * @param errors the errors - if any
     * @return a <code>ValidationResult</code>
     */
    public static ValidationResult create(ValidationError... errors) {
        if (errors.length == 0) {
            return VALID;
        }
        return new ValidationResult(Arrays.asList(errors));
    }
    
    /**
     * Creates a <code>ValidationResult</code>. If no error is given, {@link #VALID} is returned.
     * @param errors the errors - if any
     * @return a <code>ValidationResult</code>
     */
    public static ValidationResult create(Collection<ValidationError> errors) {
        if (errors == null || errors.size() == 0) {
            return VALID;
        }
        return new ValidationResult(new ArrayList<ValidationError>(errors));
    }

    private ValidationResult(Collection<ValidationError> errors)  {
        this.errors = errors;
    }
    
    /**
     * @return true, if no error occurred during validation; false otherwise.
     */
    public boolean isValid() {
        return this.errors.isEmpty();
    }
    
    /**
     * @return an unmodifiable collection of errors (may be empty but not null).
     */
    public Collection<ValidationError> getErrors() {
        return Collections.unmodifiableCollection(this.errors);
    }
    
}
