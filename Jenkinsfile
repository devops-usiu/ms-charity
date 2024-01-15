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
            GIT_COMMIT = getCommit()
            TIMESTAMP = BUILDVERSION()
    }
    stages {

        // stage('Clone Repository'){
        //     steps{//PAT needed
        //         git 'https://github.com/Cnnb01/${GIT_REPO_NAME}.git'
        //     }
        // commm}

        stage('Build') {
            steps {
                 sh 'mvn -B -DskipTests clean package'
            }
        }

        //Performs a sonarscan on the docker image
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
                sh 'docker build -t ${GIT_REPO_NAME}:v1-${GIT_COMMIT}-${TIMESTAMP} .'//container_name:version
            }
        }

        //TODO add a security scan for the image Trivy
        //Trivy must be installed as a Jenkins tool
        // stage('Security scan for the image'){
        //     steps{
        //         sh 'trivy --exit-code 0 --severity HIGH ${GIT_REPO_NAME}:v1-${GIT_COMMIT}-${TIMESTAMP}'
        //     }
        // }

        //<have to setup credentials first under GUI>
        stage('Push Image') {
            steps {
                script {
                    // define your Docker Hub credentials in the GUI as well
                    def dockerHubUser = credentials('cnnb01')
                    def dockerHubPassword = credentials('chacha011')
                    // For logging into Docker Hub
                    sh 'docker login -u ${dockerHubUser} -p ${dockerHubPassword}'
                    // Push the Docker image to Docker Hub
                    sh 'docker push ${GIT_REPO_NAME}:v1-${GIT_COMMIT}-${TIMESTAMP}' 
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
