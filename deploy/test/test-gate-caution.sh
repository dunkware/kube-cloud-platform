#!/bin/bash
set -e # exit on error
set -o pipefail # return right most error code (or zero)
read -p "Do you really want to delete Use MVP Test Gate Service (Y/n) " -n 1 -r
echo 
NAMESPACE='mvp-test-root'
EUREKA_CHART_NAME='mvp-test-gate'
#if [[ $REPLY =~ ^[Yy]$ ]]
#then
    helm uninstall ${EUREKA_CHART_NAME} --namespace ${NAMESPACE} || true
    #kubectl delete -f ./storage-pvc.yaml -n ${NAMESPACE} || true
    kubectl delete configmap ${EUREKA_CHART_NAME} -n ${NAMESPACE} || true
#fi
kubectl get pods -n ${NAMESPACE}