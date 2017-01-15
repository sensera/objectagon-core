#!/usr/bin/env bash

java -jar objectagon-core.jar >> /tmp/objectagon.log 2>&1 &
tail -f /tmp/objectagon.log