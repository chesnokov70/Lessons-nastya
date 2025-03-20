def git_url = "git@github.com:chesnokov70/node-app.git"

pipeline {
    agent any
    parameters {
        gitParameter(name: 'revision', type: 'PT_BRANCH')
    }
    environment {
        REGISTRY = "chesnokov70/node-app"
        SSH_KEY = credentials('ssh_instance_key')
        TERRAFORM_DIR = 'terraform'
        EC2_HOST = '3.83.4.117'
    }
    stages {
        stage('Clone repo') {
            steps {
                checkout([$class: 'GitSCM',
                    branches: [[name: "${revision}"]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs: [[credentialsId: 'ssh_github_access_key', url: "$git_url"]]
                ])
            }
        }

        stage('Provision EC2 Instance') {
            steps {
                script {
                    sh """
                    export PATH=$PATH:/usr/local/bin
                    cd ${TERRAFORM_DIR}
                    terraform init -reconfigure
                    terraform apply -auto-approve
                    """

                    // Extract the instance IP dynamically
                    def ec2_ip = sh(script: 'terraform output -no-color -raw ec2_public_ip | xargs', returnStdout: true).trim()
                    echo "EC2 IP is: '${ec2_ip}'"

                    withEnv(["EC2_INSTANCE=${ec2_ip}"]) {
                      echo "EC2 IP is: '${env.EC2_INSTANCE}'"
                    }
                }
            }
        }

        

        stage('Clone Repo to EC2') {
            steps {
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: 'ssh_github_access_key', keyFileVariable: 'SSH_KEY')]) {
                        sh """
                        ssh -v -o StrictHostKeyChecking=no -i "\${SSH_KEY}" ubuntu@3.83.4.117 << 'EOF'
                        set -x
                        sudo apt update
                        sudo apt install -y git
                        GIT_SSH_COMMAND="ssh -i \${SSH_KEY}" git clone ${GIT_URL}
                        cd node-app
                        git checkout ${REVISION}
                        EOF
                        """
                    }
                }
            }
        }

    }
}
