# Define args for FROM clause (see https://github.com/moby/moby/pull/31352#issuecomment-315108094)
ARG PROFILE
FROM electrica/common:${PROFILE}

# Define args
ARG PROFILE

ENV BUILS_ARGS="--spring.profiles.active=${PROFILE}"

# Copy service jar
ADD service.jar service.jar

# Copy configuration
ADD application-${PROFILE}.yml config/application-${PROFILE}.yml

EXPOSE 22023