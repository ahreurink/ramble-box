DOCKER_IMAGE=ramble-box:latest
DOCKER_IMAGE_FILE=ramble-box.latest.tar
DOCKER_CONTAINER=ramble-box
echo saving image $DOCKER_IMAGE
docker save $DOCKER_IMAGE | ssh $RB_USER@$RB_HOST "cat > $DOCKER_IMAGE_FILE"

echo Stopping and deleting container $DOCKER_CONTAINER
ssh $RB_USER@$RB_HOST docker stop $DOCKER_CONTAINER

echo Loading $DOCKER_IMAGE_FILE
ssh $RB_USER@$RB_HOST docker load -i $DOCKER_IMAGE_FILE

ssh $RB_USER@$RB_HOST docker run -d \
  --name $DOCKER_CONTAINER \
  --rm \
  -p 8080:8080 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -e OPENAI_API_KEY=$OPENAI_API_KEY \
  -e GITHUB_TOKEN=$GITHUB_TOKEN \
  $DOCKER_IMAGE