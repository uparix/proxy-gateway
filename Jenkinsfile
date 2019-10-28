pipeline {
    agent any
    stages {
        stage ('Checkout') {
            steps {
                git branch:'master', url: 'https://github.com/uparix/proxy-gateway'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests -Puparix-releases clean deploy'
            }
        }
    }
}
