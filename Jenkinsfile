#!groovy
properties(
    [[$class: 'GithubProjectProperty', projectUrlStr: 'https://github.com/hmcts/div-div-case-progression-service/'],
     pipelineTriggers([[$class: 'GitHubPushTrigger']])]
)

@Library(['Reform', 'Divorce'])
import uk.gov.hmcts.Packager
import uk.gov.hmcts.Versioner

def packager = new Packager(this, 'divorce')
def versioner = new Versioner(this)
def notificationChannel = '#div-dev'

buildNode {
  try {
    def version
    String divCaseProgressionRPMVersion

    stage('Checkout') {
      deleteDir()
      checkout scm
      env.CURRENT_SHA = gitSha()
    }

    onDevelop {
      stage('Develop Branch SNAPSHOT') {
        sh '''
          sed  -i '1,/parent/ s/<\\/version>/-SNAPSHOT<\\/version>/' pom.xml
        '''
      }
    }

    stage('Build') {
      sh "mvn clean compile"
    }

    stage('Test (Unit)') {
      sh 'mvn test'
    }

    stage('Jacoco Coverage Check') {
      sh 'mvn verify -DskipITs=true'
    }

    stage('Code Coverage (Sonar)') {
      onPR {
        sh "mvn sonar:sonar -Dsonar.host.url=$SONARQUBE_URL -Dsonar.analysis.mode=preview"
      }

      onDevelop {
        sh "mvn sonar:sonar -Dsonar.host.url=$SONARQUBE_URL"
      }

      onMaster {
        sh "mvn sonar:sonar -Dsonar.host.url=$SONARQUBE_URL"
      }
    }

    stage("Dependency check") {
      try {
        sh "mvn dependency-check:check"
      }
      finally {
        publishHTML(target: [
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : "target/",
                reportFiles          : 'dependency-check-report.html',
                reportName           : 'Dependency Check Security Test Report'
        ])
      }
    }

    stage('Package (Docker)') {
      onMaster {
        dockerImage imageName: 'divorce/div-case-progression-service'
      }
    }

    stage('Package (JAR)') {
      versioner.addJavaVersionInfo()
      sh "mvn clean package -DskipTests=true"
    }

    stage('Package (RPM)') {
      onDevelop {
        divCaseProgressionRPMVersion = packager.javaRPM(
          'div-case-progression-service',
          '$(ls target/div-case-progression-service*.jar)',
          'springboot',
          'src/main/resources/application.properties'
        )

        version = "{div_case_progression_service_buildnumber: ${divCaseProgressionRPMVersion} }"
        packager.publishJavaRPM('div-case-progression-service')
        deploy app: 'div-case-progression-service', version: divCaseProgressionRPMVersion, sha: env.CURRENT_SHA, env:'dev'
      }

      onMaster {
          divCaseProgressionRPMVersion = packager.javaRPM(
          'div-case-progression-service',
          '$(ls target/div-case-progression-service*.jar)',
          'springboot',
          'src/main/resources/application.properties'
        )

        version = "{div_case_progression_service_buildnumber: ${divCaseProgressionRPMVersion} }"
        packager.publishJavaRPM('div-case-progression-service')
        deploy app: 'div-case-progression-service', version: divCaseProgressionRPMVersion, sha: env.CURRENT_SHA
      }
    }

  } catch (err) {

    onMaster {
      slackSend(
              channel: notificationChannel,
              color: 'danger',
              message: "${env.JOB_NAME}:  <${env.BUILD_URL}console|Build ${env.BUILD_DISPLAY_NAME}> has FAILED")
    }

    /**
     * Refinement in the future to enable slack notification only to changelist user and DevOps.
     */
    onDevelop {
      slackSend(
              channel: notificationChannel,
              color: 'danger',
              message: "${env.JOB_NAME}:  <${env.BUILD_URL}console|Build ${env.BUILD_DISPLAY_NAME}> has FAILED")
    }
    throw err
  }
}
