# Generate Harbor auth string (macOS)
HARBOR_AUTH=$(echo -n "robot\$dunkware+dunkware-gradle-push:31g67irMrjkYbrU1NiajFCsCQbTw4KOL" | base64)
DOCKER_CONFIG=$(echo -n "{\"auths\":{\"harbor.dunkware.net\":{\"auth\":\"${HARBOR_AUTH}\"}}}" | base64)

kubectl create namespace mvp-test-root || true  
# Create the secret in Kubernetes
kubectl create secret docker-registry dunkware-harbor-secret \
    --namespace=mvp-test-root \
    --docker-server=harbor.dunkware.net \
    --docker-username=robot\$dunkware+dunkware-gradle-push \
    --docker-password=31g67irMrjkYbrU1NiajFCsCQbTw4KOL \
    --docker-email=none