# services

# Setup your dev environment
1.) Make sure you have a Postgres database running locally (or somewhere). Configure your Postgres settings under: `common/src/main/resources/common.properties`

2.) Create databases in Postgres - `user_db` , `connector_hub_db` and `webhook_db`

3.) Install Docker from https://docs.docker.com/docker-for-mac/install/

# Building your environment
1.) `gradlew clean build`

2.) Best Practices for committing code are documented on the [wiki](https://electricaio.atlassian.net/wiki/spaces/EA/pages/30703619/Commits+and+PRs)

# Code coverage requirements
1.) Minimum code coverage requirement for the repo is 75%.

2.) We bump it upwards by 5% each week.

3.) Your PR cannot drop the code coverage by more than 1% at any point in time.

# Running the services cluster locally
## Start the cluster
`$ ./deploy/docker/build.sh -p dev`

`$ docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml up`

You will be able to access the services using the url: [http://localhost:8080/swagger](http://localhost:8080/swagger)

## Remove containers and drop db
`$ docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml down`

# Micro services
This is a multi-module gradle build with each microservice running in it's own module. Each microservice has 3 major sub-modules and can be found in the root folder:<br/>
`<service-name>-service-api`<br/>
`<service-name>-service`<br/>

# Questions?
Head to slack channel [dev-backend](https://electricaio.slack.com/messages/CDAG9KTUN/) to get your questions answered.



[![Build Status](https://travis-ci.com/electricaio/services.svg?token=XaPqFymCCMvmv4mU5F9x&branch=master)](https://travis-ci.com/electricaio/services)
[![Coverage Status](https://coveralls.io/repos/github/electricaio/services/badge.svg?branch=master&t=RKKuow)](https://coveralls.io/github/electricaio/services?branch=master)

