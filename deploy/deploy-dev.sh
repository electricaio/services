#!/bin/bash

echo ========================================= Started at $(date) =========================================

(aws ecr get-login --no-include-email) | bash

export ECR_HOST="251063236326.dkr.ecr.us-east-2.amazonaws.com/"
export HOST_NAME="api.dev.electrica.io"

cd /home/ubuntu/services

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml down

git pull

docker pull rabbitmq:3-management-alpine
docker pull swaggerapi/swagger-ui

docker pull ${ECR_HOST}electrica/postgres:dev
docker pull ${ECR_HOST}electrica/gateway:dev
docker pull ${ECR_HOST}electrica/user-service:dev
docker pull ${ECR_HOST}electrica/connector-hub-service:dev
docker pull ${ECR_HOST}electrica/invoker-service:dev
docker pull ${ECR_HOST}electrica/connector-service:dev
docker pull ${ECR_HOST}electrica/webhook-service:dev
docker pull ${ECR_HOST}electrica/websocket-service:dev

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml up -d
