## Run Integration Tests locally
By default integration tests will test cluster started locally with docker-compose.
```bash
./gradlew :it-service:integrationTest -PexecIntegrationTest=true
```

## Run for another stand
You have to override some parameters.
### Stand URL
Specify stand endpoint to test using Env variable:
```bash
it-service.stand.url=https://api.stage.electrica.io
```
### Disable waiting for docker images start
```bash
ELECTRICA_IT_TEST_AWAIT_DOCKER_CONTAINERS_ENABLED=false
```
### Override admin user credentials
```bash
it-service.oauth2.user=test@electrica.io
it-service.oauth2.password=secret_password
```
