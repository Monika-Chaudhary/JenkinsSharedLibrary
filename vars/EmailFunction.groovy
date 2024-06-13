def call(To, CC, Subject, appName, stage_step, build_status, env, buildArtifactName=' '){
  echo "${build_status}"

  def sonarLink=""

  def EmailRecipient=""

  if("${appName}" == "appName"){
    sonarLink="""https://sonarUrl/dashboard?id=${appName}"""
  }else{
    echo "Provide App Name or set differentSonarURL"
  }

  def emailcontent=""

  def emailcontentStart="""<br>Dear All,<br>
  <br>${appName} application has been failed at '${stage_step}' stage with build no ${BUILD_NUMBER}.</br>"""

  def emailcontentEnd="""<br><u><b>Build Artifact Name : </b></u>${buildArtifactName}</br>
  <br><u><b>Jenkins Build Log : </b></u></br>
  <br>${BUILD_URL}console</br>

  <br><br>For more details, please contact team.</br></br>

  <br>Thanks & Regards,</br>
  Jenkins Admin"""

  def emailcontentPass="""<br>Dear All,</br>
  <br>${appName} application has been built successfully with build no ${BUILD_NUMBER}.</br>

  <br>Below is build report for reference:</br>

  <br><u><b>Quality Review:</b></u></br>
  <br>${sonarLink}</br>

  <br><u><b>Unit Testing:</b></u></br>
  <br>${BUILD_URL}testReport</br>

  <br><u><b>Code Coverage:</b></u></br>
  <br>${BUILD_URL}coverage</br>
  """

  if("${build_status}" == "FAILURE"){
    if("${stage_step}" == "Checkout SCM"){
      echo "Testing SCM"
      emailcontent=emailcontentstart + emailcontentEnd
    }
    if("${stage_step}" == "Compile and Build EAR"){
      echo "Testing Compile"
      emailcontent=emailcontentstart + emailcontentEnd
    }
    if("${stage_step}" == "SAST Analysis with SonarQube"){
      echo "Testing SAST"
      emailcontent=emailcontentstart + "<br><u><b>Quality Review:</b></u></br><br>${sonarLink}</br>" + emailcontentEnd
    }
    if("${stage_step}" == "JUnit Testing"){
      echo "Testing JUnit"
      emailcontent=emailcontentstart + "<br><u><b>Unit Testing:</b></u></br><br>${BUILD_URL}testReport</br><br><u><b>Code Coverage Report Link:</b></u></br><br>${BUILD_URL}coverage</br>" + emailcontentEnd
    }
    if("${stage_step}" == "Nexus upload of Ear artifact"){
      echo "Testing Nexus"
      emailcontent=emailcontentstart + emailcontentEnd
    }
    if("${stage_step}" == "Deploy to ${env} environment"){
      echo "Deploy to ${env} environment"
      emailcontent=emailcontentstart + "<br><u><b>Deployment failed in ${env} environment</b></u></br><br>${sonarLink}</br>" + emailcontentEnd
    }
    if("${stage_step}" == "${env} Smoke Test"){
      echo "Testing Smoke Test"
      emailcontent=emailcontentstart + emailcontentEnd
    }
  }
  else{
    if("${appName}" == "appName"){
      echo "Testing Success"

      emailcontentPass="""<br>Dear All,</br>
      
      <br>${appName} application has been built successfully with build no ${BUILD_NUMBER}</br>

      <br>Build Report Link for reference:</br>
      <br><u><b>Quality Review:</b></u></br>
      <br>${sonarLink}</br>
      
      emailcontent=emailcontentPass + emailcontentEnd
      """
    }else{
      echo "Testing Success"
      emailcontent=emailcontentPass + emailcontentEnd + " for ${env} environment"
    }
  }

  def regexStr=/[a-zA-Z0-9.'_%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}/

  if(To.matches(regexStr)){
    EmailRecipient=To
  }else{
    EmailRecipient=getEmail(To)  //calling shared getEmail fun and To=githubTeamNamesSeparatedBy;InPipeline
  }

  emailext to: "${EmailRecipient}",
  mimeType: 'text/html',
  subject: "${Subject}",
  body: "${emailcontent}"
}
