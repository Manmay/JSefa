package org.jsefa.xml;

import static org.jsefa.JSefaTestUtil.FormatType.XML;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.AbstractTestDTO;
import org.jsefa.JSefaTestUtil;
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
    public void testWithStringElements() {
        ListWithStringElements list = new ListWithStringElements();
        list.list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.list.add("stringValue" + i);
        }
        JSefaTestUtil.assertRepeatedRoundTripSucceeds(XML, list);
    }

    @XmlDataType()
    static final class ListWithStringElements extends AbstractTestDTO {
        @XmlElementList(items = @ListItem(name = "item"))
        List<String> list;
    }
}
