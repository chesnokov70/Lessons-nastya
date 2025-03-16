pipeline {
    agent any 
    parameters {
      choice(name: 'playbook', choices: ["nginx.yaml", "clean.yaml"])
      string(name: 'host', defaultValue: 'iP', trim: true)
      gitParameter(name: 'branch', type: 'PT_BRANCH', sortMode: 'DESCENDING_SMART', selectedValue: 'NONE', quickFilterEnabled: true)
      booleanParam(name: 'dryrun', defaultValue: true)
    }
    stages {
      stage('Checkout repo') {
        steps {
          checkout([$class: 'GitSCM', branches: [[name: "${branch}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'jenkins_key', url: "$git_url"]]])
        }
      }
      
      stage('DryRun') {
        when {
          expression { params.dryrun }
        }
        steps {
          sh """
            export ANSIBLE_HOST_KEY_CHECKING=False
            ansible-playbook -i '$params.host,' $params.playbook --check
          """
        }
      }

      stage('Apply') {
        when {
          expression { !params.dryrun }
        }
        steps {
          sh """
            export ANSIBLE_HOST_KEY_CHECKING=False
            ansible-playbook -i '$params.host,' $params.playbook
          """
        }
      }
    }
}