pipeline{
    agent any
    tools{
        maven ''
    }
    stages{
        statge('Build')
        steps{
            sh 'mvn clean package'
        }
        post{
            success{
                echo"archiving the artifacts"
                archiveArtufacts artifacts:'**/target/*.war'
            }
        }
    
    }
    statge('Deploy to container'){
        steps{
            deploy adapters: [tomcat8(credentialsId: '1093853d-aec3-42ca-b7a8-0c46b7949056', path: '', url: 'http://34.201.110.218:800/')], contextPath: null, war: '**/*.war'
        }
    }






}