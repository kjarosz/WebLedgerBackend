./gradlew clean build
docker build --build-arg JAR_FILE=build/libs/*.jar -t kjarosz/webledgerbackend .
docker run --network=host --rm -d kjarosz/webledgerbackend
