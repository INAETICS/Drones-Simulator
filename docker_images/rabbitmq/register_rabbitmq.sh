#!/bin/bash
rabbitmq-server -detached
echo "RabbitMQ starting ..."

ETCD_HOST=${ETCD_HOST:="etcd"}
ETCD_PORT=${ETCD_PORT:="4001"}
RABBIT_HOST=${RABBIT_HOST:="rabbitmq"}
RABBIT_PORT=${RABBIT_PORT:="5672"}

# wait until RabbitMQ is started
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
RABBITMQ_PATH="amqp://$RABBIT_HOST:$RABBIT_PORT"

echo "RABBITMQ_PATH=$RABBITMQ_PATH"
echo "etcd url is $ETCD_HOST:$ETCD_PORT"

# wait until etcd is started
while ! nc -z $ETCD_HOST $ETCD_PORT; do   
  sleep 0.1 # wait for 1/10 of the second before check again
done

# Actually set values in etcd
curl http://$ETCD_HOST:$ETCD_PORT/v2/keys$INSTANCE_DIR/uri -XPUT -d value="$RABBITMQ_PATH"
curl http://$ETCD_HOST:$ETCD_PORT/v2/keys$INSTANCE_DIR/username -XPUT -d value=$USERNAME
curl http://$ETCD_HOST:$ETCD_PORT/v2/keys$INSTANCE_DIR/password -XPUT -d value=$PASSWORD

# Confirmation
echo "RabbitMQ registered with URI $RABBITMQ_PATH"

SCRIPT_RUNNING=true;	


function finish {
  # Your cleanup code here
  echo "Remove RabbitMQ from etcd"
  curl http://$ETCD_HOST:$ETCD_PORT/v2/keys$INSTANCE_DIR/uri -XDELETE
  curl http://$ETCD_HOST:$ETCD_PORT/v2/keys$INSTANCE_DIR/username -XDELETE
  curl http://$ETCD_HOST:$ETCD_PORT/v2/keys$INSTANCE_DIR/password -XDELETE

  echo "Stop RabbitMQ server"
  SCRIPT_RUNNING=false;
  rabbitmqctl stop
  
}
trap finish SIGINT SIGTERM SIGQUIT
while "$SCRIPT_RUNNING"; do
	sleep 0.5
done



