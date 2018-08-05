#!/bin/bash

echo 'Starting mongo docker image'
./mongo/start-mongo.sh 

echo 'Building frontend'
./frontend/build-frontend.sh 

echo 'Building app';
./gradlew --quiet build shadowjar

echo 'Running app';
java -jar build/libs/wiremock-mongo-request-store-1.0-SNAPSHOT-all.jar src/main/resources/requests/


