#!/bin/bash

# Define variables
JAR_FILE="server-oshi-0.0.1.jar"

# Start the Java application in the background
nohup java -jar ${JAR_FILE} > app.log 2>&1 &
