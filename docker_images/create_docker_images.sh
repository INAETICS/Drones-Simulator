#!/bin/bash

ROOT=".."
TARGET="./game_engine/files"

cp "$ROOT/bundles/"*".jar" "$TARGET/bundles/"
cp "$ROOT/dependent-bundles/"*".jar" "$TARGET/dependencies/"