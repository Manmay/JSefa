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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import junit.framework.TestCase;

import org.jsefa.test.common.AbstractTestDTO;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;

/**
 * Tests to test element lists with <code>String</code> elements and simple list type elements but with a non
 * list java field type.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class CollectionsTest extends TestCase {

    /**
     * Tests an element list for a field of type <code>Set</code>.
     */
    public void testElementListForSet() {
        ElementListWithSetField list = new ElementListWithSetField();
        list.set = new HashSet<String>();
        for (int i = 0; i < 10; i++) {
            list.set.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    /**
     * Tests an element list for a field of type <code>Queue</code>.
     */
    public void testElementListForQueue() {
        ElementListWithQueueField list = new ElementListWithQueueField();
        list.queue = new LinkedList<String>();
        for (int i = 0; i < 10; i++) {
            list.queue.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    /**
     * Tests a simple list for a field of type <code>Set</code>.
     */
    public void testSimpleListForSet() {
        SimpleListWithSetField list = new SimpleListWithSetField();
        list.set = new HashSet<String>();
        for (int i = 0; i < 10; i++) {
            list.set.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }
    
    /**
     * Tests a simple list for a field of type <code>Queue</code>.
     */
    public void testSimpleListForQueue() {
        SimpleListWithQueueField list = new SimpleListWithQueueField();
        list.queue = new LinkedList<String>();
        for (int i = 0; i < 10; i++) {
            list.queue.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    @XmlDataType()
    static final class ElementListWithSetField extends AbstractTestDTO {
        @XmlElementList(implicit = false, items = @ListItem(name = "item"))
        Set<String> set;
    }

    @XmlDataType()
    static final class ElementListWithQueueField extends AbstractTestDTO {
        @XmlElementList(implicit = false, items = @ListItem(name = "item"))
        Queue<String> queue;
    }

    @XmlDataType()
    static final class SimpleListWithSetField extends AbstractTestDTO {
        @XmlElement
        Set<String> set;
    }

    @XmlDataType()
    static final class SimpleListWithQueueField extends AbstractTestDTO {
        @XmlElement
        Queue<String> queue;
    }

}
