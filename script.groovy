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
    try {
    sh "exit 1"
    } 
    catch (Exception e) {
        echo "Test failed, marking build as unstable"
        currentBuild.result = 'UNSTABLE'
        stageResult = 'FAILURE'
        return
                    }
}
def deployApp() {
    echo 'deploying the application...'
} 

return this

