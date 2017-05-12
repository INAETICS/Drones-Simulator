#!/bin/bash

ROOT=".."
TARGET="./game_engine/files"

docker build ./base_image/ -t dronesim/base

cp "$ROOT/bundles/"*".jar" "$TARGET/bundles/"
cp "$ROOT/dependent-bundles/"*".jar" "$TARGET/dependencies/"
