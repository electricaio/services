#!/bin/bash

helm_dir=deploy/helm
if [ $HELM_DIR ]; then
  helm_dir="$HELM_DIR"
fi

electrica_charts_dir=${helm_dir}/electrica/charts

echo && \
echo --------------------- Linting and packaging common --------------------- && \
helm lint --strict ${helm_dir}/common && helm package ${helm_dir}/common && \
\
echo && \
echo --------------------- Linting and packaging user-service --------------------- && \
find ${electrica_charts_dir}/user-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/user-service/charts/ && \
  helm lint ${electrica_charts_dir}/user-service && \
  helm package ${electrica_charts_dir}/user-service && \
\
echo && \
echo --------------------- Linting and packaging connector-hub-service --------------------- && \
find ${electrica_charts_dir}/connector-hub-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/connector-hub-service/charts/ && \
  helm lint ${electrica_charts_dir}/connector-hub-service && \
  helm package ${electrica_charts_dir}/connector-hub-service && \
echo && \
echo --------------------- Linting and packaging invoker-service --------------------- && \
find ${electrica_charts_dir}/invoker-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/invoker-service/charts/ && \
  helm lint ${electrica_charts_dir}/invoker-service && \
  helm package ${electrica_charts_dir}/invoker-service && \
\
echo && \
echo --------------------- Linting and packaging connector-service --------------------- && \
find ${electrica_charts_dir}/connector-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/connector-service/charts/ && \
  helm lint ${electrica_charts_dir}/connector-service && \
  helm package ${electrica_charts_dir}/connector-service && \
\
echo && \
echo --------------------- Linting and packaging webhook-service --------------------- && \
find ${electrica_charts_dir}/webhook-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/webhook-service/charts/ && \
  helm lint ${electrica_charts_dir}/webhook-service && \
  helm package ${electrica_charts_dir}/webhook-service && \
\
echo && \
echo --------------------- Linting and packaging websocket-service --------------------- && \
find ${electrica_charts_dir}/websocket-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/websocket-service/charts/ && \
  helm lint ${electrica_charts_dir}/websocket-service && \
  helm package ${electrica_charts_dir}/websocket-service && \
\
echo && \
echo --------------------- Linting and packaging metric-service --------------------- && \
find ${electrica_charts_dir}/metric-service/charts/ -name "*.tgz" -exec rm -rf {} \; && \
  cp -v common-*.tgz ${electrica_charts_dir}/metric-service/charts/ && \
  helm lint ${electrica_charts_dir}/metric-service && \
  helm package ${electrica_charts_dir}/metric-service && \
\
echo && \
echo --------------------- Linting and packaging electrica --------------------- && \
helm lint ${helm_dir}/electrica && helm package ${helm_dir}/electrica
