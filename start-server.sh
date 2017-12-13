#!/usr/bin/env bash

mvn package -q -DskipTests
java -jar rest/target/rest-1.0-SNAPSHOT-jar-with-dependencies.jar
