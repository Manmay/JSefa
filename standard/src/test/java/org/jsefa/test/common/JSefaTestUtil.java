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

package org.jsefa.test.common;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.jsefa.Deserializer;
import org.jsefa.IOFactory;
import org.jsefa.Serializer;
import org.jsefa.common.config.Configuration;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;
import org.jsefa.flr.FlrIOFactory;
import org.jsefa.flr.config.FlrConfiguration;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.XmlSerializer;
import org.jsefa.xml.config.XmlConfiguration;
import org.jsefa.xml.namespace.QName;

/**
 * Utility class for the JSefa tests.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public final class JSefaTestUtil {
    private static final int REPETITION_COUNT = 3;

    /**
     * Enum constants for the different format types.
     * 
     * @author Norman Lahme-Huetig
     * 
     */
    public enum FormatType {
        /**
         * XML format.
         */
        XML,

        /**
         * CSV format.
         */
        CSV,

        /**
         * FLR format.
         */
        FLR;
    }

    /**
     * Does a repeated round trip check, i. e. serializes the given dtos, deserializes them back again and tests if
     * the result equals the input. This check is repeated several times.
     * 
     * @param formatType the format type
     * @param dtos the DTOs
     */
    public static void assertRepeatedRoundTripSucceeds(FormatType formatType, Object... dtos) {
        assertRepeatedRoundTripSucceeds(formatType, createConfiguration(formatType), dtos);
    }

    /**
     * Does a repeated round trip check, i. e. serializes the given dtos, deserializes them back again and tests if
     * the result equals the input. This check is repeated several times.
     * 
     * @param formatType the format type
     * @param config the configuration
     * @param dtos the DTOs
     */
    @SuppressWarnings("unchecked")
    public static void assertRepeatedRoundTripSucceeds(FormatType formatType, Configuration config, Object... dtos) {
        List<Object> dtoList = new ArrayList<Object>();
        for (int i = 0; i < REPETITION_COUNT; i++) {
            for (Object dto : dtos) {
                dtoList.add(dto);
            }
        }
        assertRoundTripSucceeds(formatType, config, dtoList);
    }

    /**
     * Serializes the given objects.
     * 
     * @param formatType the format type
     * @param objects the objects to serialize
     * @return the serialization result
     */
    public static String serialize(FormatType formatType, Object... objects) {
        return serialize(formatType, createConfiguration(formatType), objects);
    }

    /**
     * Serializes the given object.
     * 
     * @param formatType the format type
     * @param config the configuration
     * @param objects the objects to serialize
     * @return the serialization result
     */
    @SuppressWarnings("unchecked")
    public static String serialize(FormatType formatType, Configuration config, Object... objects) {
        IOFactory ioFactory = createIOFactory(formatType, config, getObjectTypes(Arrays.asList(objects)));
        return serialize(ioFactory, Arrays.asList(objects));
    }

    /**
     * Serializes the given object using an {@link IOFactory} constructed for the given object type.
     * 
     * @param formatType the format type
     * @param objectType the object type
     * @param object the object to serialize
     * @return the serialization result
     */
    @SuppressWarnings("unchecked")
    public static String serialize(FormatType formatType, Class objectType, Object object) {
        IOFactory ioFactory = createIOFactory(formatType, objectType);
        List list = new ArrayList();
        list.add(object);
        return serialize(ioFactory, list);
    }

    /**
     * Deserializes an object of the given DTO from the given <code>String</code>.
     * 
     * @param <T> the type of the object
     * @param formatType the format type
     * @param inputString the <code>String</code> to deserialize the DTO from
     * @param objectTypes the object types
     * @return the deserialized object
     */
    public static <T> T deserialize(FormatType formatType, String inputString, Class<T>... objectTypes) {
        return deserialize(formatType, createConfiguration(formatType), inputString, objectTypes);
    }

    /**
     * Deserializes an object of the given DTO from the given <code>String</code>.
     * 
     * @param <T> the type of the object
     * @param formatType the format type
     * @param config the configuration
     * @param inputString the <code>String</code> to deserialize the DTO from
     * @param objectTypes the object types
     * @return the deserialized object
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(FormatType formatType, Configuration config, String inputString,
            Class<T>... objectTypes) {
        IOFactory ioFactory = createIOFactory(formatType, config, objectTypes);
        List result = deserialize(inputString, ioFactory);
        if (result.isEmpty()) {
            return null;
        } else {
            return (T) result.get(0);
        }

    }

    /**
     * Creates a default configuration for the given format type.
     * 
     * @param formatType the format type
     * @return a configuration
     */
    @SuppressWarnings("unchecked")
    public static Configuration createConfiguration(FormatType formatType) {
        switch (formatType) {
        case XML:
            return new XmlConfiguration();
        case CSV:
            return new CsvConfiguration();
        case FLR:
            return new FlrConfiguration();
        default:
            throw new IllegalArgumentException("Unknown format type: " + formatType);
        }
    }

    /**
     * Creates a <code>IOFactory</code>.
     * 
     * @param formatType the format type
     * @param objectTypes the object types
     * @return an <code>IOFactory</code>
     */
    @SuppressWarnings("unchecked")
    public static IOFactory createIOFactory(FormatType formatType, Class<?>... objectTypes) {
        Configuration config = createConfiguration(formatType);
        switch (formatType) {
        case XML:
            return XmlIOFactory.createFactory((XmlConfiguration) config, objectTypes);
        case CSV:
            return CsvIOFactory.createFactory((CsvConfiguration) config, objectTypes);
        case FLR:
            return FlrIOFactory.createFactory((FlrConfiguration) config, objectTypes);
        default:
            throw new IllegalArgumentException("Unknown format type: " + formatType);
        }
    }

    /**
     * Creates a <code>IOFactory</code>.
     * 
     * @param formatType the format type
     * @param config the configuration
     * @param objectTypes the object types
     * @return an <code>IOFactory</code>
     */
    @SuppressWarnings("unchecked")
    public static IOFactory createIOFactory(FormatType formatType, Configuration config, Class<?>... objectTypes) {
        switch (formatType) {
        case XML:
            return XmlIOFactory.createFactory((XmlConfiguration) config, objectTypes);
        case CSV:
            return CsvIOFactory.createFactory((CsvConfiguration) config, objectTypes);
        case FLR:
            return FlrIOFactory.createFactory((FlrConfiguration) config, objectTypes);
        default:
            throw new IllegalArgumentException("Unknown format type: " + formatType);
        }
    }

    @SuppressWarnings("unchecked")
    private static void assertRoundTripSucceeds(FormatType formatType, Configuration config, List<?> inputDTOs) {
        IOFactory ioFactory = createIOFactory(formatType, config, getObjectTypes(inputDTOs));
        String serializationResult = serialize(ioFactory, inputDTOs);
        List<?> outputDTOs = deserialize(serializationResult, ioFactory);
        Assert.assertEquals(outputDTOs, inputDTOs);
    }

    private static String serialize(IOFactory ioFactory, List<?> inputDTOs) {
        Serializer serializer = ioFactory.createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        if (serializer instanceof XmlSerializer) {
            ((XmlSerializer) serializer).getLowLevelSerializer().writeStartElement(QName.create("root"));
        }
        for (Object dto : inputDTOs) {
            serializer.write(dto);
        }
        if (serializer instanceof XmlSerializer) {
            ((XmlSerializer) serializer).getLowLevelSerializer().writeEndElement();
        }
        serializer.close(true);
        return writer.toString();
    }

    @SuppressWarnings("unchecked")
    private static List deserialize(String inputString, IOFactory ioFactory) {
        List result = new ArrayList();
        Deserializer deserializer = ioFactory.createDeserializer();
        deserializer.open(new StringReader(inputString));
        while (deserializer.hasNext()) {
            result.add(deserializer.next());
        }
        deserializer.close(true);
        return result;
    }

    /**
     * Returns the classes of the given object.
     * 
     * @param objects list with classes
     * @return array with classes
     */
    private static Class<?>[] getObjectTypes(List<?> objects) {
        Set<Class<?>> objectTypes = new HashSet<Class<?>>();
        for (Object obj : objects) {
            objectTypes.add(obj.getClass());
        }
        return objectTypes.toArray(new Class[]{});
    }

    private JSefaTestUtil() {

    }

}
