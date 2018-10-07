#!/bin/bash

echo 'Starting mongo docker image'
./docker/mongo/start-mongo.sh

echo 'Building frontend'
./frontend/build-frontend.sh 

echo 'Building app';
./gradlew --quiet clean build sample:shadowJar

echo 'Running app';
java -jar sample/build/libs/sample-1.0-SNAPSHOT-all.jar sample/src/main/resources/requests/


