def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
     try {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh ' docker build -t elghetani/jenkins:jma-2.0 .'
        sh " echo $PASSWORD | docker login -u $USERNAME --password-stdin"
        sh ' docker push elghetani/jenkins:jma-2.0'
    }
     }
    catch (err) {                                        
                unstable(message: "${STAGE_NAME} is unstable")
            }
} 

def deployApp() {
    echo 'deploying the application...'
} 

return this
