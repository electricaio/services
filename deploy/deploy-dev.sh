#!/bin/bash

echo ========================================= Started at $(date) =========================================

export ECR_HOST="251063236326.dkr.ecr.us-east-2.amazonaws.com/"
export HOST_NAME="ec2-18-191-140-153.us-east-2.compute.amazonaws.com"

cd /home/ubuntu/services

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml down

git pull

docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml up -d
