def remote = [:]
pipeline {
    agent any

    environment {
        GIT_REPO_URL = 'git@github.com:chesnokov70/Lessons-nastya.git'
        CREDENTIALS_ID = 'ssh_github_access_key'        
        HOST = "172.31.42.20"
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
                git (url: 'https://github.com/chesnokov70/Lessons-nastya.git', branch: 'main')
            }
        }

    }
}