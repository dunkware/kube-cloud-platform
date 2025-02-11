#!/bin/bash
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
HELM_CHART_PATH="../helm/mvp-infra"
VALUES_FILE="./test-infra-values.yaml"
NAMESPACE="mvp-test-infra"

kubectl create namespace ${NAMESPACE} || true   
# Create namespace if it doesn't exist
kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -

# Run helm upgrade
helm upgrade --install mvp-test-infra \
    ${HELM_CHART_PATH} \
    -f ${VALUES_FILE} \
    -n ${NAMESPACE}

echo "Helm upgrade completed Cloud Infra"
echo "Postres should be avaialble on node port 31438 "