#!/bin/bash

source config_visualization
export ETCD_HOST
export ETCD_PORT

java -jar -Dorg.ops4j.pax.logging.DefaultServiceLog.level=$DEBUG_LEVEL visualisation-0.1.jar
