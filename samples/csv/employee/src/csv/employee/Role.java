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

package csv.employee;

import org.jsefa.common.annotation.EnumConstant;

/**
 * Enum for different roles an employee plays within a department.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
enum Role {
    @EnumConstant(displayName = "senior developer")
    SENIOR_DEVELOPER,

    @EnumConstant(displayName = "junior developer")
    JUNIOR_DEVELOPER,

    @EnumConstant(displayName = "account manager")
    ACCOUNT_MANAGER,

    @EnumConstant(displayName = "key account manager")
    KEY_ACCOUNT_MANAGER;
}
