JSefa (Java Simple exchange format api) is a simple library for stream-based
serialization of java objects to XML, CSV or any other format and back again
using an iterator-style interface independent of the serialization format.
The mapping between java object types and types of the serialization format
(e. g. xml complex element types) can be defined either by annotating the java
classes or programmatically using a simple API. The current implementation
supports XML, CSV and FLR (Fixed Length Record) - for XML it is based on
JSR 173.

Currently JSR 173 (Stax) is the only-available stream-based XML API for java
providing an iterator-style interface ("pull"-mechanism in contrast to the
"push"-mechanism provided by SAX). But JSR 173 defines a low-level API not
designed for directly serializing java objects and back again. On the other
hand currently available high-level APIs like JAXB or Castor are not stream-
based, so that reading a xml document will generate java objects holding the
data of the complete xml document in memory at the same time. JSefa fills this
gap by providing a high-level stream-based API for serializing java objects to
XML and back again using an iterator-style interface. Furthermore JSefa
provides iterator-style interfaces (Serializer and Deserializer) independent of
the serialization format type (XML, CSV or whatever). The current
implementation provides implementations of these interfaces for XML, CSV, and
FLR.



