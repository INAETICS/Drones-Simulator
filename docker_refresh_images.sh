#!/bin/bash

docker-compose rm -f -v
docker-compose create --build
