def git_url = "git@github.com:chesnokov70/node-app.git"
pipeline {
  agent any
  parameters {
    gitParameter (name: 'revision', type: 'PT_BRANCH')
  }
  environment {
    REGISTRY = "chesnokov70/node-app"
    EC2_HOST = '3.83.4.117'
    SSH_KEY = credentials('ssh_instance_key')
  }
  stages {
    stage ('Clone repo') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: "${revision}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'ssh_github_access_key', url: "$git_url"]]])
      }
    }
    stage('Deploy to EC2') {
        steps {
            withCredentials([sshUserPrivateKey(credentialsId: 'ssh_instance_key', keyFileVariable: 'SSH_KEY')]) {
                sh """
                ssh -o StrictHostKeyChecking=no -i $SSH_KEY ubuntu@3.228.218.199 << 'EOF'
            ssh-keyscan github.com >> ~/.ssh/known_hosts
            if [ ! -d "node-app" ]; then
                git clone git@github.com:chesnokov70/node-app.git
            else
                cd node-app
                git pull origin main
            fi
            sudo apt update
            sudo apt install -y docker.io
        
            if ! command -v docker-compose &> /dev/null; then
                sudo curl -SL https://github.com/docker/compose/releases/download/v2.23.3/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
                sudo chmod +x /usr/local/bin/docker-compose
            fi

            docker-compose up -d
            EOF
                """
            }
        }
    }    
  }    
    //stage ('Build and push') {
    //  steps {
    //    script {
    //      def Image = docker.build("${env.REGISTRY}:${env.BUILD_ID}")
    //      docker.withRegistry('https://registry-1.docker.io', 'hub_token') {
    //          Image.push()
    //    }
    //    }
    //  }
    //}


}
