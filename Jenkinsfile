pipeline {
    agent any

    // No tools block needed: Jenkins image has Java 17 built-in
    // We use Maven wrapper (mvnw) from the project repository

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'chmod +x ./mvnw && ./mvnw clean test'
            }
        }

        stage('Code Coverage Check (JaCoCo)') {
            steps {
                script {
                    try {
                        sh 'chmod +x ./mvnw && ./mvnw verify'
                    } catch (Exception e) {
                        echo "⚠️ Code coverage is below 40% threshold!"
                        currentBuild.result = 'FAILURE'
                        error("Build failed: Code coverage < 40%")
                    }
                }
            }
        }

        stage('Package') {
            steps {
                sh 'chmod +x ./mvnw && ./mvnw package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'chmod +x ./mvnw && ./mvnw sonar:sonar -Dsonar.projectKey=FlowLink'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
        failure {
            echo "❌ Pipeline FAILED - Check coverage or build errors above"
        }
        success {
            echo "✅ Pipeline SUCCESS - All checks passed!"
        }
    }
}
