# Automated Build, Test and push java app image to docker hub with Jenkins on EC2 Instance



## Project Setup

### Tools Needed
- EC2 instance on AWS with docker installed
- Jenkins container 
- GitHub account
- java app
- maven tool installed in jenkins
- Gitbash on your local host

## Installation and Configuration Guide
### 1. *Prepare GitHub Repository:*
   - Create or select a GitHub repository.
   - Apply the Git Flow model.
bash
#using git bash clone your repo


$git clone "your repo"

$git flow init


### 2. *pushing java code that you want to build which contain::*
   - java app code
   - groovy script that builds the artifact
   - Jenkinsfile

```
def gv

pipeline {
    agent any
    tools {
        maven 'maven'
    }
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("build jar") {
            steps {
                script {
                    gv.buildJar()
                }
            }
        }
        stage("build image") {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }   
}
```
and here is script.groovy file content

```
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

def deployApp() {
    echo 'deploying the application...'
} 

return this
```

### 3. *check your develop branch on github repo*

![image](https://github.com/Elghetani/jenkins/assets/61852267/2443fee5-dfde-4e21-91a0-e642fdb64174)


### 4. *Installing Jenkins container on EC2:*

On the EC2 Terminal

#### First, update your existing list of packages:

```
$ sudo apt update
```
#### Next, install a few prerequisite packages which let apt use packages over HTTPS:
```
$ sudo apt install apt-transport-https ca-certificates curl software-properties-common
```
#### Then add the GPG key for the official Docker repository to your system:
```
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```
#### Add the Docker repository to APT sources:
```
$ sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
```
#### This will also update our package database with the Docker packages from the newly added repo.

#### Make sure you are about to install from the Docker repo instead of the default Ubuntu repo:
```
$ apt-cache policy docker-ce
```
#### Finally, install Docker:
```
sudo apt install docker-ce
```
#### After the installation you going to run jenkins as a container with a composed port 8080 and persistent volume
```
$ docker run -p 8080:8080 -p 50000:50000 -d -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

### Note :
*Add Inbound rule in the security group of the EC2 instance:*
- Type: Custom TCP
- Port Range: 8080
- source: 0.0.0.0/0

### Accessing Jenkins:
- Go to web browser and write : http://(ec2-elastic-ip)>:8080
- After getting in to the jenkins container run the following command inside your container to get Jenkins password:
```
$ sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```
- Copy password to Jenkins tab and sign in


### 5. *Configure Jenkins:*
   - *Install necessary plugins in Jenkins such as Git and Pipeline.*
   - *Connect Jenkins to GitHub.*
        - Go to "Manage Jenkins" > "Manage Plugins" > "Available" and install "GitHub Integration Plugin".
     - Set up credentials in Jenkins for GitHub (username and password).
   - *Create a new pipeline job.*
     - Select "New Item", name your pipeline , and choose "Pipeline" as the type.
     - In the pipeline configuration, select "Pipeline script from SCM" and choose "Git" as the SCM.
     - Enter the repository URL and credentials.
     - Specify the branch to build .
     - In Build Triggers, Check *"GitHub hook trigger for GITScm polling"*.
     - Save

### 6. *Implement Webhooks for Continuous Integration:*
*Set up webhooks in GitHub to trigger Jenkins builds on push events.*
- In GitHub, go to your repository settings and select *"Webhooks"*.
- *Add a new webhook:*
   - Payload URL: http://<your-jenkins-url>:8080/github-webhook/
   - Content type: *application/json*
   - Select *"Just the push event"*.
   - Ensure the webhook is active
 ![image](https://github.com/Elghetani/jenkins/assets/61852267/3fbb0311-ab45-4585-bd57-d0126ff1c90a)

### *With the webhook, Jenkins will trigger a new build every time changes are pushed to the connected branch.*

### 7. *Testing and Validation:*
   - Push a change to the develop branch and verify Jenkins triggers a build.
   
- Check the Jenkins dashboard for build status and output.
![image](https://github.com/Elghetani/jenkins/assets/61852267/3ae11748-2f4e-4af4-96bb-e6956a1b3779)
