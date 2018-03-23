<!--
-->

# Building 

Run `mvn install` to build to create the single jar osgi frameworks and to 
build the docker images

# Run without docker

Use `java -jar target/drone.radar.jar`. 
Use the OSGi command `help` and `dm` (dependency manager) to get more info
about the running bundles/components

# Run with docker

Docker images are build during the install phase ('mvn install')
Use `docker run -t -i drone-simulator-radar-tactic` to run the docker image or
use `docker run -t -i -d drone-simulator-radar-tactic` to run it detached