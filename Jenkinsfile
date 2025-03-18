def git_url = "git@github.com:chesnokov70/node-app.git"
pipeline {
    agent any

    environment {
        REPO = "chesnokov70/node-app"
        DOCKER_IMAGE = 'chesnokov70/node-app'
        DOCKER_TAG = '1.0'
        HOST = "3.238.15.68"
        SVC = "zansulu"
        PORT = "3000"
    }

    stages {
        stage('Configure credentials') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_github_access_key', keyFileVariable: 'private_key', usernameVariable: 'username')]) {
                    script {
                        def remote = [
                            name: "${env.HOST}",
                            host: "${env.HOST}",
                            user: "$username",
                            identity: readFile("$private_key"),
                            allowAnyHosts: true
                        ]
                        echo "Remote host configured: ${remote.host}"
                    }
                }
            }
        }

        stage('Clone Repository') {
            steps {
                git (url: 'https://github.com/chesnokov70/node-app', 
                branch: 'main',
                credentialsId: 'ssh_github_access_key')
            }
        }
    }
}