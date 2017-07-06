- The code is located in `implementation/`
- The compiled docker images are located in `docker_images/`
- Static files which should not be changed are located in `static/`
- The visualization is locaed in `visualiztion/`

# Test
To test the entire codebase, first start a instance of etcd & RabbitMQ on localhost. Default installations work. For etcd you should be able to connect without any credentials. For RabbitMQ the connection will be made with `guest\guest`.

Now run the following in `implementation/`:
```
mvn test
```

# SonarQube
To run SonarQube on the codebase, first install and start an instance of SonarQube on localhost.

Now run the following in `implementation/`:
```
mvn test
mvn sonar:sonar
```

# To run on a host
As a requirement, make sure you have installed and started docker.
Config the EXTERNAL ip adres in `docker.env` for etcd and RabbitMQ respectively
Now run from git root:
```
docker_compiler_images.sh
```

The docker images will be added to your local docker. Start the docker containers for each host respectively.

# Multiple drones
If you would like to start multiple drones on the same host, run the following after compiling the docker images:
```
cd docker_images/
docker-compose up --scale drone=<DRONE AMOUNT> drone
``` 

# Visualization
The visualization should be run without Docker. First, config the EXTERNAL ip address in `visualization/config_visualization` for both etcd and RabbitMQ respectively. You can start the visualization by running the following from the git root:
```
cd visualization/
./compile_visualization.sh
./start_visualization.sh
```