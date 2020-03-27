./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t kjarosz/webledgerbackend .

docker run \
  --name webledger
  --network=webledger \
  --network-alias=webledgerbackend \
  -p 8080:8080 \
  -e "SPRING_PROFILES_ACTIVE=prod" \
  --rm \
  -d \
  kjarosz/webledgerbackend
