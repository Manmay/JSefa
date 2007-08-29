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

package org.jsefa.test.xml;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElementList;

/**
 * Test to test lists whereas the type of the list entries is determinable from
 * the generic argument of the list field.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class ShortSyntaxListTypeTest extends TestCase {

    /**
     * Tests a list with <code>String</code> and <code>Integer</code> elements.
     */
    public void testExplicitListWithStringElements() {
        ExplicitListWithStringElements list = new ExplicitListWithStringElements();
        list.list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.list.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    @XmlDataType()
    static final class ExplicitListWithStringElements extends AbstractTestDTO {
        @XmlElementList(items = @ListItem(name = "item"))
        List<String> list;
    }


    /**
     * Tests a list with <code>String</code> and <code>Integer</code> elements.
     */
    public void testImplicitListWithStringElements() {
        ImplicitListWithStringElements list = new ImplicitListWithStringElements();
        list.list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.list.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    @XmlDataType()
    static final class ImplicitListWithStringElements extends AbstractTestDTO {
        @XmlElementList(implicit = true, items = @ListItem(name = "item"))
        List<String> list;
    }
}
