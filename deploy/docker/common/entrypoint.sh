#!/bin/sh
# required for alpine

exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /home/electrica/service.jar ${BUILS_ARGS} ${ARGS} $@
