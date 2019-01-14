#!/bin/bash

echo ========================================= Started at $(date) =========================================

cd /home/ubuntu/services

(aws ecr get-login --no-include-email) | bash

export ECR_HOST="251063236326.dkr.ecr.us-east-2.amazonaws.com/"

echo ============= Building images =============

sh deploy/docker/build.sh -p stage

echo ============= Pushing images =============

# gateway
docker tag electrica/gateway:stage ${ECR_HOST}electrica/gateway:stage && \
  docker push ${ECR_HOST}electrica/gateway:stage

# user-service
docker tag electrica/user-service:stage ${ECR_HOST}electrica/user-service:stage && \
  docker push ${ECR_HOST}electrica/user-service:stage

# connector-hub-service
docker tag electrica/connector-hub-service:stage ${ECR_HOST}electrica/connector-hub-service:stage && \
  docker push ${ECR_HOST}electrica/connector-hub-service:stage

# invoker-service
docker tag electrica/invoker-service:stage ${ECR_HOST}electrica/invoker-service:stage && \
  docker push ${ECR_HOST}electrica/invoker-service:stage

# connector-service
docker tag electrica/connector-service:stage ${ECR_HOST}electrica/connector-service:stage && \
  docker push ${ECR_HOST}electrica/connector-service:stage

# webhook-service
docker tag electrica/webhook-service:stage ${ECR_HOST}electrica/webhook-service:stage && \
  docker push ${ECR_HOST}electrica/webhook-service:stage

# websocket-service
docker tag electrica/websocket-service:stage ${ECR_HOST}electrica/websocket-service:stage && \
  docker push ${ECR_HOST}electrica/websocket-service:stage
