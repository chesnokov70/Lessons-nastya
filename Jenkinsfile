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
    TOKEN = credentials('hub_token')
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
         sh """ 
         docker login -u chesnokov70 -p $TOKEN
         docker build -t "${env.REGISTRY}:${env.BUILD_ID}" .
         docker push "${env.REGISTRY}:${env.BUILD_ID}"
         """
        }
      }
    }
    stage ('Deploy node-app') {
      steps {
        script {
         sh """ 
         export APP_IMG="${env.REGISTRY}:${env.BUILD_ID}"
         envsubst < docker-compose.tmpl | tee docker-compose.yaml
         docker compose up -d
         """
        }
      }
    }
  }    
} 
