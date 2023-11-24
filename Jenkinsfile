pipeline {

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
        stage('Test') {
            steps {
                sh 'echo : not implemented yet'
            }
        }

        // Security Scan  - SAST
        // Security scan - SCA


        stage('Build Image') {
            steps {
                sh 'echo : not implemented yet'
            }
        }
        //TODO add a security scan for the image Trivy
        stage('Scan Docker Image') {
            steps {
                sh 'echo : not implemented yet'
            }
        }

        stage('Push Image') {
            steps {
                sh 'echo : not implemented yet'
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
