pipeline {

    def app

    agent any
    //disable concurrent build to avoid race conditions and to save resources
    options {
        disableConcurrentBuilds()
    }

    //load tools - these should be configured in jenkins global tool configuration
     tools {
        maven 'M3'
        jdk 'Java-17'
    }
    environment {
            GIT_REPO_NAME = determineRepoName()
    }
    stages {


        stage('Build') {
            steps {
                 sh 'mvn -B -DskipTests clean package'
            }
        }


        //TODO add a security scan for the image Trivy
        stage('Scan Docker Image') {
            steps {
                withSonarQubeEnv(installationName: 'sonar'){
                    sh 'mvn sonar:sonar'
                }

            }
        }


        // Security Scan  - SAST
        // Security scan - SCA


        stage('Build Image') {
            steps {


                sh 'docker build -t ms-devsecops-wit:dev-001 .'
                app = docker.build(owner + "/${image}","-f ./${dockerfile} .");
            }
        }


        stage('Docker Push') {
              steps {
                docker.withRegistry('https://hub.docker.com/', 'onekoech-docker-hub-credentials') {
                app.push("prod-${env.SHORT_COMMIT}-${env.BUILDVERSION}")
                app.push("latest")

            }
              }
            }
    }
}

String determineRepoName() {
    return   GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')
}

String getCommit(){
   return GIT_COMMIT[0..7]
}


def BUILDVERSION(){
    timestamp=Calendar.getInstance().getTime().format('YYYYMMddHHmmss',TimeZone.getTimeZone('EAT'))
    return timestamp
}

def version(){
    pom = readMavenPom file: 'pom.xml'
    return pom.version
}
