# div-case-progression-service

## A restful service used to transform a divorce frontend session to a general core case data format

## Introduction
* This API provides below endpoints
  * JSON Transformation
* It uses below technical stack
  *  Java8
  * Spring Boot
  * Junit, Mockito, WireMock and SpringBootTest
* Plugins used by project
  * Jacoco
  * OWASP dependency check
  * Sonar
  * Xlint
  * Checkstyle 
  #### :bulb: Checkstyle is currently configured to not fail on violations as the configuration is not yet finalised.

## Project setup
> * git clone [https://github.com/hmcts/div-div-case-progression-service.git](https://github.com/hmcts/div-div-case-progression-service.git)
> * cd div-case-progression-service
> * Run `make run-transformationservice` This command will start the spring boot application in an embedded tomcat on port 4003.To change the port change the configuration in `application.properties`

## Below commands are available in the make file


This command will create a dependency check report to identify the use of known vulnerable components.
```
make dependency-check
```

This command will check to see if the code is adheres to the code linting specification
```
make lint-all
```
    
This command creates a local SonarQube instance which can accessed at http://localhost:9000/ 
```
make create-sonar-local
```

This generates sonar reports and updates the local sonar qube instance. (Before executing this make command make sure make create-sonar-local is executed.)

```
make generate-sonar-report-local
```

This command will run unit tests.

```
make run-unit-tests
```

This command will run service locally on port 4003. e.g post to url http://localhost:4003/transformationapi/version/1/submit

```
make run-transformationservice
```

 
## API Consumption

| File Upload Endpoint | HTTP Protocol | Header Attribute  Condition | Headers | Body |
|:----------------------------------:|---------------|:---------------------------:|:------------------------------------:|:----------------------------------------------------------------:|
| /transformationapi/version/1/submit | POST | Required | AuthorizationToken : { User Token }  |  |
|  |  | Required | Content-Type :application/json  |  |
|  |  | Optional | RequestId :{RequestId} |  |

## Request Body Example:

```json
{
  "petitionerFirstName": "Danny"
}
```

## Service Response Examples:

| Status | Response |
|:----------------------------------:|:-----------------|
| Success | {"caseId": 1509031793780148,"error": null,"status": "success"} |

| Status | Response |
|:----------------------------------:|:-----------------|
| Error | "caseId": 0, <br> "error": "Request Id : 1234 and Exception message : 422 , Exception response body: {"exception":"uk.gov.hmcts.ccd.endpoint.exceptions.CaseValidationException","timestamp":"2017-10-26T16:04:37.03","status":422,"error":"Unprocessable Entity","message":"Case data validation failed","path":"/citizens/69/jurisdictions/DIVORCE/case-types/DIVORCE/cases","details":{"field_errors":\\{"id":"D8DivorceWho","message":"wiferhello is not a valid value"}},"callbackErrors":null,"callbackWarnings":null}", <br> "status": "error"  |

## Run the transformation service in a Docker container

Start the docker container
```
docker-compose up
```

NOTE: You'll need to start Div-Validation-Service manually until it gets published to docker.

Create the citizen and caseworker-divorce roles in CCD
```
./scripts/ccd-add-role.sh citizen PUBLIC
./scripts/ccd-add-role.sh caseworker-divorce RESTRICTED
```

Upload a mapping spreadsheet
```
./scripts/ccd-import-definition.sh /path/to/spreadsheet
```

Create a user in IDAM and return a JWT (requires jq)
```
./scripts/divorce-user-jwt.sh
```
##  License
```The MIT License (MIT)

Copyright (c) 2018 HMCTS (HM Courts & Tribunals Service)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
