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

package performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.Serializer;

/**
 * Abstract base class for the performance demos.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
abstract class AbstractPerformanceDemo {

    private static final int NAME_FIELD_LENGTH = 15;

    private static final int RESULT_FIELD_LENGTH = 15;

    private List<Object> dtoList = new ArrayList<Object>();

    private Map<Object, IOFactory> ioFactories;

    private File resultFile;

    private String targetFormatName;

    AbstractPerformanceDemo(String targetFormatName, String fileName) {
        this.targetFormatName = targetFormatName;
        this.resultFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        this.ioFactories = new HashMap<Object, IOFactory>();
    }

    protected void addDTO(Object dto) {
        this.dtoList.add(dto);
        if (this.ioFactories.get(dto.getClass()) == null) {
            this.ioFactories.put(dto.getClass(), createIOFactory(dto.getClass()));
        }
    }

    /**
     * Override to return the {@link IOFactory} to use.
     * 
     * @param objectType the object type to create the factory for
     * @return a <code>IOFactory</code>
     */
    protected abstract IOFactory createIOFactory(Class<?> objectType);

    /**
     * Override to perform special action after start (before writing the first DTO).
     * 
     * @param serializer the serializer
     */
    protected void afterStart(Serializer serializer) {

    }

    /**
     * Override to perfom special action before finish (after writing the last DTO).
     * 
     * @param serializer the serializer
     */
    protected void beforeFinish(Serializer serializer) {

    }

    void start(long... dtoCountArray) {
        System.out.println("Starting JSefa " + this.targetFormatName + " Performance Test\n\n");
        StringBuilder result = new StringBuilder();
        result.append("JSefa " + this.targetFormatName
                + " Performance Test Result (write/read in milliseconds)\n\n");
        print("", NAME_FIELD_LENGTH, result);
        for (long dtoCount : dtoCountArray) {
            print("" + dtoCount, RESULT_FIELD_LENGTH, result);
        }
        result.append("\n");
        print("", NAME_FIELD_LENGTH + dtoCountArray.length * RESULT_FIELD_LENGTH, '=', result);
        result.append("\n");

        for (Object dto : dtoList) {
            print(dto.getClass().getSimpleName(), NAME_FIELD_LENGTH, result);
            for (long count : dtoCountArray) {
                String value = writePerformance(dto, count) + "/" + readPerformance(dto.getClass());
                print(value, RESULT_FIELD_LENGTH, result);
            }
            result.append("\n");
        }
        System.out.println(result);
    }

    private void print(String content, int columnLength, StringBuilder buffer) {
        print(content, columnLength, ' ', buffer);
    }

    private void print(String content, int columnLength, char padChar, StringBuilder buffer) {
        if (content.length() >= columnLength) {
            buffer.append(content.substring(0, columnLength));
        } else {
            buffer.append(content);
            for (int i = 0; i < columnLength - content.length(); i++) {
                buffer.append(padChar);
            }
        }
    }

    private long writePerformance(Object obj, long count) {
        try {
            Serializer serializer = this.ioFactories.get(obj.getClass()).createSerializer();
            Writer writer = new BufferedWriter(new FileWriter(this.resultFile));
            serializer.open(writer);
            afterStart(serializer);
            long before = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                serializer.write(obj);
            }
            long after = System.currentTimeMillis();
            beforeFinish(serializer);
            serializer.close(false);
            writer.close();
            return (after - before);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private long readPerformance(Class<?> obj) {
        try {
            Deserializer deserializer = this.ioFactories.get(obj).createDeserializer();
            deserializer.open(new BufferedReader(new FileReader(this.resultFile)));
            long before = System.currentTimeMillis();
            while (deserializer.hasNext()) {
                deserializer.next();
            }
            long after = System.currentTimeMillis();
            deserializer.close(true);
            return (after - before);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
