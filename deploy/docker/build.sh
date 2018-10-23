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
  echo "Profile required: -p dev|staging|prod OR --profile=dev|staging|prod"
  exit -1
fi

echo "Specified profile: $profile"

echo "==================== Building postgres docker image ===================="
docker build -t electrica/postgres:${profile} ./deploy/docker/postgres

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