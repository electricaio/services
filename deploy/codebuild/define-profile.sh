#!/bin/sh

echo "AWS CodeBuild Extra Environment Variables:
    AWS_DEFAULT_REGION = $AWS_DEFAULT_REGION
    AWS_REGION = $AWS_REGION
    CODEBUILD_BUILD_ARN = $CODEBUILD_BUILD_ARN
    CODEBUILD_BUILD_ID = $CODEBUILD_BUILD_ID
    CODEBUILD_BUILD_IMAGE = $CODEBUILD_BUILD_IMAGE
    CODEBUILD_BUILD_SUCCEEDING = $CODEBUILD_BUILD_SUCCEEDING
    CODEBUILD_INITIATOR = $CODEBUILD_INITIATOR
    CODEBUILD_KMS_KEY_ID = $CODEBUILD_KMS_KEY_ID
    CODEBUILD_LOG_PATH = $CODEBUILD_LOG_PATH
    CODEBUILD_RESOLVED_SOURCE_VERSION = $CODEBUILD_RESOLVED_SOURCE_VERSION
    CODEBUILD_SOURCE_REPO_URL = $CODEBUILD_SOURCE_REPO_URL
    CODEBUILD_SOURCE_VERSION = $CODEBUILD_SOURCE_VERSION
    CODEBUILD_SRC_DIR = $CODEBUILD_SRC_DIR
    CODEBUILD_START_TIME = $CODEBUILD_START_TIME
    HOME = $HOME
"

if [ ! ${CODEBUILD_INITIATOR} ]; then
    echo Required CODEBUILD_INITIATOR env variable to set profile
    exit -1
elif [ ${CODEBUILD_INITIATOR} = "codepipeline/backend-dev" ]; then
    export APP_PROFILE="dev"
elif [ ${CODEBUILD_INITIATOR} = "codepipeline/backend-stage" ]; then
    export APP_PROFILE="stage"
elif [ ${CODEBUILD_INITIATOR} = "codepipeline/backend-prod" ]; then
    export APP_PROFILE="prod"
fi

echo Build started for \'$APP_PROFILE\' profile