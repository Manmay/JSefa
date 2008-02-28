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

package org.jsefa.flr.lowlevel.config;

import org.jsefa.flr.lowlevel.FlrLowLevelDeserializer;
import org.jsefa.flr.lowlevel.FlrLowLevelSerializer;
import org.jsefa.rbf.lowlevel.config.RbfLowLevelConfiguration;

/**
 * Configuration object for creating a {@link FlrLowLevelSerializer} or {@link FlrLowLevelDeserializer}. It uses
 * lazy initialization.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class FlrLowLevelConfiguration extends RbfLowLevelConfiguration {

    /**
     * Constructs a new <code>FlrLowLevelConfiguration</code>.
     */
    public FlrLowLevelConfiguration() {
    }

    private FlrLowLevelConfiguration(FlrLowLevelConfiguration other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     */
    public FlrLowLevelConfiguration createCopy() {
        return new FlrLowLevelConfiguration(this);
    }

}
