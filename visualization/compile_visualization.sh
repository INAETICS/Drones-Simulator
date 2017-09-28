#!/bin/bash

ROOT="../implementation"
TARGET="./"

set -e

(cd $ROOT && mvn clean package -P all -Dmaven.test.skip=true)

cp "$ROOT/visualisation/target/visualisation-0.1.jar" $TARGET
