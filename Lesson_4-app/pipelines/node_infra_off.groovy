pipeline {
    options {
        ansiColor('xterm')
    }

    agent any

    tools {
        terraform 'tf1.8'
    }

    environment {
        GIT_REPO_URL = 'git@github.com:chesnokov70/Lessons-nastya.git'
        CREDENTIALS_ID = 'ssh_github_access_key' // Replace with your credential ID in Jenkins 
        SSH_KEY_PATH_HOST = '/home/ches/.ssh/ssh_instance_key.pem'   // Path on host machine
        SSH_KEY_PATH_CONTAINER = '/var/jenkins_home/.ssh/ssh_instance_key.pem'  // Path inside container
    }

    stages {
        stage('Sparse Checkout') {
            steps {
                script {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: 'main']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [[
                            $class: 'SparseCheckoutPaths',
                            sparseCheckoutPaths: [[path: 'Lesson_4-app/']]
                        ]],
                        userRemoteConfigs: [[
                            url: env.GIT_REPO_URL,
                            credentialsId: env.CREDENTIALS_ID
                        ]]
                    ])
                }
            }
        }

        stage('Main Initialize') {
            steps {
                dir('Lesson_4-app/terraform') {
                    sh '''
                        terraform init
                    '''
                }
            }
        }
        stage('Destroy Plan') {
            steps {
                dir('Lesson_4-app/terraform') {
                    sh '''
                        terraform plan -destroy -out=destroy-tfplan
                    '''
                }
            }
        }
                stage('Destroy Apply') {
            steps {
                dir('Lesson_4-app/terraform') {
                    sh '''
                        terraform apply -input=false "destroy-tfplan"                
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}