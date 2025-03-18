def git_url = "git@github.com:chesnokov70/node-app.git"
pipeline {
  agent any
  parameters {
    gitParameter (name: 'revision', type: 'PT_BRANCH')
  }
  environment {
    REGISTRY = "chesnokov70/node-app"
    EC2_INSTANCE = '3.228.218.199'
    SSH_KEY = credentials('ssh_instance_key')
    TERRAFORM_DIR = 'terraform/ec2-instance'    
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
          cd ${TERRAFORM_DIR}
          terraform init
          terraform apply -auto-approve
          """

          // Extract the instance IP dynamically
          def ec2_ip = sh(script: "terraform output -raw ec2_public_ip", returnStdout: true).trim()
          env.EC2_INSTANCE = ec2_ip
        }
      }
    }

    stage ('Deploy Node.js App with Docker Compose') {
      steps {
        script {
          sh """
          ssh -o StrictHostKeyChecking=no -i ${env.SSH_KEY} ubuntu@${env.EC2_INSTANCE} << EOF
          sudo apt update && sudo apt install -y docker docker-compose
          docker --version
          docker-compose --version

          # Clone repo and run docker-compose
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