#!/bin/bash

# This should be a temporary file until

# etcd paths
INSTANCE_DIR="/instances/rabbitmq/broker/default"
CONFIG_DIR="/configs"
CONFIG_ID=1

# RabbitMQ config
RABBITMQ_PATH="amqp://localhost:5672"

# Actually set values
etcdctl set $INSTANCE_DIR/uri $RABBITMQ_PATH > /dev/null
etcdctl set $CONFIG_DIR/$CONFIG_ID $INSTANCE_DIR > /dev/null

# Confirmation
echo "RabbitMQ registered with URI $RABBITMQ_PATH"

