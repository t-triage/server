#!/bin/bash

mvn dependency:go-offline
mvn package -DskipTests
docker build . -t ttriage/server
