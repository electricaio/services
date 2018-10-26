#!/bin/bash

echo ========================================= Started at $(date) =========================================

export ECR_HOST="251063236326.dkr.ecr.us-east-2.amazonaws.com/"

cd /home/ubuntu/services

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml down

git pull

docker pull ${ECR_HOST}electrica/postgres:dev
docker pull ${ECR_HOST}electrica/user-service:dev
docker pull ${ECR_HOST}electrica/connector-hub-service:dev

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml up -d
