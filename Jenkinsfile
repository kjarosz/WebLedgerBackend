pipeline {
  agent any
  stages {
    stage("Build") {
      steps {
        sh "./gradlew clean build"
      }
    }
    stage("Build Image") {
      steps {
        sh "docker build --build-arg JAR_FILE=build/libs/*.jar -t kjarosz/webledgerbackend ."
      }
    }
    stage("Start Container") {
      steps {
        sh "docker run --network=host --rm -d kjarosz/webledgerbackend"
      }
    }
  }
}
