#/bin/bash

ROOT="implementation" 

set -e
(cd $ROOT && mvn clean)
(cd $ROOT && mvn package -Dmaven.test.skip=true)

# Copy and overwrite felix config in base image
cp "felix-config.properties" "docker_images/base_image/files/config.properties"

# Build base docker image 
docker build ./docker_images/base_image/ -t dronesim/base

# Remove old dependent bundles
rm -rf "docker_images/game_engine/files/dependencies/*.jar"
rm -rf "docker_images/drone/files/dependencies/*.jar"
rm -rf "docker_images/architecture_manager/dependencies/*.jar"

# Copy and overwrite dependencies for felix to docker images
cp "dependent-bundles/"*".jar" "docker_images/game_engine/files/dependencies/"
cp "dependent-bundles/"*".jar" "docker_images/drone/files/dependencies/"
cp "dependent-bundles/"*".jar" "docker_images/architecture_manager/files/dependencies/"

# Remove possible old bundles
rm -rf "docker_images/game_engine/files/bundles/*.jar"
rm -rf "docker_images/drone/files/bundles/*.jar"
rm -rf "docker_images/architecture_manager/files/bundles/*.jar"

# Game engine
GAME_ENGINE="docker_images/game_engine/files/bundles/"
cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $GAME_ENGINE
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $GAME_ENGINE
cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $GAME_ENGINE
cp "$ROOT/pubsub/rabbitmq/common/target/rabbitmq-common-0.1.jar" $GAME_ENGINE
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $GAME_ENGINE
cp "$ROOT/pubsub/rabbitmq/publisher/target/rabbitmq-publisher-0.1.jar" $GAME_ENGINE
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $GAME_ENGINE
cp "$ROOT/common/target/common-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/common/target/gameengine-common-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/gameengine/target/gameengine-gameengine-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/gamestate-manager/target/gameengine-gamestate-manager-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/physicsengine/target/gameengine-physicsengine-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/physicsenginedriver/target/gameengine-physicsenginedriver-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/identifier-mapper/target/gameengine-identifier-mapper-0.1.jar" $GAME_ENGINE
cp "$ROOT/gameengine/ruleprocessors/target/gameengine-ruleprocessors-0.1.jar" $GAME_ENGINE
cp "$ROOT/architecture-event-controller/target/architecture-event-controller-0.1.jar" $GAME_ENGINE

# Drone
DRONE="docker_images/drone/files/bundles/"
cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $DRONE
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $DRONE
cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $DRONE
cp "$ROOT/pubsub/rabbitmq/common/target/rabbitmq-common-0.1.jar" $DRONE
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $DRONE
cp "$ROOT/pubsub/rabbitmq/publisher/target/rabbitmq-publisher-0.1.jar" $DRONE
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $DRONE
cp "$ROOT/common/target/common-0.1.jar" $DRONE
cp "$ROOT/drone/components/engine/target/components-engine-0.1.jar" $DRONE
cp "$ROOT/drone/components/gps/target/components-gps-0.1.jar" $DRONE
cp "$ROOT/drone/components/gun/target/components-gun-0.1.jar" $DRONE
cp "$ROOT/drone/components/radar/target/components-radar-0.1.jar" $DRONE
cp "$ROOT/drone/drone-init/target/drone-init-0.1.jar" $DRONE
cp "$ROOT/drone/tactic/target/tactic-0.1.jar" $DRONE
cp "$ROOT/architecture-event-controller/target/architecture-event-controller-0.1.jar" $DRONE

# Architecture Manager
ARCHITECTURE_MANAGER="docker_images/architecture_manager/files/bundles/"
cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/pubsub/javaserializer/target/java-serializer-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/pubsub/rabbitmq/common/target/rabbitmq-common-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/pubsub/rabbitmq/subscriber/target/rabbitmq-subscriber-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/common/target/common-0.1.jar" $ARCHITECTURE_MANAGER
cp "$ROOT/architecture-manager/target/architecture-manager-0.1.jar" $ARCHITECTURE_MANAGER

docker-compose rm -f -v
docker-compose create --build
