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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory for creating {@link Reader} from different sources suitable to read a xml document providing
 * autodetection of the character set encoding.
 * <p>
 * The autodetection mechanism follows the recommendations of <a
 * href="http://www.w3.org/TR/2004/REC-xml-20040204/#sec-guessing">Section F.1 of the XML specification</a>.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public final class XmlReaderFactory {

    private static final String UTF_16BE = "UTF-16BE";

    private static final String UTF_16LE = "UTF-16LE";

    private static final String UTF_8 = "UTF-8";

    private static final EncodingPattern[] BOM_PATTERNS = {
            new EncodingPattern(new int[]{0x00, 0x00, 0xFE, 0xFF}, "UCS-4, big-endian machine", null),
            new EncodingPattern(new int[]{0xFF, 0xFE, 0x00, 0x00}, "UCS-4, little-endian machine", null),
            new EncodingPattern(new int[]{0x00, 0x00, 0xFF, 0xFE}, "UCS-4, unusual octet order (2143)", null),
            new EncodingPattern(new int[]{0xFE, 0xFF, 0x00, 0x00}, "UCS-4, unusual octet order (3412)", null),
            new EncodingPattern(new int[]{0xFE, 0xFF}, "UTF-16, big-endian", UTF_16BE),
            new EncodingPattern(new int[]{0xFF, 0xFE}, "UTF-16, little-endian", UTF_16LE),
            new EncodingPattern(new int[]{0xEF, 0xBB, 0xBF}, "UTF-8", UTF_8)};

    private static final EncodingPattern[] CONTENT_PATTERNS = {
            new EncodingPattern(new int[]{0x00, 0x00, 0x00, 0x3C}, "32 bit, big-endian", null),
            new EncodingPattern(new int[]{0x3C, 0x00, 0x00, 0x00}, "32 bit, little-endian", null),
            new EncodingPattern(new int[]{0x00, 0x00, 0x3C, 0x00}, "32 bit, unusual octet order (2143)", null),
            new EncodingPattern(new int[]{0x00, 0x3C, 0x00, 0x00}, "32 bit, unusual octet order (3412)", null),
            new EncodingPattern(new int[]{0x00, 0x3C, 0x00, 0x3F}, "16 bit, big-endian", UTF_16BE),
            new EncodingPattern(new int[]{0x3C, 0x00, 0x3F, 0x00}, "16 bit, little-endian", UTF_16LE),
            new EncodingPattern(new int[]{0x3C, 0x3F, 0x78, 0x6D}, "8 bit, ASCII conform", UTF_8),
            new EncodingPattern(new int[]{0x4C, 0x6F, 0xA7, 0x94}, "8 bit, ASCII conform", null)};

    private static final Pattern XML_DECL_ENCODING_PATTERN = Pattern
            .compile("<\\?xml.*encoding\\s*=\\s*((?:\"[^\"]*\")|(?:'[^']*')).*\\?>");

    /**
     * Creates a <code>Reader</code> from the given <code>File</code> with an autodetected character set
     * encoding.
     * 
     * @param file the file to create a reader for
     * @return a <code>Reader</code>
     * @throws XmlEncodingException if an unsupported encoding is detected or an error occurs while encoding
     *                 detection
     */
    public static Reader create(File file) {
        try {
            return create(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new XmlEncodingException(e);
        }
    }

    /**
     * Creates a <code>Reader</code> from the given <code>InputStream</code> with an autodetected character set
     * encoding.
     * 
     * @param inputStream the input stream to create a reader for
     * @return a <code>Reader</code>
     * @throws XmlEncodingException if an unsupported encoding is detected or an error occurs while encoding
     *                 detection
     */
    public static Reader create(InputStream inputStream) {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream, 128);
        String encodingFamily = getEncodingFamily(BOM_PATTERNS, pushbackInputStream);
        if (encodingFamily == null) {
            encodingFamily = getEncodingFamily(CONTENT_PATTERNS, pushbackInputStream);
        }
        String encoding = null;
        if (encodingFamily == null) {
            encoding = UTF_8;
        } else {
            encoding = getEncodingFromXmlDecl(pushbackInputStream, encodingFamily);
            if (encoding == null) {
                encoding = encodingFamily;
            }
        }
        try {
            return new InputStreamReader(pushbackInputStream, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new XmlEncodingException(e);
        }
    }

    private static String getEncodingFamily(EncodingPattern[] patterns, PushbackInputStream inputStream) {
        byte[] buffer = readBytesAhead(inputStream, 4);
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i].matches(buffer)) {
                if (patterns[i].encodingFamily == null) {
                    throw new XmlEncodingException("Unsupported: " + patterns[i].getDescription());
                } else {
                    return patterns[i].getEncodingFamily();
                }
            }
        }
        return null;
    }

    private static String getEncodingFromXmlDecl(PushbackInputStream inputStream, String encodingFamily) {
        String content = readStringAhead(inputStream, 128, encodingFamily);
        Matcher m = XML_DECL_ENCODING_PATTERN.matcher(content);
        if (m.find()) {
            String encoding = m.group(1).toUpperCase();
            return encoding.substring(1, encoding.length() - 1);
        } else {
            return null;
        }

    }

    private static String readStringAhead(PushbackInputStream inputStream, int lengthInBytes, String encoding) {
        byte[] bytes = readBytesAhead(inputStream, lengthInBytes);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes, 0,
                    lengthInBytes), encoding));
            StringBuilder result = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                result.append("\n");
                line = reader.readLine();
            }
            reader.close();
            return result.toString();
        } catch (IOException e) {
            throw new XmlEncodingException(e);
        }
    }

    private static byte[] readBytesAhead(PushbackInputStream inputStream, int size) {
        byte[] buffer = new byte[size];
        int bytesLeft = size;
        int totalBytesRead = 0;
        try {
            int bytesRead = inputStream.read(buffer, totalBytesRead, bytesLeft);
            while (bytesRead != -1 && bytesLeft > 0) {
                totalBytesRead += bytesRead;
                bytesLeft -= bytesRead;
                bytesRead = inputStream.read(buffer, totalBytesRead, bytesLeft);
            }
            if (totalBytesRead > 0) {
                inputStream.unread(buffer, 0, totalBytesRead);
            }
            return buffer;
        } catch (IOException e) {
            throw new XmlEncodingException(e);
        }
    }

    private XmlReaderFactory() {

    }

    private static final class EncodingPattern {
        private int[] pattern;

        private String description;

        private String encodingFamily;

        public EncodingPattern(int[] pattern, String description, String encodingFamily) {
            this.pattern = pattern;
            this.description = description;
            this.encodingFamily = encodingFamily;
        }

        public boolean matches(byte[] input) {
            for (int i = 0; i < this.pattern.length; i++) {
                if (this.pattern[i] != (input[i] & 0xFF)) {
                    return false;
                }
            }
            return true;
        }

        public String getDescription() {
            return this.description;
        }

        public String getEncodingFamily() {
            return this.encodingFamily;
        }
    }

}
