# Build docker images
Example to build for `dev` profile:
```
sh deploy/docker/build.sh -p dev
```
OR
```
sh deploy/docker/build.sh --profile=dev
```
Possible profiles are: `dev`, `staging`, `prod`.

## Env variables
- `JAVA_OPTS` - specify java options to start micro-service
- `ARGS` - specify application args, can be used to override Spring properties (e.q `--spring.liquibase.enabled=true`)

## Custom args
Any args also can be passed with start container:
```
docker run electrica/user-service:dev --spring.liquibase.enabled=true
```

# Start cluster
Example to start cluster for `dev` profile:
```
docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml up
```

Example to down cluster for `dev` profile and remove all containers and data:
```
docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml down
```
