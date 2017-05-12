#!/bin/bash

ROOT="../implementation"
TARGET="./"

(cd $ROOT && mvn clean -P all)
(cd $ROOT && mvn package -P all -Dmaven.test.skip=true)

rm -rf "./"

cp "$ROOT/visualisation/target/visualisation-0.1.jar" $TARGET
cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/common/target/rabbitmq-common-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/publisher/target/rabbitmq-publisher-0.1.jar" $TARGET
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $TARGET
cp "$ROOT/physicsenginewrapper/target/physicsenginewrapper-0.1.jar" $TARGET
cp "$ROOT/common/target/common-0.1.jar" $TARGET
cp "$ROOT/drone/target/drone-0.1.jar" $TARGET

