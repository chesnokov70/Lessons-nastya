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
        stage('Verify hosts file') {
            steps {
                script {
                    def hostsFilePath = "/var/jenkins_home/workspace/Lessons-Nastya/ansible/hosts"
                    if (fileExists(hostsFilePath)) {
                        echo "File exists: ${hostsFilePath}"
                    } else {
                        error "File does not exist: ${hostsFilePath}"
                    }
                }
            }
        }  
        stage('Read EC2 IP from Terraform Output') {
            steps {
                script {
                    // Read the IP address or other data from the output file
                    def hostsFilePath = "${workspace}/ansible/hosts"
                    def hostsFileContent = readFile(hostsFilePath)
                    echo "Hosts file content: ${hostsFileContent}"
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