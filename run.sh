#!/bin/bash
export API_PATH=/home/andrew/Documents/fieldml.api
java -cp target/classes:/usr/share/java/scala-library.jar:$API_PATH/jni/fieldml.jar -Djava.library.path=$API_PATH "$@"
