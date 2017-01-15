#!/usr/bin/env bash

cd ./docker
cp ../../../target/core-1.0-SNAPSHOT-jar-with-dependencies.jar objectagon-core.jar
docker build -t sensera/objectagon .