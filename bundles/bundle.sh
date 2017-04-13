#!/bin/bash

ROOT="../implementation"
TARGET="./"

(cd $ROOT && mvn compile -P all)
(cd $ROOT && mvn org.apache.felix:maven-bundle-plugin:bundle -P all)

cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/publisher/target/rabbitmq-publisher-0.1.jar" $TARGET
cp "$ROOT/physicsengine/target/physicsengine-0.1.jar" $TARGET
