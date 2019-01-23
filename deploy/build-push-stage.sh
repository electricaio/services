#!/bin/bash

echo ========================================= Started at $(date) =========================================

cd /home/ubuntu/services && \
  ./gradlew clean assemble && \
  sh deploy/docker/build.stage.sh && \
  export TAG=stage && \
  sh deploy/teamcity/push-docker.sh
