Included with JSefa are the following libraries because they are (optionally) required for
compiling JSefa, using JSefa in your own applications, or for running the unit tests.
Note that each of these libraries is subject to the respective license. Please check the respective
website before using any of them in your own applications.

jsr173_1.0_api.jar
- required for building JSefa and running applications using JSefa for XML serialization/deserialization
- not required if using Java 6 or above

wstx-asl-3.2.1.jar
- woodstox implementation of JSR 173
- required for running applications using JSefa for XML serialization/deserialization
  if no other implementation of JSR 173 is provided
- not required if using Java 6 or above

javassist.jar
- required only when JavassistBasedObjectAccessorProvider is used;
  thus not needed for the default case.
  
junit.jar
- required for testing only  

commons-lang-2.3.jar
- required for testing only