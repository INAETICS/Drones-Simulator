#!/bin/bash

ROOT="../implementation"
TARGET="./"

set -e

(cd $ROOT && mvn clean -P all)
(cd $ROOT && mvn package -P all -Dmaven.test.skip=true)

cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $TARGET
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $TARGET
cp "$ROOT/visualisation/target/visualisation-0.1.jar" $TARGET
cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/common/target/rabbitmq-common-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $TARGET
cp "$ROOT/pubsub/rabbitmq/publisher/target/rabbitmq-publisher-0.1.jar" $TARGET
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $TARGET
cp "$ROOT/common/target/common-0.1.jar" $TARGET

cp "$ROOT/drone/components/engine/target/components-engine-0.1.jar" $TARGET
cp "$ROOT/drone/components/gps/target/components-gps-0.1.jar" $TARGET
cp "$ROOT/drone/components/gun/target/components-gun-0.1.jar" $TARGET
cp "$ROOT/drone/components/radar/target/components-radar-0.1.jar" $TARGET
cp "$ROOT/drone/drone-init/target/drone-init-0.1.jar" $TARGET
cp "$ROOT/drone/tactic/target/tactic-0.1.jar" $TARGET

cp "$ROOT/gameengine/common/target/gameengine-common-0.1.jar" $TARGET
cp "$ROOT/gameengine/gameengine/target/gameengine-gameengine-0.1.jar" $TARGET
cp "$ROOT/gameengine/gamestate-manager/target/gameengine-gamestate-manager-0.1.jar" $TARGET
cp "$ROOT/gameengine/physicsengine/target/gameengine-physicsengine-0.1.jar" $TARGET
cp "$ROOT/gameengine/physicsenginedriver/target/gameengine-physicsenginedriver-0.1.jar" $TARGET
cp "$ROOT/gameengine/identifier-mapper/target/gameengine-identifier-mapper-0.1.jar" $TARGET
cp "$ROOT/gameengine/ruleprocessors/target/gameengine-ruleprocessors-0.1.jar" $TARGET
