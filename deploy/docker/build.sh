#!/bin/bash

# Parse args
while [ "$#" -gt 0 ]; do
  case "$1" in
    -n) name="$2"; shift 2;;
    -p) profile="$2"; shift 2;;

    --name=*) name="${1#*=}"; shift 1;;
    --profile=*) profile="${1#*=}"; shift 1;;
    --name|--profile) echo "$1 requires an argument" >&2; exit 1;;

    -*) echo "unknown option: $1" >&2; exit 1;;
    *) handle_argument "$1"; shift 1;;
  esac
done

# Validate args
if [ ! ${profile} ]; then
  echo "Profile required: -p dev|stage|prod OR --profile=dev|stage|prod"
  exit -1
fi

echo "Specified profile: $profile"

echo "==================== Building postgres docker image ===================="
docker build -t electrica/postgres:${profile} ./deploy/docker/postgres

echo "==================== Building gateway docker image ===================="
docker build -t electrica/gateway:${profile} ./deploy/docker/gateway

echo "==================== Building common docker image ===================="
docker build -t electrica/common:${profile} ./deploy/docker/common

echo "==================== Building gradle projects ===================="
./gradlew bootJar

echo "==================== Building user-service docker image ===================="
cp ./user-service/build/libs/user-service-*.jar ./deploy/docker/user-service/service.jar
docker build -t electrica/user-service:${profile} \
  --build-arg PROFILE=${profile} \
  ./deploy/docker/user-service
rm -rf ./deploy/docker/user-service/service.jar

echo "==================== Building connector-hub-service docker image ===================="
cp ./connector-hub-service/build/libs/connector-hub-service-*.jar ./deploy/docker/connector-hub-service/service.jar
docker build -t electrica/connector-hub-service:${profile} \
  --build-arg PROFILE=${profile} \
  ./deploy/docker/connector-hub-service
rm -rf ./deploy/docker/connector-hub-service/service.jar

echo "==================== Building invoker-service docker image ===================="
cp ./invoker-service/build/libs/invoker-service-*.jar ./deploy/docker/invoker-service/service.jar
docker build -t electrica/invoker-service:${profile} \
  --build-arg PROFILE=${profile} \
  ./deploy/docker/invoker-service
rm -rf ./deploy/docker/invoker-service/service.jar

echo "==================== Building connector-service docker image ===================="
cp ./connector-service/build/libs/connector-service-*.jar ./deploy/docker/connector-service/service.jar
docker build -t electrica/connector-service:${profile} \
  --build-arg PROFILE=${profile} \
  ./deploy/docker/connector-service
rm -rf ./deploy/docker/connector-service/service.jar

echo "==================== Building webhook-service docker image ===================="
cp ./webhook-service/build/libs/webhook-service-*.jar ./deploy/docker/webhook-service/service.jar
docker build -t electrica/webhook-service:${profile} \
  --build-arg PROFILE=${profile} \
  ./deploy/docker/webhook-service
rm -rf ./deploy/docker/webhook-service/service.jar

echo "==================== Building websocket-service docker image ===================="
cp ./websocket-service/build/libs/websocket-service-*.jar ./deploy/docker/websocket-service/service.jar
docker build -t electrica/websocket-service:${profile} \
  --build-arg PROFILE=${profile} \
  ./deploy/docker/websocket-service
rm -rf ./deploy/docker/websocket-service/service.jar
