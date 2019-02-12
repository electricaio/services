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
echo --------------------- Linting and packaging invoker-service --------------------- && \
cp -v common-*.tgz ${electrica_charts_dir}/invoker-service/charts/ && \
  helm lint ${electrica_charts_dir}/invoker-service && \
  helm package ${electrica_charts_dir}/invoker-service && \
\
echo && \
echo --------------------- Linting and packaging connector-service --------------------- && \
cp -v common-*.tgz ${electrica_charts_dir}/connector-service/charts/ && \
  helm lint ${electrica_charts_dir}/connector-service && \
  helm package ${electrica_charts_dir}/connector-service && \
\
echo && \
echo --------------------- Linting and packaging electrica --------------------- && \
helm lint ${helm_dir}/electrica && helm package ${helm_dir}/electrica
