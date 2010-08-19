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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jsefa.common.converter.XmlDateTimeConverter;
import org.jsefa.test.common.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.MapKey;
import org.jsefa.xml.annotation.MapValue;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlElementMap;
import org.jsefa.xml.annotation.XmlTextContent;

/**
 * Tests for testing the configurability of simple types (concerning format and/or converterType) for different xml node
 * types.
 * 
 * @author Marko Kovacevic
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
     * Tests the converter type configuration for an attribute.
     */
    public void testAttributeWithConverterType() {
        AttributeWithConverterTypeTestDTO obj = new AttributeWithConverterTypeTestDTO();
        obj.attribute = getDateForConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter type configuration for an attribute.
     */
    public void testAttributeWithFormatAndConverterType() {
        AttributeWithFormatAndConverterTypeTestDTO obj = new AttributeWithFormatAndConverterTypeTestDTO();
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
     * Tests the converter type configuration for an element.
     */
    public void testElementWithConverterType() {
        ElementWithConverterTypeTestDTO obj = new ElementWithConverterTypeTestDTO();
        obj.element = getDateForConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter type configuration for an element.
     */
    public void testElementWithFormatAndConverterType() {
        ElementWithFormatAndConverterTypeTestDTO obj = new ElementWithFormatAndConverterTypeTestDTO();
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
     * Tests the converter type configuration for a text content.
     */
    public void testTextContentWithConverterTypeDTO() {
        TextContentWithConverterTypeTestDTO obj = new TextContentWithConverterTypeTestDTO();
        obj.textContent = getDateForConverter("28.02.2007:15:21:27");
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter type configuration for a text content.
     */
    public void testTextContentWithFormatAndConverterTypeDTO() {
        TextContentWithFormatAndConverterTypeTestDTO obj = new TextContentWithFormatAndConverterTypeTestDTO();
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
     * Tests the converter type configuration for an element list.
     */
    public void testElementListWithConverterType() {
        ElementListWithConverterTypeTestDTO obj = new ElementListWithConverterTypeTestDTO();
        obj.elementList = new ArrayList<Date>();
        obj.elementList.add(getDateForConverter("28.02.2007:15:21:27"));
        obj.elementList.add(getDateForConverter("01.03.2007:15:21:27"));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-03-01T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter type configuration for an element list.
     */
    public void testElementListWithFormatAndConverterType() {
        ElementListWithFormatAndConverterTypeTestDTO obj = new ElementListWithFormatAndConverterTypeTestDTO();
        obj.elementList = new ArrayList<Date>();
        obj.elementList.add(getDateForFormatAndConverter("28.02.2007:15:21:27"));
        obj.elementList.add(getDateForFormatAndConverter("01.03.2007:15:21:27"));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T14:21:27.000-01:00") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-03-01T14:21:27.000-01:00") >= 0);
    }

    /**
     * Tests the format configuration for an element map.
     */
    public void testElementMapWithFormat() {
        ElementMapWithFormatTestDTO obj = new ElementMapWithFormatTestDTO();
        obj.map = new HashMap<Date, BigDecimal>();
        obj.map.put(getDateForFormat("28.02.2007:15:21:27"), new BigDecimal(3.14));
        obj.map.put(getDateForFormat("01.03.2007:15:21:27"), new BigDecimal(0.815));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("28.02.2007:15:21:27") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("3.140") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("01.03.2007:15:21:27") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("0.8150") >= 0);
    }
    /**
     * Tests the converter type configuration for an element map.
     */
    public void testElementMapWithConverterType() {
        ElementMapWithConverterTypeTestDTO obj = new ElementMapWithConverterTypeTestDTO();
        obj.map = new HashMap<Date, BigDecimal>();
        obj.map.put(getDateForConverter("28.02.2007:15:21:27"), new BigDecimal(3.14));
        obj.map.put(getDateForConverter("01.03.2007:15:21:27"), new BigDecimal(0.815));
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-02-28T15:21:27.000Z") >= 0);
        assertTrue(JSefaTestUtil.serialize(XML, obj).indexOf("2007-03-01T15:21:27.000Z") >= 0);
    }

    /**
     * Tests the format and converter type configuration for an element map.
     */
    public void testElementMapWithFormatAndConverterType() {
        ElementMapWithFormatAndConverterTypeTestDTO obj = new ElementMapWithFormatAndConverterTypeTestDTO();
        obj.map = new HashMap<Date, BigDecimal>();
        obj.map.put(getDateForFormatAndConverter("28.02.2007:15:21:27"), new BigDecimal(3.14));
        obj.map.put(getDateForFormatAndConverter("01.03.2007:15:21:27"), new BigDecimal(0.815));
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
    static final class AttributeWithConverterTypeTestDTO {
        @XmlAttribute(converterType = XmlDateTimeConverter.class)
        Date attribute;
    }

    @XmlDataType()
    static final class AttributeWithFormatAndConverterTypeTestDTO {
        @XmlAttribute(format = "GMT-1:00", converterType = XmlDateTimeConverter.class)
        Date attribute;
    }

    @XmlDataType()
    static final class ElementWithFormatTestDTO {
        @XmlElement(format = "dd.MM.yyyy:HH:mm:ss")
        Date element;
    }

    @XmlDataType()
    static final class ElementWithConverterTypeTestDTO {
        @XmlElement(converterType = XmlDateTimeConverter.class)
        Date element;
    }

    @XmlDataType()
    static final class ElementWithFormatAndConverterTypeTestDTO {
        @XmlElement(format = "GMT-1:00", converterType = XmlDateTimeConverter.class)
        Date element;
    }

    @XmlDataType()
    static final class TextContentWithFormatTestDTO {
        @XmlTextContent(format = "dd.MM.yyyy:HH:mm:ss")
        Date textContent;
    }

    @XmlDataType()
    static final class TextContentWithConverterTypeTestDTO {
        @XmlTextContent(converterType = XmlDateTimeConverter.class)
        Date textContent;
    }

    @XmlDataType()
    static final class TextContentWithFormatAndConverterTypeTestDTO {
        @XmlTextContent(format = "GMT-1:00", converterType = XmlDateTimeConverter.class)
        Date textContent;
    }

    @XmlDataType()
    static final class ElementListWithFormatTestDTO {
        @XmlElementList(items = @ListItem(name = "item", format = "dd.MM.yyyy:HH:mm:ss"))
        List<Date> elementList;
    }

    @XmlDataType()
    static final class ElementListWithConverterTypeTestDTO {
        @XmlElementList(items = @ListItem(name = "item", converterType = XmlDateTimeConverter.class))
        List<Date> elementList;
    }

    @XmlDataType()
    static final class ElementListWithFormatAndConverterTypeTestDTO {
        @XmlElementList(items = @ListItem(name = "item", format = "GMT-1:00",
                converterType = XmlDateTimeConverter.class))
        List<Date> elementList;
    }

    @XmlDataType()
    static final class ElementMapWithFormatTestDTO {
        @XmlElementMap(key = @MapKey(name = "key", format = "dd.MM.yyyy:HH:mm:ss"),
                values = @MapValue(name = "value", format = {
                "en", "#0.0000" }))
        Map<Date, BigDecimal> map;
    }

    @XmlDataType()
    static final class ElementMapWithConverterTypeTestDTO {
        @XmlElementMap(key = @MapKey(name = "key", converterType = XmlDateTimeConverter.class),
                values = @MapValue(name = "value", format = {
                "en", "#0.0000" }))
        Map<Date, BigDecimal> map;
    }

    @XmlDataType()
    static final class ElementMapWithFormatAndConverterTypeTestDTO {
        @XmlElementMap(key = @MapKey(name = "key", format = "GMT-1:00", converterType = XmlDateTimeConverter.class),
                values = @MapValue(name = "value", format = {
                "en", "#0.0000" }))
        Map<Date, BigDecimal> map;
    }

}
