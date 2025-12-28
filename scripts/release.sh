DOCKER_IMAGE=ramble-box:latest-$(date +%Y-%m-%d_%H-%M-%S)
DOCKER_IMAGE_FILE=ramble-box.latest.tar
DOCKER_CONTAINER=ramble-box

SSH_COMMAND="ssh $RB_USER@$RB_HOST"

mvn -DskipTests clean package
docker build --tag $DOCKER_IMAGE .

echo Saving image $DOCKER_IMAGE
docker save $DOCKER_IMAGE | ssh $RB_USER@$RB_HOST "cat > $DOCKER_IMAGE_FILE"

echo Stopping and deleting container $DOCKER_CONTAINER
$SSH_COMMAND docker stop $DOCKER_CONTAINER

echo Loading $DOCKER_IMAGE_FILE
ssh $RB_USER@$RB_HOST docker load -i $DOCKER_IMAGE_FILE

$SSH_COMMAND docker run -d \
  --name $DOCKER_CONTAINER \
  --rm \
  -p 8080:8080 \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -e OPENAI_API_KEY=$OPENAI_API_KEY \
  -e GITHUB_TOKEN=$GITHUB_TOKEN \
  $DOCKER_IMAGE