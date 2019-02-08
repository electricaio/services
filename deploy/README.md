# Build docker images
To build Docker images execute following:
```
./gradlew clean assemble && sh deploy/docker/build.sh
```

## Override Properties
There are two ways to override application properties for Docker container.

### Env variables
- `JAVA_OPTS` - specify java options to start micro-service
- `ARGS` - specify application args, can be used to override Spring properties (e.q `--spring.liquibase.enabled=true`)
- `PROPERTY_NAME` - set env variable with the same name of the property (e.q `SPRING_LIQUIBASE_ENABLED=true` or `spring.liquibase.enabled=true`) 
E.q
```bash
docker run electrica/user-service -e ARGS="--spring.liquibase.enabled=true" -e JAVA_OPTS="Xmx256" -e SPRING_RABBIT_HOST=localhost 
```

### Start arguments
Any args also can be passed to container:
```bash
docker run electrica/user-service --spring.liquibase.enabled=true
```

# Start cluster locally
Below is example how to start cluster locally with `dev` profile:
```
docker-compose -f deploy/docker-compose/cluster.yml up
```
And down it **removing** all containers and **data**:
```
docker-compose -f deploy/docker-compose/cluster.yml down
```

# Start Postgres locally
Below is example how to start Postgres locally with all backend expected databases:
```
docker-compose -f deploy/docker-compose/postgres.yml up
```
And down it **removing** all **data**:
```
docker-compose -f deploy/docker-compose/postgres.yml down
```

# Setup Dev Stand

##### 1. Initial Env setup
```bash
sudo apt-get update && \
  sudo apt-get upgrade && \
  sudo apt-get install mc awscli apt-transport-https ca-certificates curl software-properties-common
```

##### 2. Install Docker
```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
sudo apt update && sudo apt install docker-ce
sudo usermod -aG docker ${USER}
```

##### 3. Configure AWS CLI
```bash
aws configure
(aws ecr get-login --no-include-email) | bash
```

##### 4. Install Docker Compose
```bash
sudo apt install docker-compose
```

##### 5. Clone Services repo
Create keypair if not exist and add public key to Github 
```bash
ssh-keygen -t rsa -b 4096 -C "dev-stand@electrica.io"
```
Clone repo
```bash
cd ~/ && git clone git@github.com:electricaio/services.git
```

##### 6. Configure Crontab
```bash
crontab -e
```
And replace with `~/services/deploy/crontab` content.

##### 7. Reboot
```bash
sudo reboot
```

##### 8. Run manually first time
Execute command, configured for Crontab.
