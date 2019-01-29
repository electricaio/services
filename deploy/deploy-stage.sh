#!/bin/bash

echo ========================================= Started at $(date) =========================================

(aws ecr get-login --no-include-email) | bash

export ECR_HOST="251063236326.dkr.ecr.us-east-2.amazonaws.com/"
export HOST_NAME="api.stage.electrica.io"

cd /home/ubuntu/services

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.stage.yml down

git pull

docker pull rabbitmq:3-management-alpine
docker pull swaggerapi/swagger-ui

docker pull ${ECR_HOST}electrica/gateway:stage
docker pull ${ECR_HOST}electrica/user-service:stage
docker pull ${ECR_HOST}electrica/connector-hub-service:stage
docker pull ${ECR_HOST}electrica/invoker-service:stage
docker pull ${ECR_HOST}electrica/connector-service:stage
docker pull ${ECR_HOST}electrica/webhook-service:stage
docker pull ${ECR_HOST}electrica/metric-service:stage
docker pull ${ECR_HOST}electrica/websocket-service:stage

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.stage.yml up -d
