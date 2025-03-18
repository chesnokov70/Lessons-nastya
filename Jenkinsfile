def remote = [:]
pipeline {
    agent any

    environment {
        REPO = "chesnokov/node-app"
        DOCKER_IMAGE = 'node-app'
        DOCKER_TAG = 'latest'
        HOST = "3.238.15.68"
        SVC = "zansulu"
        PORT = "3000"
    }

    stages {
        stage('Configure credentials') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_github_access_key', keyFileVariable: 'private_key', usernameVariable: 'username')]) {
                    script {
                        remote.name = "${env.HOST}"
                        remote.host = "${env.HOST}"
                        remote.user = "$username"
                        remote.identity = readFile("$private_key")
                        remote.allowAnyHosts = true
                    }
                }
            }
        }

        stage('Clone Repository') {
            steps {
                git (url: 'https://github.com/chesnokov70/node-app', branch: 'main')
            }
        }
    }
}