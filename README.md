This project contains a drone simulation program. The drones are controlled by an autonomous tactic. All components run in docker containers to create a boundary 
between the components. The components use OSGi as a base. To communicate, RabbitMQ is used as a message broker and Etcd is used for discovery of the available 
containers.

- The code is located in `implementation/`
- The compiled docker images are located in `docker_images/`
- Static files which should not be changed are located in `static/`
- The visualization is located in `visualiztion/`

# Config
First copy `config-default.env` to `config.env` and configure it for your system. Anytime you change the configuration, you should recompile the docker images. The 
visualization will not have to be recompiled, just restarted. Note that the project should work by default if you copy the default config as it uses the docker ip 
instead of a public ip. Localhost will never work since the config will copied into the docker containers.

# Test
The application contains two types of tests: unit tests and integration tests. The unit tests can be run by executing the following command from the `implementation/` 
directory:
```
mvn test -Dskip.surefire.tests=false -Dskip.failsafe.tests=true 
```
To run the integration tests, you need to start the docker containers first by executing the following command:
```
docker-compose up -d
```
When this is all started, you can execute the following command to start the integration tests:
```
mvn verify -Dskip.surefire.tests=true -Dskip.failsafe.tests=false
```
Note that in both instances we have to specify which kind of plugin to skip and which not to skip. The surefire plugin is used for unit tests and the failsafe plugin 
for integration tests.

To create new tests please consider the following naming convention: unit test classes should end with `Test`, and integration test classes should end with `IT`.

# To run on a host
As a requirement, make sure you have installed and started docker.
Now run from git root:
```
docker_compile_images.sh
```

The docker images will be added to your local docker. Start the docker containers for each host respectively.
To start the containers you can run: 
```
docker-compose up -d
```
If you only want to start the drones you can use:
```
docker-compose -f docker-compose-drones.yml up -d
``` 
Note that this uses the docker-compose-drones.yml file instead of docker-compose.yml
The same holds for server-components only


# Visualization
The visualization should be run without docker. You can start the visualization by running the following from the git root:
```
cd visualization/
./compile_visualization.sh
./start_visualization.sh
```

# SonarQube
To run SonarQube on the codebase, first install and start an instance of SonarQube on localhost.

Now run the following in `implementation/`:
```
mvn test -Dskip.surefire.tests=false -Dskip.failsafe.tests=true
mvn sonar:sonar
```

# Continuous Integration
This project is setup to use three services:
- Travis-ci for running the build, running the tests and making calls to the other services mentioned below
- Coverity for security analysis of the code
- SonarCloud for analysis of the report created by the command `mvn sonar:sonar`

The current build status is: <INSERT BADGE HERE>
