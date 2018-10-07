#!/bin/bash

set -e

cd "$(dirname "$0")";

ASSETS_DIR=../core/src/main/resources/public/

rm -Rf "$ASSETS_DIR"
mkdir -p "$ASSETS_DIR"

npm run build

mv ./build/* "$ASSETS_DIR"
