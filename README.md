In this assesment application, I have created two rest APIs, one is /scan, another one is /query.
The source code is in assesment/src folder. Main contains the Java code for the core functionalities and test folder contains the Junit test cases for the core functionalities.
The flow starts with ScannerController, and then invokes ScannerService and finally VulnerabilityRepository gets invoked. Properties are configures using application.properties file.

Steps two build this project:
Prerequisite: docker should be installed in the system.

Download the project from Github.

Start docker in the system

using Command line, navigate to the assesment(root) directory of the project. Inside this directory I have placed the Dockerfile.

use this command to build the image : docker build -t my-spring-boot-app .

now run this command to start the container : docker run -d -p 8080:8080 --name my-app my-spring-boot-app

now we can test using postman

Import these curl command in postman and test /scan endpoint

#1. Curl command for positive response

curl --location 'http://localhost:8080/api/scan' \
--header 'Content-Type: application/json' \
--data '{
  "repo": "https://raw.githubusercontent.com/velancio/vulnerability_scans",
  "files": ["vulnscan15.json","vulnscan1213.json"]
}'

#2. Curl Command to Check Failure to connect GitHub

curl --location 'http://localhost:8080/api/scan' \
--header 'Content-Type: application/json' \
--data '{
  "repo": "https://raw.githubusercontent.com/velancio",
  "files": ["vulnscan15.json","vulnscan1213.json"]
}'

Import these curl command to test /query endpoint

#1. curl command to get matching vulnerabilities

curl --location 'http://localhost:8080/api/query' \
--header 'Content-Type: application/json' \
--data '{
    "filters": {
        "severity": "LOW"
    }
}
'

#2.curl command to test error scenario

curl --location 'http://localhost:8080/api/query' \
--header 'Content-Type: application/json' \
--data '{
    "filters": {
        "severity": "error"
    }
}
'

We can also pull the code into IDE( I used Intellij). Then run these maven commands to build the package. As a prerequieste we should have maven installed
and the Maven Home path should be set to Ide's Build Tools. 
 mvn clean
 mvn install

 After the successful build, go to the IDe's :
 Run-->Edit Configuration-->Add a new Configuration-->Choose "Application"
 --> In the Build and run options, choose Java 21 SDK and add main class :kai.cyber.assesment.AssesmentApplication
 -->Apply and Ok-->Run this saved configuration

 when the application is started we can test using the previously mentioned curl commands


Please note, while processing multiple files using concurrent threads , sometimes I am getting Database lock error in Sqlite, which I understand is common in Sqlite, as it is not
optimized for multiple concurrent save.







