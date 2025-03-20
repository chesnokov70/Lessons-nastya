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

                    def ec2_ip = sh(script: """
                         terraform output -no-color -raw ec2_public_ip | xargs
                         echo "EC2 IP is: ${ec2_ip}"
                    """, returnStdout: true).trim()

                    if (!ec2_ip || !ec2_ip.matches('\\d+\\.\\d+\\.\\d+\\.\\d+')) {
                        error("Failed to retrieve valid EC2 IP. Check Terraform state or instance creation.")
                    }

                    env.EC2_INSTANCE = ec2_ip
                }
            }
        }

        stage('Clone Repo to EC2') {
            steps {
                script {
                    sh """
                    ssh -o StrictHostKeyChecking=no -i "\${SSH_KEY}" ubuntu@${env.EC2_INSTANCE} << 'EOF'
                    sudo apt update
                    sudo apt install -y git
                    if [ ! -d "node-app" ]; then
                        git clone ${git_url}
                    else
                        cd node-app && git pull origin ${revision}
                    fi
                    cd node-app
                    git checkout ${revision}
                    EOF
                    """
                }
            }
        }
    }
}
