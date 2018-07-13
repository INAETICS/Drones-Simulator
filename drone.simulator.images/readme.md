<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at
   
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
