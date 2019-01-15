FROM electrica/common

# Define args
ARG PROFILE

ENV BUILS_ARGS="--spring.profiles.active=${PROFILE}"

# Copy service jar
ADD service.jar service.jar

# Copy configuration
ADD application-${PROFILE}.yml config/application-${PROFILE}.yml

EXPOSE 22026
