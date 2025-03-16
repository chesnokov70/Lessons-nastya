def git_url = "git@github.com:chesnokov70/node-app.git"
pipeline {
  agent any
  parameters {
    gitParameter (name: 'revision', type: 'PT_BRANCH')
  }
  environment {
    REGISTRY = "chesnokov70/node-app"
    EC2_INSTANCE = '3.91.19.9'
    SSH_KEY = credentials('your-ssh-key')
  }
  stages {
    stage ('Clone repo') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: "${revision}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'ssh_github_access_key', url: "$git_url"]]])
      }
    }
    stage ('Build and push') {
      steps {
        script {
          def Image = docker.build("${env.REGISTRY}:${env.BUILD_ID}")
          docker.withRegistry('https://registry-1.docker.io', 'hub_token') {
              Image.push()
        }
        }
      }
    }

    stage('Deploy to EC2') {
      steps {
        script {
            // SSH into EC2 instance and deploy the app using Docker Compose
                sh """
                ssh -i ${SSH_KEY} ${EC2_INSTANCE} << EOF
                # Ensure Docker and Docker Compose are installed on the EC2 instance
                if ! which docker > /dev/null; then
                    echo "Docker not found, installing..."
                    sudo apt-get update -y
                    sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
                    curl -fsSL https://get.docker.com -o get-docker.sh
                    sudo sh get-docker.sh
                    sudo usermod -aG docker ubuntu
                fi

                if ! which docker-compose > /dev/null; then
                    echo "Docker Compose not found, installing..."
                    sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-\$(uname -s)-\$(uname -m)" -o /usr/local/bin/docker-compose
                    sudo chmod +x /usr/local/bin/docker-compose
                fi

                # Pull the latest code
                cd /path/to/your/app || exit 1  # Ensure this directory exists on EC2
                git pull origin main || exit 1

                # Build and deploy using Docker Compose
                docker-compose down || exit 1
                docker-compose up -d || exit 1

                echo "Deployment completed successfully"
                EOF
                """
        }
      }
  }

  post {
      always {
          cleanWs() // Clean workspace after each build
      }

      success {
          echo 'Deployment was successful!'
      }

      failure {
          echo 'There was a problem with the deployment.'
      }
  }

}
}
