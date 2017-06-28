#!/bin/bash
rabbitmq-server -detached
echo "RabbitMQ starting ..."

while ! nc -z localhost 5672; do   
  sleep 0.1 # wait for 1/10 of the second before check again
done

echo "RabbitMQ is gestart"

USERNAME=yourUser
PASSWORD=yourPass

	# add rabbitmq user
rabbitmqctl add_user $USERNAME $PASSWORD
rabbitmqctl set_permissions -p / $USERNAME ".*" ".*" ".*"
rabbitmqctl set_user_tags $USERNAME administrator

echo "User $USERNAME toegevoegd aan RabbitMQ"

# etcd paths
INSTANCE_DIR="/instances/rabbitmq/broker/default"

# RabbitMQ config
RABBITMQ_PATH="amqp://rabbitmq:5672"

# Actually set values in etcd
curl http://etcd:4001/v2/keys$INSTANCE_DIR/uri -XPUT -d value="$RABBITMQ_PATH"
curl http://etcd:4001/v2/keys$INSTANCE_DIR/username -XPUT -d value=$USERNAME
curl http://etcd:4001/v2/keys$INSTANCE_DIR/password -XPUT -d value=$PASSWORD

# Confirmation
echo "RabbitMQ registered with URI $RABBITMQ_PATH"



function finish {
  # Your cleanup code here
  echo "Remove RabbitMQ from etcd"
  curl http://etcd:4001/v2/keys$INSTANCE_DIR/uri -XDELETE
  curl http://etcd:4001/v2/keys$INSTANCE_DIR/username -XDELETE
  curl http://etcd:4001/v2/keys$INSTANCE_DIR/password -XDELETE

  echo "Stop RabbitMQ server"
  rabbitmqctl stop
  
}
trap finish SIGTERM
while true; do :; done



