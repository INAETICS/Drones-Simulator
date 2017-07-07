#!/bin/bash

source ../config.env
export ETCD_HOST
export ETCD_PORT

export ARENA_WIDTH
export ARENA_DEPTH
export ARENA_HEIGHT

export GAME_MODE

java -jar -Dorg.ops4j.pax.logging.DefaultServiceLog.level=$DEBUG_LEVEL visualisation-0.1.jar
