#!/usr/bin/env bash

mvn clean package
docker build -t sensera/objectagon .