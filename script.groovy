def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'sudo docker build -t elghetani/jenkins:jma-2.0 .'
        sh "sudo echo $PASS | docker login -u $USER --password-stdin"
        sh 'sudo docker push elghetani/jenkins:jma-2.0'
    }
} 

def deployApp() {
    echo 'deploying the application...'
} 

return this
