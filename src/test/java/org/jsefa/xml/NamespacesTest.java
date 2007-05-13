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

package org.jsefa.xml;

import static org.jsefa.JSefaTestUtil.FormatType.XML;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
import org.jsefa.xml.annotation.ListItem;
import org.jsefa.xml.annotation.Namespace;
import org.jsefa.xml.annotation.XmlAttribute;
import org.jsefa.xml.annotation.XmlDataType;
import org.jsefa.xml.annotation.XmlElement;
import org.jsefa.xml.annotation.XmlElementList;
import org.jsefa.xml.annotation.XmlNamespaces;


/**
 * Tests to correct serialization/deserialization with different namespace declaration scenarios.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 * 
 */
public class NamespacesTest extends TestCase {

    /**
     * Tests the case when no namespace is given.
     */
    public void testNoNamespace() {
        NoNamespaceTestDTO obj = new NoNamespaceTestDTO();
        obj.attribute = "a";
        obj.element = "b";
        obj.elementList.add("c");
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(getAttributeNamespace(serializationResult, "attribute").equals(XmlConstants.NO_NAMESPACE_URI));
        assertTrue(getElementNamespace(serializationResult, "element").equals(XmlConstants.NO_NAMESPACE_URI));
        assertTrue(getElementNamespace(serializationResult, "elementList").equals(XmlConstants.NO_NAMESPACE_URI));
        assertTrue(getElementNamespace(serializationResult, "item").equals(XmlConstants.NO_NAMESPACE_URI));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Tests the case when a default namespace is given.
     */
    public void testDefaultNamespace() {
        DefaultNamespaceTestDTO obj = new DefaultNamespaceTestDTO();
        obj.attribute = "a";
        obj.element = "b";
        obj.elementList.add("c");
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(getAttributeNamespace(serializationResult, "attribute").equals(XmlConstants.NO_NAMESPACE_URI));
        assertTrue(getElementNamespace(serializationResult, "element").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "elementList").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "item").equals("uriA"));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Tests the case when an explicit namespace is given.
     */
    public void testExplicitNamespace() {
        ExplicitNamespaceTestDTO obj = new ExplicitNamespaceTestDTO();
        obj.attribute = "a";
        obj.element = "b";
        obj.elementList.add("c");
        String serializationResult = JSefaTestUtil.serialize(XML, obj);
        assertTrue(getAttributeNamespace(serializationResult, "attribute").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "element").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "elementList").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "item").equals("uriA"));
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Tests the case when 2 default namespaces are given with the second
     * overwriting the first.
     */
    public void testOverwrittenDefaultNamespace() {
        DefaultNamespaceOverridingTestParentDTO obj = new DefaultNamespaceOverridingTestParentDTO();
        obj.attribute = "a";
        obj.element = "b";
        obj.elementList.add("c");
        obj.element2 = new DefaultNamespaceOverridingTestChildDTO();
        obj.element2.attributeB = "d";
        obj.element2.elementB = "e";
        obj.element2.elementListB.add("f");
        String serializationResult = JSefaTestUtil.serialize(XML, obj);

        assertTrue(getAttributeNamespace(serializationResult, "attribute").equals(XmlConstants.NO_NAMESPACE_URI));
        assertTrue(getElementNamespace(serializationResult, "element").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "elementList").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "item").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "element2").equals("uriA"));

        assertTrue(getAttributeNamespace(serializationResult, "attributeB").equals(XmlConstants.NO_NAMESPACE_URI));
        assertTrue(getElementNamespace(serializationResult, "elementB").equals("uriB"));
        assertTrue(getElementNamespace(serializationResult, "elementListB").equals("uriB"));
        assertTrue(getElementNamespace(serializationResult, "itemB").equals("uriB"));

        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }

    /**
     * Tests the case when 2 explicit namespaces are given with the second
     * overwriting the first.
     */
    public void testOverwrittenExplicitNamespace() {
        ExplicitNamespaceOverridingTestParentDTO obj = new ExplicitNamespaceOverridingTestParentDTO();
        obj.attribute = "a";
        obj.element = "b";
        obj.elementList.add("c");
        obj.element2 = new ExplicitNamespaceOverridingTestChildDTO();
        obj.element2.attributeB = "d";
        obj.element2.elementB = "e";
        obj.element2.elementListB.add("f");
        String serializationResult = JSefaTestUtil.serialize(XML, obj);

        assertTrue(getAttributeNamespace(serializationResult, "attribute").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "element").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "elementList").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "item").equals("uriA"));
        assertTrue(getElementNamespace(serializationResult, "element2").equals("uriA"));

        assertTrue(getAttributeNamespace(serializationResult, "attributeB").equals("uriB"));
        assertTrue(getElementNamespace(serializationResult, "elementB").equals("uriB"));
        assertTrue(getElementNamespace(serializationResult, "elementListB").equals("uriB"));
        assertTrue(getElementNamespace(serializationResult, "itemB").equals("uriB"));

        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, obj);
    }
    
    private String getElementNamespace(String inputString, String localName) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            StringReader reader = new StringReader(inputString);
            XMLStreamReader streamReader = factory.createXMLStreamReader(reader);
            while (streamReader.hasNext()) {
                int event = streamReader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    javax.xml.namespace.QName qname = streamReader.getName();
                    if (qname.getLocalPart().equals(localName)) {
                        return qname.getNamespaceURI();
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getAttributeNamespace(String inputString, String localName) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            StringReader reader = new StringReader(inputString);
            XMLStreamReader streamReader = factory.createXMLStreamReader(reader);
            while (streamReader.hasNext()) {
                int event = streamReader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    for (int i = 0; i < streamReader.getAttributeCount(); i++) {
                        javax.xml.namespace.QName qname = streamReader.getAttributeName(i);
                        if (qname.getLocalPart().equals(localName)) {
                            return qname.getNamespaceURI();
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @XmlDataType()
    static final class NoNamespaceTestDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attribute;

        @XmlElement()
        String element;

        @XmlElementList(implicit = false, items = {@ListItem(name = "item", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();
    }

    @XmlNamespaces(@Namespace(uri = "uriA"))
    @XmlDataType()
    static final class DefaultNamespaceTestDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attribute;

        @XmlElement()
        String element;

        @XmlElementList(implicit = false, items = {@ListItem(name = "item", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();
    }

    @XmlNamespaces(@Namespace(prefix = "a", uri = "uriA"))
    @XmlDataType()
    static final class ExplicitNamespaceTestDTO extends AbstractTestDTO {
        @XmlAttribute(name = "a:attribute")
        String attribute;

        @XmlElement(name = "a:element")
        String element;

        @XmlElementList(name = "a:elementList", implicit = false, 
                items = {@ListItem(name = "a:item", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();
    }

    @XmlNamespaces(@Namespace(uri = "uriA"))
    @XmlDataType()
    static final class DefaultNamespaceOverridingTestParentDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attribute;

        @XmlElement()
        String element;

        @XmlElementList(implicit = false, items = {@ListItem(name = "item", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();

        @XmlElement()
        DefaultNamespaceOverridingTestChildDTO element2;
    }

    @XmlNamespaces(@Namespace(uri = "uriB"))
    @XmlDataType()
    static final class DefaultNamespaceOverridingTestChildDTO extends AbstractTestDTO {
        @XmlAttribute()
        String attributeB;

        @XmlElement()
        String elementB;

        @XmlElementList(implicit = false, items = {@ListItem(name = "itemB", objectType = String.class)})
        List<String> elementListB = new ArrayList<String>();
    }

    @XmlNamespaces(@Namespace(prefix = "a", uri = "uriA"))
    @XmlDataType()
    static final class ExplicitNamespaceOverridingTestParentDTO extends AbstractTestDTO {
        @XmlAttribute(name = "a:attribute")
        String attribute;

        @XmlElement(name = "a:element")
        String element;

        @XmlElementList(name = "a:elementList", implicit = false, 
                items = {@ListItem(name = "a:item", objectType = String.class)})
        List<String> elementList = new ArrayList<String>();

        @XmlElement(name = "a:element2")
        ExplicitNamespaceOverridingTestChildDTO element2;
    }

    @XmlNamespaces(@Namespace(prefix = "a", uri = "uriB"))
    @XmlDataType()
    static final class ExplicitNamespaceOverridingTestChildDTO extends AbstractTestDTO {
        @XmlAttribute(name = "a:attributeB")
        String attributeB;

        @XmlElement(name = "a:elementB")
        String elementB;

        @XmlElementList(name = "a:elementListB", implicit = false, 
                items = {@ListItem(name = "a:itemB", objectType = String.class)})
        List<String> elementListB = new ArrayList<String>();
    }

}
