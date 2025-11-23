pipeline {
  agent any

  environment {
    MVN_CMD = './mvnw'
    MAVEN_OPTS = '-Xmx1g'
  }

  options {
    timestamps()
    ansiColor('xterm')
    timeout(time: 60, unit: 'MINUTES')
    buildDiscarder(logRotator(numToKeepStr: '25'))
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'chmod +x mvnw'
      }
    }

    stage('Build (compile)') {
      steps {
        sh "${MVN_CMD} -B -DskipTests=true clean package"
      }
    }

    stage('Unit Tests') {
      steps {
        sh "${MVN_CMD} -B test"
      }
    }

    stage('JaCoCo Report') {
      steps {
        sh "${MVN_CMD} -B jacoco:report"
      }
    }

    stage('SonarQube Analysis') {
      steps {
        sh "${MVN_CMD} -B sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=sqa_77975c42e18c17a361402141babf6f69818d3911"
      }
    }

    stage('Package') {
      steps {
        sh "${MVN_CMD} -B -DskipTests=true package"
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/*.jar, target/site/jacoco/**', allowEmptyArchive: true
      cleanWs()
    }

    success {
      echo "Build succeeded: ${env.BUILD_URL}"
    }

    failure {
      echo "Build failed: ${env.BUILD_URL}"
    }
  }
}