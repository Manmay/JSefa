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

import org.jsefa.IOFactory;
import org.jsefa.Serializer;
import org.jsefa.xml.XmlIOFactory;
import org.jsefa.xml.XmlSerializer;
import org.jsefa.xml.namespace.QName;

/**
 * Demo for demonstrating the XML serialization/deserialization performance.
 * 
 * @author Norman Lahme-Huetig
 * @author Marko Kovacevic
 * 
 */
public class XmlPerformanceDemo extends AbstractPerformanceDemo {

    private static final String FILE_NAME = "jsefa-performance.xml";

    XmlPerformanceDemo() {
        super("XML", FILE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IOFactory createIOFactory(Class<?> objectType) {
        return XmlIOFactory.createFactory(objectType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterStart(Serializer serializer) {
        super.afterStart(serializer);
        ((XmlSerializer) serializer).getLowLevelSerializer().writeStartElement(QName.create("root"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeFinish(Serializer serializer) {
        super.beforeFinish(serializer);
        ((XmlSerializer) serializer).getLowLevelSerializer().writeEndElement();
    }

    /**
     * The main method.
     * 
     * @param args no args
     */
    public static void main(String[] args) {
        XmlPerformanceDemo demo = new XmlPerformanceDemo();
        demo.addDTO(DTOFactory.createStringOnlyDTO());
        demo.addDTO(DTOFactory.createMixedDTO());
        demo.addDTO(DTOFactory.createDeepDTO());

        demo.start(1000, 100000);
    }

}
