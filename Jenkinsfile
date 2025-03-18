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
  }
  stages {
    stage ('Clone repo') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: "${revision}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'ssh_github_access_key', url: "$git_url"]]])
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

    stage('Deploy to EC2') {
      steps {
        script {
            // SSH into EC2 instance and deploy the app using Docker Compose
              
        }
      }
    }
  }
//  post {
//      always {
//          cleanWs() // Clean workspace after each build
//      }

//      success {
//          echo 'Deployment was successful!'
//      }

//      failure {
//          echo 'There was a problem with the deployment.'
//      }
//  }

}
