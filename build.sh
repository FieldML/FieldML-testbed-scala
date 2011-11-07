#!/bin/bash
cd src
export API_PATH=/home/andrew/Documents/fieldml.api
scalac -cp $API_PATH/jni/fieldml.jar $(find fieldml framework util test -iname *.scala)
