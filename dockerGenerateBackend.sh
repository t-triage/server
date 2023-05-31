#!/bin/bash

mvn dependency:go-offline
mvn package -DskipTests
docker build . -t ttriagebe/ttriage
docker tag ttriagebe/ttriage:latest dev.clarolab.com:8882/clarolab-ttriage:latest
