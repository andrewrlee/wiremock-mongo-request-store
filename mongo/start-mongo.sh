#!/bin/bash

set -e

cd "$(dirname "$0")"

app="mongo-db"

sudo docker ps -aq --filter "name=$app" | grep -q . && sudo docker rm -fv $app

sudo docker pull mongo:3.6-jessie
        
sudo docker run \
       --name "$app" \
       -p 27017:27017 \
       -v "$(pwd)/data:/data/db"\
       -d mongo:3.6-jessie \
       --smallfiles

