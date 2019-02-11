# Backend Services

## Setup your environment
1. Install Docker.
2. Install Docker Compose.

## Building your environment
- Install Lombok plugin and enable annotation processing in your IDE.
- Assemble project with Gradle:
```bash
./gradlew clean assemble
```
- We use Checkstyle and Findbug plugins to check code-style. You can run it locally before PR:
```bash
./gradlew check
```
Or commend below if you want to skip tests:
```bash
./gradlew check -x test
```

## Development Flow
Best Practices for committing code are documented on the [wiki](https://electricaio.atlassian.net/wiki/spaces/EA/pages/30703619/Commits+and+PRs)

## Code coverage requirements
1. Minimum code coverage requirement for the repo is 75%.
2. We bump it upwards by 5% each week.
3. Your PR cannot drop the code coverage by more than 1% at any point in time.

## Build Docker Images
To build Docker images execute following:
```
./gradlew clean assemble && sh deploy/docker/build.sh
```

## Running the functional tests locally
To run functional tests locally, you have to have running Postgres instance.
Below is example how to start Postgres locally with all backend expected databases:
```
docker-compose -f deploy/docker-compose/postgres.yml up
```
And down it **removing** all **data**:
```
docker-compose -f deploy/docker-compose/postgres.yml down
```

## Running the services cluster locally
Below is example how to start cluster locally with `dev` profile:
```
docker-compose -f deploy/docker-compose/cluster.yml up
```
And down it **removing** all containers and **data**:
```
docker-compose -f deploy/docker-compose/cluster.yml down
```

## Running the integration tests locally
1. build Docker images;
2. start cluster locally;
3. run integration tests with command below:
```bash
./gradlew :it-service:integrationTest -PexecIntegrationTest=true
```
4. optionally down the cluster.

## Micro services
This is a multi-module gradle build with each microservice running in it's own Docker container. Each microservice consists of 2 modules:
- `<service-name>-service-api`
- `<service-name>-service`

## Questions?
Head to slack channel [dev-backend](https://electricaio.slack.com/messages/CDAG9KTUN/) to get your questions answered.
