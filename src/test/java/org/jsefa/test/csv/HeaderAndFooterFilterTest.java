package org.jsefa.test.csv;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import junit.framework.TestCase;

import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.common.lowlevel.filter.Line;
import org.jsefa.csv.CsvDeserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.CsvSerializer;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.test.common.AbstractTestDTO;

/**
 * Tests to test the CSV deserialization with a <code>HeaderAndFooterFilter</code>.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public class HeaderAndFooterFilterTest extends TestCase {
    /**
     * Test a CSV document with one header and no footer.
     */
    public void testWithOneHeaderLine() {
        check(1, false, 0);
        check(1, false, 3);
    }
    
    /**
     * Test a CSV document with multiple header lines and no footer.
     */
    public void testWithThreeHeaderLines() {
        check(3, false, 0);
        check(3, false, 3);
    }

    /**
     * Test a CSV document with no header but a footer.
     */
    public void testWithNoHeaderButFooter() {
        check(0, true, 0);
        check(0, true, 3);
    }

    /**
     * Test a CSV document with one header and a footer.
     */
    public void testWithOneHeaderLineAndFooter() {
        check(1, true, 0);
        check(1, true, 3);
    }
    
    /**
     * Test a CSV document with multiple header lines and a footer.
     */
    public void testWithThreeHeaderLinesAndFooter() {
        check(3, true, 0);
        check(3, true, 3);
    }

    private void check(int headerSize, boolean hasFooter, int recordCount) {
        String input = serialize(headerSize, hasFooter, recordCount);
        
        CsvConfiguration config = new CsvConfiguration();
        config.setLineFilter(new HeaderAndFooterFilter(headerSize, hasFooter, true));
        CsvDeserializer deserializer = CsvIOFactory.createFactory(config, SimpleDTO.class).createDeserializer();
        StringReader reader = new StringReader(input);
        int foundRecords = 0;

        deserializer.open(reader);
        while (deserializer.hasNext()) {
            SimpleDTO dto = deserializer.next();
            foundRecords++;
            assertNotNull(dto.fieldA);
            assertNotNull(dto.fieldB);
        }
        assertEquals(recordCount, foundRecords);
        
        deserializer.close(true);
        
        List<Line> storedLines = deserializer.getStoredLines();
        assertNotNull(storedLines);
        if (hasFooter) {
            assertTrue(storedLines.size() == headerSize + 1);
        } else {
            assertTrue(storedLines.size() == headerSize);
        }
        
    }
    
    private String serialize(int headerSize, boolean hasFooter, int recordCount) {
        CsvSerializer serializer = CsvIOFactory.createFactory(SimpleDTO.class).createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        
        for (int i = 1; i <= headerSize; i++) {
            serializer.getLowLevelSerializer().writeLine("HEADER " + i);
        }
        for (int i = 1; i <= recordCount; i++) {
            serializer.write(createSimpleDTO("FieldA " + i, "FieldB " + i));
        }
        if (hasFooter) {
            serializer.getLowLevelSerializer().writeLine("FOOTER");
        }
        
        serializer.close(true);
        return writer.toString();
    }
    
    private SimpleDTO createSimpleDTO(String fieldA, String fieldB) {
        SimpleDTO dto = new SimpleDTO();
        dto.fieldA = fieldA;
        dto.fieldB = fieldB;
        return dto;
    }
    
    @CsvDataType()
    static final class SimpleDTO extends AbstractTestDTO {
        @CsvField(pos = 1)
        String fieldA;

        @CsvField(pos = 2)
        String fieldB;
    }    
}
