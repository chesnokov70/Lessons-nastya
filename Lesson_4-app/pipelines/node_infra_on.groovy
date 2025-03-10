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
                        ], [
                            $class: 'SparseCheckoutType', sparseCheckoutType: 'sparse-checkout'
                        ]],
                        userRemoteConfigs: [[
                            url: env.GIT_REPO_URL,
                            credentialsId: env.CREDENTIALS_ID
                        ]]
                    ])

                }
            }
        }

        stage('Install Ansible') {
            steps {
                sh '''
                if ! command -v ansible &> /dev/null; then
                  apt-get update && apt-get install -y ansible
                fi
                if ! command -v aws &> /dev/null; then
                  apt-get install awscli -y
                fi
                 '''
            }
        }
        stage('Main Initialize and Plan') {
            steps {
                dir('Lesson_4-app/terraform') {
                    sh '''
                        terraform init
                        terraform plan -out=tfplan
                    '''
                }
            }
        }

        stage('Main Apply Infrastructure') {
            steps {
                dir('Lesson_4-app/terraform') {
                    sh '''
                        terraform apply -input=false "tfplan"
                    '''
                }
            }
        }
        stage('Setup Control Plane Ansible') {
            steps {
                dir('Lesson_4-app/ansible/') {
                    sh '''
                    ansible-playbook install_node_app.yml
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