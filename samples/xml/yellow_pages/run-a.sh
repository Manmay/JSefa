#!/bin/sh
JSEFA_HOME=`cd ../../..;pwd`
CLASSPATH=$CLASSPATH:$JSEFA_HOME/jsefa-1.0.jar:$JSEFA_HOME/lib/jsr173_1.0_api.jar:$JSEFA_HOME/lib/wstx-asl-3.2.1.jar
cd src
java xml.yellowpages.DeserializationDemo

