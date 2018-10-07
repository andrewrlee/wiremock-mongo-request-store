#!/bin/bash

echo 'Starting mongo docker image'
./docker/mongo/start-mongo.sh

echo 'Building frontend'
./frontend/build-frontend.sh 

echo 'Building app';
./gradlew --quiet clean build

echo 'Running app';
java -jar sample/build/libs/wiremock-mongo-request-store-1.0-SNAPSHOT-all.jar src/main/resources/requests/


