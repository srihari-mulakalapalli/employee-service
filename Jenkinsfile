pipeline {
    agent any

    environment {
        IMAGE_NAME = "srihari25/employee-service"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Git Checkout') {
            steps {
                git branch: 'main',
                url: 'https://github.com/srihari-mulakalapalli/employee-service.git'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Scan') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh '''
                    mvn sonar:sonar \
                    -Dsonar.projectKey=employee-service
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh """
                docker build -t $IMAGE_NAME:$IMAGE_TAG .
                """
            }
        }

        stage('Trivy Scan') {
            steps {
                sh """
                trivy image $IMAGE_NAME:$IMAGE_TAG
                """
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin

                    docker push srihari25/employee-service:${BUILD_NUMBER}
                    '''
                }
            }
        }
      
        stage('Update GitOps Repo') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-creds',
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_TOKEN'
                )]) {
                    sh '''
                    rm -rf employee-service-gitops

                    git clone https://${GIT_USER}:${GIT_TOKEN}@github.com/srihari-mulakalapalli/employee-service-gitops.git

                    cd employee-service-gitops/k8s

                    sed -i "s#image: srihari25/employee-service:.*#image: srihari25/employee-service:${BUILD_NUMBER}#g" deployment.yaml

                    git config user.email "jenkins@example.com"
                    git config user.name "Jenkins"

                    git add deployment.yaml
                    git commit -m "Update image tag to ${BUILD_NUMBER}" || true
                    git push origin main
                    '''
                }
            }
        }
    }
}
