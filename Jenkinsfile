pipeline {
    agent any
    stages {
        stage ('Checkout') {
            steps {
                git branch:'master', url: 'git@github.com:uparix/proxy-gateway.git'
            }
        }
        stage('Build') {
            steps {
                sh '/root/.sdkman/candidates/maven/current/bin/mvn -B -DskipTests -P uparix-releases -X clean install'
            }
        }
    }
}
