create-sonar-local:
	docker pull sonarqube:latest
	docker run -d --restart=always -p9000:9000 sonarqube:latest

generate-sonar-report-local:
	mvn clean install -DskipITs=true sonar:sonar -Dsonar.host.url=http://localhost:9000

dependency-check:
	mvn dependency-check:check

clean-install-skip-it-test:
	mvn clean install -DskipITs=true

lint-all:
	mvn -DcompilerArgument=-Xlint:all compile

run-transformationservice:
	mvn clean
	mvn spring-boot:run

compile-transformationservice:
	mvn compile

run-unit-tests:
	mvn test

