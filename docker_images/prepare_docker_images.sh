#!/bin/bash
ROOT=".."

#./../bundles/bundle.sh
docker build ./base_image/ -t dronesim/base

# Remove possible old bundles
rm -rf "game_engine/files/bundles/*.jar"
rm -rf "drone/files/bundles/*.jar"

# Add new required bundles

# Game engine
GAME_ENGINE="game_engine/files/bundles/"
cp "$ROOT/bundles/discovery-api-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/etcd-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/common-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/java-serializer-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/pubsub-api-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/rabbitmq-common-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/rabbitmq-publisher-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/rabbitmq-subscriber-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-common-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-gameengine-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-gamestate-manager-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-physicsengine-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-physicsenginedriver-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-identifier-mapper-0.1.jar" $GAME_ENGINE
cp "$ROOT/bundles/gameengine-ruleprocessors-0.1.jar" $GAME_ENGINE

# Drone
DRONE="drone/files/bundles"
cp "$ROOT/bundles/discovery-api-0.1.jar" $DRONE
cp "$ROOT/bundles/etcd-0.1.jar" $DRONE
cp "$ROOT/bundles/common-0.1.jar" $DRONE
cp "$ROOT/bundles/java-serializer-0.1.jar" $DRONE
cp "$ROOT/bundles/pubsub-api-0.1.jar" $DRONE
cp "$ROOT/bundles/rabbitmq-common-0.1.jar" $DRONE
cp "$ROOT/bundles/rabbitmq-publisher-0.1.jar" $DRONE
cp "$ROOT/bundles/rabbitmq-subscriber-0.1.jar" $DRONE
cp "$ROOT/bundles/drone-init-0.1.jar" $DRONE
cp "$ROOT/bundles/components-engine-0.1.jar" $DRONE
cp "$ROOT/bundles/components-gps-0.1.jar" $DRONE
cp "$ROOT/bundles/components-gun-0.1.jar" $DRONE
cp "$ROOT/bundles/components-radar-0.1.jar" $DRONE
cp "$ROOT/bundles/tactic-0.1.jar" $DRONE

# Copy and overwrite dependencies for felix to docker images
cp "$ROOT/dependent-bundles/"*".jar" "game_engine/files/dependencies/"
cp "$ROOT/dependent-bundles/"*".jar" "drone/files/dependencies/"
