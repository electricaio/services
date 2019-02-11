#!/bin/bash

(aws ecr get-login --no-include-email) | bash

echo Cleaning up all untagged images..
aws ecr describe-repositories --output text | awk '{print $5}' |
  while read line; do
    aws ecr list-images --repository-name $line --filter tagStatus=UNTAGGED --query 'imageIds[*]' --output text |
      while read imageId; do
        aws ecr batch-delete-image --repository-name $line --image-ids imageDigest=$imageId
      done
  done

export ECR_HOST="251063236326.dkr.ecr.us-east-2.amazonaws.com"

#docker tag electrica/postgres ${ECR_HOST}/electrica/postgres:${TAG} && \
#docker push ${ECR_HOST}/electrica/postgres:${TAG} && \
# \
#docker tag electrica/gateway ${ECR_HOST}/electrica/gateway:${TAG} && \
#docker push ${ECR_HOST}/electrica/gateway:${TAG} && \
# \
docker tag electrica/user-service ${ECR_HOST}/electrica/user-service:${TAG} && \
docker push ${ECR_HOST}/electrica/user-service:${TAG} && \
\
docker tag electrica/connector-hub-service ${ECR_HOST}/electrica/connector-hub-service:${TAG} && \
docker push ${ECR_HOST}/electrica/connector-hub-service:${TAG} && \
\
docker tag electrica/invoker-service ${ECR_HOST}/electrica/invoker-service:${TAG} && \
docker push ${ECR_HOST}/electrica/invoker-service:${TAG} && \
\
docker tag electrica/connector-service ${ECR_HOST}/electrica/connector-service:${TAG} && \
docker push ${ECR_HOST}/electrica/connector-service:${TAG} && \
\
docker tag electrica/webhook-service ${ECR_HOST}/electrica/webhook-service:${TAG} && \
docker push ${ECR_HOST}/electrica/webhook-service:${TAG} && \
\
docker tag electrica/websocket-service ${ECR_HOST}/electrica/websocket-service:${TAG} && \
docker push ${ECR_HOST}/electrica/websocket-service:${TAG}  && \
\
docker tag electrica/metric-service ${ECR_HOST}/electrica/metric-service:${TAG} && \
docker push ${ECR_HOST}/electrica/metric-service:${TAG}
