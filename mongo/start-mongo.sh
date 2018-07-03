#!/bin/bash

set -e

sudo docker pull mongo:3.6-jessie
        
sudo docker run \
       --name mongo-db \
       -p 27017:27017 \
       -v "$(pwd)/data:/data/db"\
       -d mongo:3.6-jessie \
       --smallfiles

