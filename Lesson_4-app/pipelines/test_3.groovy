pipeline {
    agent any
    tools {
        terraform 'tf1.8'
    }
    options {
        ansiColor('xterm')
    }
    stages {
        stage('Clone Git repo Lesson_4-app') {
            steps {
                checkout([
                    $class: 'GitSCM', 
                    branches: [[name: 'main']], 
                    doGenerateSubmoduleConfigurations: false, 
                    extensions: [
                        [
                            $class: 'SparseCheckoutPaths',
                            sparseCheckoutPaths: [[path: 'Lesson_4-app']]
                        ]
                    ], 
                    userRemoteConfigs: [
                        [
                            url: 'git@github.com:chesnokov70/Lessons-nastya.git',
                            credentialsId: 'ssh_github_access_key' // please use your jenkins access to git
                        ]
                    ]
                ])
            }
        }
        stage ('Terraform init') {
            steps {
                sh '''
                cd ./Lesson_4-app/terraform/
                terraform init -reconfigure
                '''
            }
        }
        stage ('Terraform plan') {
            steps {
                sh '''
                cd ./Lesson_4-app/terraform/
                terraform plan -out terraform.tfplan
                '''
            }
        }
        stage('Apply') {
            steps {
                sh '''
                cd ./Lesson_4-app/terraform/
                terraform apply terraform.tfplan
                '''
            }
        }
        stage('Terraform output') {
            steps {
                sh '''
                cd ./Lesson_4-app/terraform/
                terraform output web-address_monitoring > ../ansible/hosts
                '''
            }
        }
        stage('Install Ansible') {
            steps {
                sh '''
                sudo apt-get update
                sudo apt-get install ansible -y
                sleep 60
                '''
            }
        }
        stage('Run Ansible ') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_instance_key', keyFileVariable: 'SSH_KEY_PATH', usernameVariable: 'SSH_USER')]) {
                    sh '''
                        cd ./Lesson_4-app/ansible/
                        ansible-playbook -i hosts install_node_app.yml -u $SSH_USER \
                        --private-key=$SSH_KEY_PATH \
                        -e ansible_ssh_common_args="-o StrictHostKeyChecking=no"
                    '''
                }


            }
        }
    }
}