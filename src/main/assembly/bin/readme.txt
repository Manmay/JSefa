Included with JSefa are the following libraries because they are (optionally) required for using JSefa
in your own applications.
Note that each of these libraries is subject to the respective license. Please check the respective
website before using any of them in your own applications.

jsr173_1.0_api.jar
- required for building JSefa and running applications using JSefa

wstx-asl-3.2.1.jar
- woodstox implementation of JSR 173
- required for running applications using JSefa if no other implementation of JSR 173 is provided

javassist.jar
- required only when JavassistBasedObjectAccessorProvider is used;
  thus not needed for the default case.