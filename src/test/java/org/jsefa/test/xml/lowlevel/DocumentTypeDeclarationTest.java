package org.jsefa.test.xml.lowlevel;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.jsefa.xml.lowlevel.StaxBasedXmlLowLevelSerializer;
import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;
import org.jsefa.xml.namespace.QName;

/**
 * Tests to test the correct serialization of DOCTYPE declarations ({@link StaxBasedXmlLowLevelSerializer}).
 * 
 * @author Norman Lahme-Huetig
 */
public class DocumentTypeDeclarationTest extends TestCase {

    /**
     * Tests it with no namespace and no public id.
     */
    public void testWithoutNamespacesAndWithoutPublicId() {
        String xml = createXML(QName.create("test"), null, "http://www.jsefa.org/xml/dtd/test");
        assertTrue(xml.indexOf("<!DOCTYPE test SYSTEM \"http://www.jsefa.org/xml/dtd/test\">") > -1);
    }

    /**
     * Tests it with no namespace but with a public id.
     */
    public void testWithoutNamespacesAndWithPublicId() {
        String xml = createXML(QName.create("test"), "-//JSEFA//DTD//test//EN", "http://www.jsefa.org/xml/dtd/test");
        assertTrue(xml.indexOf(
                "<!DOCTYPE test PUBLIC \"-//JSEFA//DTD//test//EN\" \"http://www.jsefa.org/xml/dtd/test\">") > -1);
    }

    /**
     * Tests it with the default namespace prefix (no public id).
     */
    public void testWithDefaultNamespacePrefix() {
        String xml = createXML(QName.create("http://jsefa.org/xml/test", "test"), null,
                "http://www.jsefa.org/xml/dtd/test");
        assertTrue(xml.indexOf("<!DOCTYPE test SYSTEM \"http://www.jsefa.org/xml/dtd/test\">") > -1);
        assertTrue(xml.indexOf("xmlns=") > -1);
    }

    /**
     * Tests it with an explicit namespace prefix (no public id).
     */
    public void testWithExplicitNamespacePrefix() {
        XmlLowLevelConfiguration config = new XmlLowLevelConfiguration();
        config.getNamespaceManager().registerPreferredPrefix("p", "http://jsefa.org/xml/test");
        String xml = createXML(QName.create("http://jsefa.org/xml/test", "test"), null,
                "http://www.jsefa.org/xml/dtd/test", config);
        assertTrue(xml.indexOf("<!DOCTYPE p:test SYSTEM \"http://www.jsefa.org/xml/dtd/test\">") > -1);
        assertTrue(xml.indexOf("xmlns:p=") > -1);
    }

    private String createXML(QName element, String publicId, String systemId) {
        return createXML(element, publicId, systemId, new XmlLowLevelConfiguration());
    }

    private String createXML(QName element, String publicId, String systemId, XmlLowLevelConfiguration config) {
        StaxBasedXmlLowLevelSerializer serializer = new StaxBasedXmlLowLevelSerializer(config);
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        serializer.writeDocTypeDeclaration(element, publicId, systemId);
        serializer.writeStartElement(element);
        serializer.writeEndElement();
        serializer.close(true);
        return writer.toString();
    }
}
