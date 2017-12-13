#!/usr/bin/env bash

java -jar objectagon-core.jar >> /objectagon.log 2>&1 &
tail -f /objectagon.log