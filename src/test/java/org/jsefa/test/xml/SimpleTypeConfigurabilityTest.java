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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

import static org.jsefa.test.common.JSefaTestUtil.FormatType.XML;

import org.jsefa.common.converter.XmlDateTimeConverter;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests for testing the configurability of simple types (concerning format
 * and/or converterClass) for different xml node types.
 * 
 * @author Marko Kovacevic
 * 
 */
public class SimpleTypeConfigurabilityTest extends TestCase {

    /**
     * Tests the format configuration for an attribute.
     */
    public void testAttributeWithFormat() {
        AttributeWithFormatTestDTO obj = new AttributeWithFormatTestDTO();
        obj.attribute = getDateForFormat("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("28.02.2007:15:21:27") >= 0);
    }

    /**
     * Tests the converter class configuration for an attribute.
     */
    public void testAttributeWithConverterClass() {
        AttributeWithConverterClassTestDTO obj = new AttributeWithConverterClassTestDTO();
        obj.attribute = getDateForConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter class configuration for an attribute.
     */
    public void testAttributeWithFormatAndConverterClass() {
        AttributeWithFormatAndConverterClassTestDTO obj = new AttributeWithFormatAndConverterClassTestDTO();
        obj.attribute = getDateForFormatAndConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T14:21:27.000-01:00") >= 0);
    }

    /**
     * Tests the format configuration for an element.
     */
    public void testElementWithFormat() {
        ElementWithFormatTestDTO obj = new ElementWithFormatTestDTO();
        obj.element = getDateForFormat("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("28.02.2007:15:21:27") >= 0);
    }

    /**
     * Tests the converter class configuration for an element.
     */
    public void testElementWithConverterClass() {
        ElementWithConverterClassTestDTO obj = new ElementWithConverterClassTestDTO();
        obj.element = getDateForConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter class configuration for an element.
     */
    public void testElementWithFormatAndConverterClass() {
        ElementWithFormatAndConverterClassTestDTO obj = new ElementWithFormatAndConverterClassTestDTO();
        obj.element = getDateForFormatAndConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T14:21:27.000-01:00") >= 0);
    }

    /**
     * Tests the format configuration for a text content.
     */
    public void testTextContentWithFormat() {
        TextContentWithFormatTestDTO obj = new TextContentWithFormatTestDTO();
        obj.textContent = getDateForFormat("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("28.02.2007:15:21:27") >= 0);
    }

    /**
     * Tests the converter class configuration for a text content.
     */
    public void testTextContentWithConverterClassDTO() {
        TextContentWithConverterClassTestDTO obj = new TextContentWithConverterClassTestDTO();
        obj.textContent = getDateForConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter class configuration for a text content.
     */
    public void testTextContentWithFormatAndConverterClassDTO() {
        TextContentWithFormatAndConverterClassTestDTO obj = new TextContentWithFormatAndConverterClassTestDTO();
        obj.textContent = getDateForFormatAndConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T14:21:27.000-01:00") >= 0);
    }

    /**
     * Tests the format configuration for an element list.
     */
    public void testElementListWithFormat() {
        ElementListWithFormatTestDTO obj = new ElementListWithFormatTestDTO();
        obj.elementList = new ArrayList<Date>();
        obj.elementList.add(getDateForFormat("28.02.2007:15:21:27"));
        obj.elementList.add(getDateForFormat("01.03.2007:15:21:27"));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("28.02.2007:15:21:27") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("01.03.2007:15:21:27") >= 0);
    }

    /**
     * Tests the converter class configuration for an element list.
     */
    public void testElementListWithConverterClass() {
        ElementListWithConverterClassTestDTO obj = new ElementListWithConverterClassTestDTO();
        obj.elementList = new ArrayList<Date>();
        obj.elementList.add(getDateForConverter("28.02.2007:15:21:27"));
        obj.elementList.add(getDateForConverter("01.03.2007:15:21:27"));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-03-01T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter class configuration for an element list.
     */
    public void testElementListWithFormatAndConverterClass() {
        ElementListWithFormatAndConverterClassTestDTO obj = new ElementListWithFormatAndConverterClassTestDTO();
        obj.elementList = new ArrayList<Date>();
        obj.elementList.add(getDateForFormatAndConverter("28.02.2007:15:21:27"));
        obj.elementList.add(getDateForFormatAndConverter("01.03.2007:15:21:27"));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T14:21:27.000-01:00") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-03-01T14:21:27.000-01:00") >= 0);
    }
    private Date getDateForFormat(String inputString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
        try {
            return dateFormat.parse(inputString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getDateForConverter(String inputString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(XmlDateTimeConverter.DEFAULT_FORMAT));
        try {
            return dateFormat.parse(inputString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getDateForFormatAndConverter(String inputString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy:HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(XmlDateTimeConverter.DEFAULT_FORMAT));
        try {
            return dateFormat.parse(inputString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @XmlDataType()
    static final class AttributeWithFormatTestDTO {
        @XmlAttribute(format = "dd.MM.yyyy:HH:mm:ss")
        Date attribute;
    }

    @XmlDataType()
    static final class AttributeWithConverterClassTestDTO {
        @XmlAttribute(converterClass = XmlDateTimeConverter.class)
        Date attribute;
    }

    @XmlDataType()
    static final class AttributeWithFormatAndConverterClassTestDTO {
        @XmlAttribute(format = "GMT-1:00", converterClass = XmlDateTimeConverter.class)
        Date attribute;
    }

    @XmlDataType()
    static final class ElementWithFormatTestDTO {
        @XmlElement(format = "dd.MM.yyyy:HH:mm:ss")
        Date element;
    }

    @XmlDataType()
    static final class ElementWithConverterClassTestDTO {
        @XmlElement(converterClass = XmlDateTimeConverter.class)
        Date element;
    }

    @XmlDataType()
    static final class ElementWithFormatAndConverterClassTestDTO {
        @XmlElement(format = "GMT-1:00", converterClass = XmlDateTimeConverter.class)
        Date element;
    }

    @XmlDataType()
    static final class TextContentWithFormatTestDTO {
        @XmlTextContent(format = "dd.MM.yyyy:HH:mm:ss")
        Date textContent;
    }

    @XmlDataType()
    static final class TextContentWithConverterClassTestDTO {
        @XmlTextContent(converterClass = XmlDateTimeConverter.class)
        Date textContent;
    }

    @XmlDataType()
    static final class TextContentWithFormatAndConverterClassTestDTO {
        @XmlTextContent(format = "GMT-1:00", converterClass = XmlDateTimeConverter.class)
        Date textContent;
    }

    @XmlDataType()
    static final class ElementListWithFormatTestDTO {
        @XmlElementList(items = @ListItem(name = "item", format = "dd.MM.yyyy:HH:mm:ss"))
        List<Date> elementList;
    }

    @XmlDataType()
    static final class ElementListWithConverterClassTestDTO {
        @XmlElementList(items = @ListItem(name = "item", converterClass = XmlDateTimeConverter.class))
        List<Date> elementList;
    }

    @XmlDataType()
    static final class ElementListWithFormatAndConverterClassTestDTO {
        @XmlElementList(items = @ListItem(name = "item", format = "GMT-1:00",
                converterClass = XmlDateTimeConverter.class))
        List<Date> elementList;
    }
}