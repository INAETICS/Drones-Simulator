#!/bin/bash

ROOT="implementation" 
STATIC="static"
DEPENDENT_BUNDLES="$STATIC/dependent-bundles"

set -e

if [[ $1 == "no-maven" ]]; then
    echo "Skipping maven because the script received commandline parameters"
else
    (cd $ROOT && mvn clean install -Dmaven.test.skip=true)
fi

if [[ "$1" == *"yml" ||  "$1" == *"yaml" ]]
then
    COMPOSEFILE=${1:-docker-compose.yml}
else
    COMPOSEFILE=${2:-docker-compose.yml}
fi

echo "We will be using the following docker-compose file: ${COMPOSEFILE}"


DOCKER_IMAGES="docker_images"
BASE_IMAGE="$DOCKER_IMAGES/base_image"
GAME_ENGINE_IMAGE="$DOCKER_IMAGES/game_engine"
DRONE_IMAGE="$DOCKER_IMAGES/drone"
ARCHITECTURE_MANAGER_IMAGE="$DOCKER_IMAGES/architecture_manager"

# Remove old images if exists
if [ -d "$DOCKER_IMAGES" ]; then
	echo "Removing old docker images! At: $DOCKER_IMAGES"
	rm -R "$DOCKER_IMAGES"
fi

# Setup image dirs
BASE_IMAGE_DEPENDENCIES="$BASE_IMAGE/files/dependencies"
mkdir -p "$BASE_IMAGE_DEPENDENCIES"

GAME_ENGINE_BUNDLES="$GAME_ENGINE_IMAGE/files/bundles"
mkdir -p "$GAME_ENGINE_IMAGE/files/bundles"

DRONE_BUNDLES="$DRONE_IMAGE/files/bundles"
mkdir -p "$DRONE_BUNDLES"

ARCHITECTURE_MANAGER_BUNDLES="$ARCHITECTURE_MANAGER_IMAGE/files/bundles"
mkdir -p "$ARCHITECTURE_MANAGER_BUNDLES"


# BASE IMAGE #
##############

# Copy and overwrite felix config in base image
cp "$STATIC/felix-config.properties" "$BASE_IMAGE/files/config.properties"

# Copy the common dependencies to base image
cp "$DEPENDENT_BUNDLES/"*".jar" "$BASE_IMAGE_DEPENDENCIES"
cp -r "$STATIC/jvmtop" "$BASE_IMAGE/files/jvmtop"

# Copy base image docker file
cp "$STATIC/base_image_Dockerfile" "$BASE_IMAGE/Dockerfile"

# Build base docker image 
docker build $BASE_IMAGE -t dronesim/base


# ETCD #
########

# Copy etcd image
cp -r "$STATIC/etcd_docker_image" "$DOCKER_IMAGES/etcd"


# GAME ENGINE #
###############

# Copy game engine Dockerfile
cp "$STATIC/osgi_Dockerfile" "$GAME_ENGINE_IMAGE/Dockerfile"

# Game engine add bundles
cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/common/target/common-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/common/target/gameengine-common-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/core/target/gameengine-core-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/gamestate-manager/target/gameengine-gamestate-manager-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/physicsengine/target/gameengine-physicsengine-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/physicsenginedriver/target/gameengine-physicsenginedriver-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/identifier-mapper/target/gameengine-identifier-mapper-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/gameengine/ruleprocessors/target/gameengine-ruleprocessors-0.1.jar" $GAME_ENGINE_BUNDLES
cp "$ROOT/architecture-event-controller/target/architecture-event-controller-0.1.jar" $GAME_ENGINE_BUNDLES


# DRONE #
#########

# Copy drone Dockerfile
cp "$STATIC/osgi_Dockerfile" "$DRONE_IMAGE/Dockerfile"

# Drone add bundles
cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/common/target/common-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/components/engine/target/components-engine-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/components/gps/target/components-gps-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/components/gun/target/components-gun-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/components/radar/target/components-radar-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/components/radio/target/components-radio-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/drone-init/target/drone-init-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/drone/tactic/target/tactic-0.1.jar" $DRONE_BUNDLES
cp "$ROOT/architecture-event-controller/target/architecture-event-controller-0.1.jar" $DRONE_BUNDLES


# ARCHITECTURE MANAGER #
########################

# Copy architecture manager Dockerfile
cp "$STATIC/osgi_Dockerfile" "$ARCHITECTURE_MANAGER_IMAGE/Dockerfile"

# Architecture Manager bundles
cp "$ROOT/discovery/api/target/discovery-api-0.1.jar" $ARCHITECTURE_MANAGER_BUNDLES
cp "$ROOT/discovery/etcd/target/etcd-0.1.jar" $ARCHITECTURE_MANAGER_BUNDLES
cp "$ROOT/pubsub/api/target/pubsub-api-0.1.jar" $ARCHITECTURE_MANAGER_BUNDLES
cp "$ROOT/common/target/common-0.1.jar" $ARCHITECTURE_MANAGER_BUNDLES
cp "$ROOT/architecture-manager/target/architecture-manager-0.1.jar" $ARCHITECTURE_MANAGER_BUNDLES

sh docker_refresh_images.sh $COMPOSEFILE
