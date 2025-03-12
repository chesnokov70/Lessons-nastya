pipeline {
    agent any

    environment {
        AWS_ACCESS_KEY_ID = credentials('ssh_instance_key') // Use Jenkins' credentials store for AWS keys
        GIT_REPO_URL = 'git@github.com:chesnokov70/Lessons-nastya.git' // GitHub repo URL
        CREDENTIALS_ID = 'ssh_github_access_key' // Jenkins SSH credentials ID for GitHub
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

        stage('Provision EC2 Instance') {
            steps {
                dir('./Lesson_4-app/terraform') {
                    sh '''
                        terraform init
                        terraform plan -out=tfplan
                    '''
                }
            }
        }
        stage('Read EC2 IP from Terraform Output') {
            steps {
                script {
                    // Assuming Terraform writes the public IP to a file, read the IP address
                    def ec2_ip = readFile('../ansible/hosts').trim()
                    // Set the IP as an environment variable to be used later in the pipeline
                    env.HOST = ec2_ip
                }
            }
        }
        stage('Configure Credentials for Ansible') {
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

        stage('Clone GitHub Repository and Run App') {
            steps {
                script {
                    // Use Ansible to configure the EC2 instance, clone the repo, and run the app
                    sh '''
                        ansible-playbook -i $HOST, -u deployer --private-key $private_key install_node_app.yml
                    '''
                }
            }
        }

    }

    post {
        always {
            // Clean up Terraform resources after pipeline execution
            sh 'terraform destroy -auto-approve'
        }
    }
}
