# Build docker images
Example to build for `dev` profile:
```
sh deploy/docker/build.sh -p dev
```
OR
```
sh deploy/docker/build.sh --profile=dev
```
Possible profiles are: `dev`, `stage`, `prod`.

## Env variables
- `JAVA_OPTS` - specify java options to start micro-service
- `ARGS` - specify application args, can be used to override Spring properties (e.q `--spring.liquibase.enabled=true`)

## Custom args
Any args also can be passed with start container:
```
docker run electrica/user-service:dev --spring.liquibase.enabled=true
```

# Start cluster 1
Example to start cluster for `dev` profile:
```
docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml up
```

Example to down cluster for `dev` profile and remove all containers and data:
```
docker-compose -f deploy/docker-compose/cluster.yml -f deploy/docker-compose/cluster.dev.yml down
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
