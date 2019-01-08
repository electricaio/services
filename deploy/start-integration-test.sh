#!/bin/bash

echo ========================================= Started at $(date) =========================================

cd /home/ubuntu/services

./gradlew :it-service:integrationTest -PexecIntegrationTest=true