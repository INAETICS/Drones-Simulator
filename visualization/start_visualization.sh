#!/bin/bash

source config_visualization
export ETCD_HOST
export ETCD_PORT

java -jar visualisation-0.1.jar
