-------------------------------------------------------------------------------
JSefa ${version}
-------------------------------------------------------------------------------

1. INTRODUCTION

  JSefa (Java Simple exchange format api) is a simple library for stream-based
serialization of java objects to XML, CSV, and FLR (extensible to other formats)
and back again using an iterator-style interface independent of the serialization
format. The mapping between java object types and types of the serialization
format (e. g. xml complex element types) can be defined either by annotating the
java classes or programmatically using a simple API. The current implementation
supports XML, CSV and FLR (Fixed Length Record) - for XML it is based on JSR 173.

  JSR 173 (Stax) is a popular stream-based XML API for java providing an iterator-style
interface ("pull"-mechanism in contrast to the "push"-mechanism provided by SAX).
But JSR 173 defines a low-level API not designed for directly serializing java objects
and back again. On the other hand traditional high-level APIs like JAXB or Castor are
not stream-based, so that reading a xml document will generate java objects holding the
data of the complete xml document in memory at the same time. Even the integration of StAX
into JAXB 2.0 is only a first step to high-level streaming, as two independent APIs have
to be used in parallel. JSefa provides a convenient and performant approach to high-level
streaming using an iterator-style interface. It has a layered API with the top layer
allowing the streaming to be independent of the serialization format type (XML, CSV or
whatever). The current implementation provides support for XML, CSV, and FLR.

2. RELEASE INFO

JSefa ${version} requires J2SE 1.5 and some external jar files (see lib directory
and the including readme.txt).

Contents:
* lib
  Contains third party jar files
* ${artifactId}-${version}.jar
  The JSefa jar file
* ${artifactId}-${version}-src.zip
  The zipped JSefa source code. You can use this file as a source attachment
  to the ${artifactId}-${version}.jar when including JSefa into your project.
* readme.txt
  This readme file
* notice.txt
  Contains some notes
* license.txt
  JSefa is released under the terms of the Apache License Version 2 a copy of 
  which is contained in this file.

3. WHERE TO START?

A quick tutorial is available at http://jsefa.sourceforge.net/quick-tutorial.html.
Some samples are shipped with all JSefa distributions. See the samples directory.
A lot of JUnit tests are shipped with the JSefa source distribution; they may be
of additional help to you in getting started with JSefa.
  