def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
        sh ' docker build -t elghetani/jenkins:jma-2.0 .'
        sh " echo $PASSWORD | docker login -u $USERNAME --password-stdin"
        sh ' docker push elghetani/jenkins:jma-2.0'
    }
} 
def testApp() {
    echo 'perform testing'
    try {
    eco 'iam a mistake'
    }
    catch {
        echo "GREAT!"
    }
}
def deployApp() {
    echo 'deploying the application...'
} 

return this
