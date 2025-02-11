#!/bin/bash
set -e # exit on error
set -o pipefail # return right most error code (or zero)

NAMESPACE='mvp-test-root'
EUREKA_RELEASE_TAG='latest'
EUREKA_CHART_NAME='mvp-test-eureka'

kubectl create namespace ${NAMESPACE} || true
kubectl delete configmap ${EUREKA_CHART_NAME} -n ${NAMESPACE} || true
kubectl create configmap ${EUREKA_CHART_NAME} --namespace=${NAMESPACE} --from-file=$(pwd)/config/eureka || true

helm upgrade \
    --install \
    --namespace=${NAMESPACE} \
    --create-namespace \
    ${EUREKA_CHART_NAME} ../helm/mvp-service \
    --set image.tag=${EUREKA_RELEASE_TAG} \
    --values=test-eureka-values.yaml

kubectl get pods -n ${NAMESPACE}