pipeline {
  agent any
  stages {
    stage("Build") {
      steps {
        sh "./gradlew clean build"
      }
    }
    stage("Containerize") {
      steps {
        sh "docker build --build-arg JAR_FILE=build/libs/*.jar -t kjarosz/webledgerbackend ."
      }
    }
  }
}
