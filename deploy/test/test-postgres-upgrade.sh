#!/bin/bash
set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
HELM_CHART_PATH="../helm/mvp-postgres"
VALUES_FILE="./test-postgres-values.yaml"
NAMESPACE="mvp-test-infra"

# Remove the manual namespace creation
# Let Helm handle it with --create-namespace flag

helm upgrade --install postgres \
    ${HELM_CHART_PATH} \
    -f ${VALUES_FILE} \
    -n ${NAMESPACE} \
    --create-namespace

echo "Helm upgrade completed Cloud Infra"
echo "Postgres should be available on node port 31438"