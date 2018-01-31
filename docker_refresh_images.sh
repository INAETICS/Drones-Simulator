#!/bin/bash

docker-compose -f ${1:-docker-compose.yml} rm -f -v
docker-compose -f ${1:-docker-compose.yml} create --build
