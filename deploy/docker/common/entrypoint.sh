#!/bin/bash

exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /home/electrica/service.jar ${BUILS_ARGS} ${ARGS} $@
