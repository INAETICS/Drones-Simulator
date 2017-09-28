#!/bin/bash

docker start dronessimulator_etcd_1
docker start dronessimulator_rabbitmq_1
sleep 2s
docker start dronessimulator_game-engine_1
docker start dronessimulator_architecture-manager_1

docker-compose up -d --scale drone=4 drone
