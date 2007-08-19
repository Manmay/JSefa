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
import org.jsefa.flr.FlrIOFactory;

/**
 * Demo for demonstrating the FLR serialization/deserialization performance.
 * 
 * @author Norman Lahme-Huetig
 * 
 */

public class FlrPerformanceDemo extends AbstractPerformanceDemo {
    private static final String FILE_NAME = "jsefa-performance.flr";

    FlrPerformanceDemo() {
        super("FLR", FILE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IOFactory createIOFactory(Class<?> objectType) {
        return FlrIOFactory.createFactory(objectType);
    }

    /**
     * The main method.
     * 
     * @param args no args
     */
    public static void main(String[] args) {
        FlrPerformanceDemo demo = new FlrPerformanceDemo();
        demo.addDTO(DTOFactory.createStringOnlyDTO());
        demo.addDTO(DTOFactory.createMixedDTO());
        demo.addDTO(DTOFactory.createDeepDTO());

        demo.start(1000, 100000);
    }
}
