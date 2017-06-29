- The code is located in `implementation/`
- The compiled docker images are located in `docker_images/`


#Test
To test the entire codebase, first start a instance of etcd & RabbitMQ on localhost. Default installations work. For etcd you should be able to connect without any credentials. For RabbitMQ the connection will be made with `guest\guest`.

Now run the following in `implementation/`:
```
mvn test
```

#SonarQube
To run SonarQube on the codebase, first install and start an instance of SonarQube on localhost.

Now run the following in `implementation/`:
```
mvn test
mvn sonar:sonar
```