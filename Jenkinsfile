pipeline {
    agent any
    tools {
        maven 'Maven 3.6.3'
    }
    stages {
        stage ('Checkout') {
            steps {
                git branch:'master', url: 'git@github.com:uparix/proxy-gateway.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests -P uparix-releases -X clean compile'
            }
        }
    }
}
