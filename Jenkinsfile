try {
node {
    def app
    stage('Clone Repository')
    {
        final scmVars = checkout(scm)
        env.BRANCH_NAME = scmVars.GIT_BRANCH
        env.SHORT_COMMIT = "${scmVars.GIT_COMMIT[0..7]}"
        env.GIT_REPO_NAME = scmVars.GIT_URL.replaceFirst(/^.*\/([^\/]+?).git$/, '$1')
    }
     stage('Run Java Unit Tests') {
        withMaven(maven: 'M3') {
            /// Run the maven build
            withEnv(["JAVA_HOME=${env.JAVA_11}"]){
                sh "mvn -Dmaven.test.failure.ignore=true -Dserver.port=8090 clean package"
            }
        }
    }


    stage('SonarQube code analysis') {
        withMaven(maven: 'M3') {
            withSonarQubeEnv('SonarQube') {
                withEnv(["JAVA_HOME=${env.JAVA_11}"]) {
                    sh "mvn sonar:sonar"
                }
            }
        }
    }
    if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master') {
        try {
            stage('Veracode SCA scan') {
            // 3rd party scan application
                withEnv(['https_proxy=http://proxy3:8080', 'scan_collectors=maven', 'compile_first=false', 'install_first=false']){
                    withMaven(maven: 'M3') {
                        withCredentials([string(credentialsId: 'SRCCLR_API_TOKEN', variable: 'SRCCLR_API_TOKEN')]) {
                            sh 'curl -sSL https://download.sourceclear.com/ci.sh | sh'
                        }
                    }
                }
            }
        } catch(Error|Exception e) {
            echo 'failed but we continue'
        }
    }
    if (env.BRANCH_NAME == 'develop') {
        stage('Veracode SAST scan') {
            //pipeline scan
            try {
                withEnv(["https_proxy=http://proxy3:8080"]){
                    withCredentials([usernamePassword(credentialsId: 'veracode-cred', usernameVariable: 'VERACODE_API_ID', passwordVariable: 'VERACODE_API_KEY')]) {
                        sh 'curl -O https://downloads.veracode.com/securityscan/pipeline-scan-LATEST.zip'
                        sh 'unzip -o pipeline-scan-LATEST.zip pipeline-scan.jar'
                        sh '''java -Djava.net.useSystemProxies=true -jar pipeline-scan.jar -vid "$VERACODE_API_ID" -vkey "$VERACODE_API_KEY" --file target/**.jar'''
                    }
                }
             } catch(Error|Exception e) {
                echo "failed but we continue"
            }
        }
    } else if(env.BRANCH_NAME == 'master') {
        stage('Veracode Policy scan') {
            // Policy scan
            withCredentials([usernamePassword(credentialsId: 'veracode-cred', usernameVariable: 'VERACODE_API_ID', passwordVariable: 'VERACODE_API_KEY')]) {
                veracode applicationName: 'safaricom loyalty microservices', criticality: 'VeryHigh',
                fileNamePattern: '', pHost: 'proxy3.safaricom.net',
                pPassword: '', pPort: '8080', pUser: '',
                replacementPattern: '', scanExcludesPattern: '', scanIncludesPattern: '',
                scanName: '$buildnumber - $timestamp', teams: 'DevSecOps', timeout: 5,
                uploadExcludesPattern: '', uploadIncludesPattern: 'target/**.jar', waitForScan: true,
                useProxy: true, vid: "${VERACODE_API_ID}", vkey: "${VERACODE_API_KEY}"
            }
        }
    }
    stage('Build Docker Image') {
        app = docker.build("${env.GIT_REPO_NAME}")
    }
    if (env.BRANCH_NAME == 'develop' || env.BRANCH_NAME == 'master') {
        stage('Veracode Docker Security Scan') {
            try {
                // 3rd party scan docker container
                withEnv(["https_proxy=http://proxy3:8080"]){
                    withCredentials([string(credentialsId: 'SRCCLR_API_TOKEN', variable: 'SRCCLR_API_TOKEN')]) {
                        sh "curl -sSL https://download.sourceclear.com/ci.sh | sh -s scan --image ${env.GIT_REPO_NAME}"
                    }
                }
            } catch(Error|Exception e) {
                echo "failed but we continue"
            }
        }
    }
    /* Finally, we'll push the image:
    * We tag the image with the incremental build number from Jenkins
    * Pushing multiple tags is cheap, as all the layers are reused.*/
   if  (env.BRANCH_NAME == 'develop') {
       stage('Push Image to aws Uat Registry') {
            retry(3) {
                docker.withRegistry('https://559104660845.dkr.ecr.eu-west-1.amazonaws.com/', 'ecr:eu-west-1:awsecr-uat') {
                    app.push("uat-${env.SHORT_COMMIT}")
                    app.push("latest")
                }
            }
        }
    } else if (env.BRANCH_NAME == 'master') {
        stage('Push Image to aws Registry') {
            retry(3) {
                env.VERSION = version()
                docker.withRegistry('https://385328525783.dkr.ecr.eu-west-1.amazonaws.com/', "ecr:eu-west-1:awsecr-prod") {
                    app.push("v${env.Version}_${env.SHORT_COMMIT}")
                    app.push("latest")
                }
            }
        }
    }
}
} catch(Error|Exception e) {
  //Finish failing the build after telling someone about it
  throw e
} finally {
    // Post build steps here
    /* Success or failure, always run post build steps */
    // send email
    // publish test results etc
}
def version()
{
    pom = readMavenPom file: 'pom.xml'
    return pom.version
}
