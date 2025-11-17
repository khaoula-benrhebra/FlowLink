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

        stage('Build') {
            steps {
                sh 'chmod +x ./mvnw && ./mvnw clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'chmod +x ./mvnw && ./mvnw test'
            }
        }

        stage('Package') {
            steps {
                sh 'chmod +x ./mvnw && ./mvnw package'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }
    }

    //il faut ajouter 2 analyse jacococ et sonarqube

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
    }
}
