#!/bin/bash

rabbitmq-server -detached
echo "RabbitMQ started"


sleep 10


USERNAME=yourUser
PASSWORD=yourPass

# add rabbitmq user
rabbitmqctl add_user $USERNAME $PASSWORD
rabbitmqctl set_permissions -p / $USERNAME ".*" ".*" ".*"
rabbitmqctl set_user_tags $USERNAME administrator

echo "User $USERNAME toegevoegd aan rabbitmq"

# etcd paths
INSTANCE_DIR="/instances/rabbitmq/broker/default"

# RabbitMQ config
RABBITMQ_PATH="amqp://rabbitmq:5672"

# Actually set values
curl http://etcd:4001/v2/keys$INSTANCE_DIR/uri -XPUT -d value="$RABBITMQ_PATH"
curl http://etcd:4001/v2/keys$INSTANCE_DIR/username -XPUT -d value=$USERNAME
curl http://etcd:4001/v2/keys$INSTANCE_DIR/password -XPUT -d value=$PASSWORD

# Confirmation
echo "RabbitMQ registered with URI $RABBITMQ_PATH"

rabbitmqctl stop

sleep 2

rabbitmq-server start



