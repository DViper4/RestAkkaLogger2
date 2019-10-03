#!/bin/bash
mvn clean package
docker rm -f $(docker ps -a -q)
docker rmi -f $(docker images -a -q)
docker build . -t akka-server-image
docker run -t -p 8080:8080 --name akka-server-container akka-server-image
