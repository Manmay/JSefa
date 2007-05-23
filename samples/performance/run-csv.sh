#!/bin/sh
JSEFA_HOME=`cd ../..;pwd`
CLASSPATH=$CLASSPATH:$JSEFA_HOME/jsefa-1.0.jar
cd src
java performance.CsvPerformanceDemo

