#!/bin/bash

set -e

cd "$(dirname "$0")/frontend";

ASSETS_DIR=../src/main/resources/public/

rm -Rf "$ASSETS_DIR"
mkdir -p "$ASSETS_DIR"

npm run build

mv ./build/* "$ASSETS_DIR"
