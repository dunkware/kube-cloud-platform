#!/bin/bash
set -e # exit on error
set -o pipefail # return right most error code (or zero)

NAMESPACE='mvp-test-root'
EUREKA_RELEASE_TAG='latest'
EUREKA_CHART_NAME='mvp-test-web'

kubectl create namespace ${NAMESPACE} || true

helm upgrade \
    --install \
    --namespace=${NAMESPACE} \
    --create-namespace \
    ${EUREKA_CHART_NAME} ../helm/mvp-service \
    --set image.tag=${EUREKA_RELEASE_TAG} \
    --values=test-web-values.yaml

kubectl get pods -n ${NAMESPACE}