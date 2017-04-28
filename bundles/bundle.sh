#!/bin/bash

ROOT="../implementation"
TARGET="./"

(cd $ROOT && mvn clean -P all)
(cd $ROOT && mvn package -P all -Dmaven.test.skip=true)

cp "$ROOT/visualisation/target/visualisation-0.1.jar" $TARGET
cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/publisher/target/rabbitmq-publisher-0.1.jar" $TARGET
cp "$ROOT/physicsengine/target/physicsengine-0.1.jar" $TARGET
cp "$ROOT/drone/target/drone-0.1.jar" $TARGET