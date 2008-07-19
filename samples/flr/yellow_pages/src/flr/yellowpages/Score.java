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

package flr.yellowpages;

import java.math.BigDecimal;

import org.jsefa.flr.annotation.FlrDataType;
import org.jsefa.flr.annotation.FlrField;

/**
 * DTO describing a typed score.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
@FlrDataType(defaultPrefix = "SCO")
public class Score {
    @FlrField(pos = 1, length = 12)
    String type;

    @FlrField(pos = 2, length = 4)
    BigDecimal value;

}
