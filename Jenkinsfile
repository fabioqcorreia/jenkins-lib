@Library('jenkins-lib@master')
import Commands;

String projectKey = "typification-documents"
String projectName = "Typification documents"
String excludedFiles = "app/tests"
String projectVersion = "1.0"
String projectSources = "app/services, app/excepts, app/settings, app/views"
String projectTestsSources = "app/tests"
String projectCoverageXmlFile = "coverage.xml"
String projectMainFile = "main.py"
String requirementsFile = "requirements-dev.txt"

def cmd = new Commands();

/**
 * Customizar parâmetros para o projeto aqui.
 * 
 * @param: String projectKey <- Chave identificadora do projeto ex: typification-documents
 * @param: String projectName <- Nome qualificado do projeto ex: Typification documents
 * @param: String excludedFiles <- Arquivos para exclusão na execução do SonarQube
 * @param: String projectVersion <- Versão do projeto ex: 1.0
 * @param: String projectSources <- pasta(s) que contém o(s) código(s) executável(eis) ex: app/utils
 * @param: String projectTestsSources <- pasta(s) que contém o(s) código(s) de teste(s) ex: app/tests
 * @param: String projectCoverageXmlFile <- caminho do arquivo covarage.xml que será gerado ex: coverage.xml
 * @param: String projectMainFile <- caminho do arquivo de execução principal ex: main.py
 * @param: String requirementsFile <- caminho do arquivo de requirements.txt
 */

cmd.executePipeline( projectKey, projectName, excludedFiles, 
                     projectVersion, projectSources, projectTestsSources,
                     projectCoverageXmlFile, projectMainFile, requirementsFile);
