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

package org.jsefa.common.lowlevel.filter;
import static org.jsefa.common.lowlevel.filter.FilterResult.*;

/**
 * A filter for filtering out header and footer lines.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Norman Lahme-Huetig
 */
public final class HeaderAndFooterFilter implements LineFilter {
    private final int headerSize;
    private final boolean filterLastLine;
    private final boolean storeFailedLines;
    
    /**
     * Constructs a new <code>HeaderAndFooterFilter</code>.
     * @param headerSize the size of the header to skip, i. e. number of lines
     * @param hasFooter true, if there is a footer to skip; false otherwise
     * @param storeFailedLines true, if the lines which fail the filter should be stored; false otherwise
     */
    public HeaderAndFooterFilter(int headerSize, boolean hasFooter, boolean storeFailedLines) {
        this.headerSize = headerSize;
        this.filterLastLine = hasFooter;
        this.storeFailedLines = storeFailedLines;
    }

    /**
     * {@inheritDoc}
     */
    public FilterResult filter(String line, int lineNumber, boolean truncated, boolean lastLine) {
        if ((lineNumber > headerSize) && (!filterLastLine || !lastLine)) {
            return PASSED;
        } else if (this.storeFailedLines) {
            return FAILED_BUT_STORE;
        } else {
            return FAILED;
        }
    }

}
