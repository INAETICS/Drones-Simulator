#!/bin/bash

ROOT=".."

docker build ./base_image/ -t dronesim/base

# Copy bundles to the docker images
rm -rf "game_engine/files/bundles/*"
rm -rf "drone/files/bundles/*"
cp "$ROOT/bundles/"*".jar" "game_engine/files/bundles/"
cp "$ROOT/bundles/"*".jar" "drone/files/bundles/"

# Copy dependencies for felix to docker images
rm -rf "game_engine/files/dependencies/*"
rm -rf "drone/files/dependencies/*"
cp "$ROOT/dependent-bundles/"*".jar" "game_engine/files/dependencies/"
cp "$ROOT/dependent-bundles/"*".jar" "drone/files/dependencies/"
