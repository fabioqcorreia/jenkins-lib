
/**
 * Executes Pipeline
 */
def executePipeline (String projectKey, String projectName, String excludedFiles, String projectVersion, 
                     String projectSources, String projectTestsSources, String projectCoverageXmlFile, 
                     String projectMainFile, String requirementsFile) {

    timeout(120) {

        stage('Git Checkout') {
                    
            gitCheckoutPhase(projectKey, projectName, excludedFiles, projectVersion, 
                            projectSources, projectTestsSources, projectCoverageXmlFile);
        
        }

        stage('Installing depedencies') {
            installDepedencies(requirementsFile);
        }

        stage('Pytest') {
                    
            pytestPhase(projectTestsSources);
        
        }

        stage('PyCoverage') {
                    
            pycoveragePhase(projectMainFile);
        
        }

        stage('SonarQube') {
                    
            sonarPhase();
        
        }

        stage('Git Tag (release)') {
                    
            gitTagPhase(projectVersion);
        
        }

    }
    
}

def gitCheckoutPhase(String projectKey, String projectName, String excludedFiles, String projectVersion, 
                     String projectSources, String projectTestsSources, String projectCoverageXmlFile){

    // // Checkout server
    checkout scm

    // Writing properties file
    writeSonarProperties ( projectKey, projectName, excludedFiles, projectVersion, 
                           projectSources, projectTestsSources, projectCoverageXmlFile );

}

def installDepedencies(String requirementsFile){

    sh "python3 -m pip install --user -r ${WORKSPACE}/${requirementsFile}"

}

def pytestPhase(String projectTestsSources){

   sh " python3 -m pytest ${WORKSPACE}/${projectTestsSources}"

}

def pycoveragePhase(String projectMainFile){

    sh "python3 -m coverage run --branch ${projectMainFile}"
    sh "python3 -m coverage xml"

}

def sonarPhase(){

    withSonarQubeEnv('sonarqube') {

      sh "cd ${WORKSPACE}; ${scannerHome}/bin/sonar-scanner "

    }

}

def gitTagPhase(String projectVersion){

    def tag = "${projectVersion}-${env.BUILD_NUMBER}"

    sh "cd ${WORKSPACE}; git tag ${tag}"

    sh "git push origin ${tag}"

}

/**
 * Creates a greeting method for a certain person.
 *
 * @param slackId Slack id: https://hooks.slack.com/services/[this part]
 * @param channelId Channel Id: "SOMEHASH/ANOTHERHASH"
 */
def sendMsgToSlack (String slackId, String channelToken) {
    
    try{

        def lastBuild = currentBuild.getPreviousBuild().result
        def msg
        def color
        def url = "${env.BUILD_URL}console"

        if ( lastBuild == "FAILURE" && currentBuild.result == "FAILURE" ){
            
            msg = "Branch **${env.BRANCH_NAME}** continua com problema\n Link: "+url
            color = "danger"

        }

        if ( lastBuild == "SUCCESS" && currentBuild.result == "FAILURE" ){

            msg = "Branch **${env.BRANCH_NAME}** com Problema\n Link: "+url
            color = "warning"

        }

        if ( lastBuild == "FAILURE" && currentBuild.result == "SUCCESS" ){

            msg = "Branch **${env.BRANCH_NAME}** voltou ao normal, uhuuu!!!"
            color = "good"

        }

        if ( !(lastBuild == "SUCCESS" && currentBuild.result == "SUCCESS") ){
            
            slackSend channel: '#', color: color, message: msg, teamDomain: 'https://hooks.slack.com/services/'+slackId, token: channelToken

        }

    } catch (exec){
        throw new NullPointerException("Oops, it was not possible to send message")
    }

}

/**
 * Writes the sonarproperties.
 * excludedFiles ex: "tests/**,utils/**" <- excluir todos os arquivos dentro das pastas tests e utils.
 * projectSources ex: "data/,services/" <- buscará todo conteúdo das pastas informadas.
 * projectTestsSources ex: "tests/"
 * projectCoverageXmlFile ex: "coverage.xml" <- arquivo presente na raiz do projeto, este arquivo é gerado na execução da etapa coverage xml -i
 */
def writeSonarProperties (String projectKey, String projectName, String excludedFiles, String projectVersion, 
                          String projectSources, String projectTestsSources, String projectCoverageXmlFile){

    File file = new File("${WORKSPACE}/sonar-project.properties")

    file.write("sonar.projectKey=${projectKey}\n" +
               "sonar.projectName=${projectName}\n" +
               "sonar.projectVersion=${projectVersion}\n" +
               "sonar.exclusions=${excludedFiles}\n" +
               "sonar.sources=${projectSources}\n" +
               "sonar.tests=${projectTestsSources}\n" +
               "sonar.python.coverage.reportPath=${projectCoverageXmlFile}")

}