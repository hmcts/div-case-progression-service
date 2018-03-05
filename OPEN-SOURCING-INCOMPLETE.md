# Incomplete Tasks

1. docker hostname references in pom.xml and docker-compose.yml need to refer to publicly accessible hosts. Currently there is no solution as to how this will work
: Alec suggests that these will not be a problem

2. docker secret values possibly need to be injected (need to check if these are dummy values or not)
 
3. sonar url references in JenkinsFile need to refer to publicly accessible hosts. Currently there is no solution as to how this will work

4. all of the json files in the resources/divorce-payload-json directory of the test submodule refer via urls to documents stored in a generic internal area. As no publicly available document storage area has yet been set up, these urls cannot be adapted
