#!/bin/sh
# required for alpine

sleep 60 && \
    envsubst '${HOST_NAME}' < /etc/nginx/conf.d/api.conf.template > /etc/nginx/conf.d/api.conf && \
    echo Starting gateway..
    nginx -g 'daemon off;'
