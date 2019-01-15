#!/bin/bash

echo "==================== Building postgres docker image ====================" && \
docker build --pull -t electrica/postgres ./deploy/docker/postgres && \
\
echo "==================== Building gateway docker image ====================" && \
docker build --pull -t electrica/gateway ./deploy/docker/gateway && \
\
echo "==================== Building common docker image ====================" && \
docker build --pull -t electrica/common ./deploy/docker/common && \
\
echo "==================== Building user-service docker image ====================" && \
cp ./user-service/build/libs/user-service-*.jar ./deploy/docker/user-service/service.jar && \
docker build -t electrica/user-service \
  --build-arg PROFILE=${PROFILE} \
  ./deploy/docker/user-service && \
rm -rf ./deploy/docker/user-service/service.jar && \
\
echo "==================== Building connector-hub-service docker image ====================" && \
cp ./connector-hub-service/build/libs/connector-hub-service-*.jar ./deploy/docker/connector-hub-service/service.jar && \
docker build -t electrica/connector-hub-service \
  --build-arg PROFILE=${PROFILE} \
  ./deploy/docker/connector-hub-service && \
rm -rf ./deploy/docker/connector-hub-service/service.jar && \
\
echo "==================== Building invoker-service docker image ====================" && \
cp ./invoker-service/build/libs/invoker-service-*.jar ./deploy/docker/invoker-service/service.jar && \
docker build -t electrica/invoker-service \
  --build-arg PROFILE=${PROFILE} \
  ./deploy/docker/invoker-service && \
rm -rf ./deploy/docker/invoker-service/service.jar && \
\
echo "==================== Building connector-service docker image ====================" && \
cp ./connector-service/build/libs/connector-service-*.jar ./deploy/docker/connector-service/service.jar && \
docker build -t electrica/connector-service \
  --build-arg PROFILE=${PROFILE} \
  ./deploy/docker/connector-service && \
rm -rf ./deploy/docker/connector-service/service.jar && \
\
echo "==================== Building webhook-service docker image ====================" && \
cp ./webhook-service/build/libs/webhook-service-*.jar ./deploy/docker/webhook-service/service.jar && \
docker build -t electrica/webhook-service \
  --build-arg PROFILE=${PROFILE} \
  ./deploy/docker/webhook-service && \
rm -rf ./deploy/docker/webhook-service/service.jar && \
\
echo "==================== Building websocket-service docker image ====================" && \
cp ./websocket-service/build/libs/websocket-service-*.jar ./deploy/docker/websocket-service/service.jar && \
docker build -t electrica/websocket-service \
  --build-arg PROFILE=${PROFILE} \
  ./deploy/docker/websocket-service && \
rm -rf ./deploy/docker/websocket-service/service.jar
