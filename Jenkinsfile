def git_url = "git@github.com:chesnokov70/node-app.git"
pipeline {
  agent any
  parameters {
    gitParameter (name: 'revision', type: 'PT_BRANCH')
  }
  environment {
    REGISTRY = "chesnokov70/node-app"
  }
  stages {
    stage ('Clone repo') {
      steps {
        checkout([$class: 'GitSCM', branches: [[name: "${revision}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jenkins_ssh_key', url: "$git_url"]]])
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
  }
}
