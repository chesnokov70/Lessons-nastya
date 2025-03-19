def git_url = "git@github.com:chesnokov70/node-app.git"
pipeline {
  agent any
  parameters {
    gitParameter (name: 'revision', type: 'PT_BRANCH')
  }
  environment {
    REGISTRY = "chesnokov70/node-app"
    SSH_KEY = credentials('ssh_instance_key')
    TERRAFORM_DIR = 'terraform'    
  }
  stages {
    stage ('Clone repo') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: "${revision}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'ssh_github_access_key', url: "$git_url"]]])
      }
    }

    stage ('Provision EC2 Instance') {
      steps {
        script {
          sh """
          export PATH=$PATH:/usr/local/bin
          cd ${TERRAFORM_DIR}
          terraform init -reconfigure
          terraform apply -auto-approve
          """

          // Extract the instance IP dynamically
          def ec2_ip = sh(script: "terraform output -raw ec2_public_ip", returnStdout: true).trim()
          echo "EC2 IP is: ${ec2_ip}"
          env.EC2_INSTANCE = ec2_ip
        }
      }
    }

    stage ('Deploy Node.js App with Docker Compose') {
      steps {
        withCredentials([sshUserPrivateKey(credentialsId: 'ssh_instance_key', keyFileVariable: 'SSH_KEY')]) {
          script {
            sh """
            ssh -o StrictHostKeyChecking=no -i ${env.SSH_KEY} ubuntu@${env.EC2_INSTANCE} << EOF
            sudo apt update && sudo apt install -y docker docker-compose
            docker --version
            docker-compose --version

            git clone ${git_url} || (cd node-app && git pull)
            cd node-app
            docker-compose up -d
            EOF
            """
          }
        }
      }
    }

  }
}