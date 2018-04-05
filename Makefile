create-sonar-local:
	docker pull sonarqube:latest
	docker run -d --restart=always -p9000:9000 sonarqube:latest

generate-sonar-report-local:
	./gradlew clean install -DskipITs=true sonar:sonar -Dsonar.host.url=http://localhost:9000

dependency-check:
	./gradlew dependency-check:check

clean-install-skip-it-test:
	./gradlew clean install -DskipITs=true

lint-all:
	./gradlew -DcompilerArgument=-Xlint:all compile

run-transformationservice:
	./gradlew clean
	./gradlew spring-boot:run

compile-transformationservice:
	./gradlew compile

run-unit-tests:
	./gradlew test

