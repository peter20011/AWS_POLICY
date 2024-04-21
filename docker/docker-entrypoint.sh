#!/bin/bash

echo "server.port=8082" >> application.properties

echo "Server has started."

java -jar app.jar