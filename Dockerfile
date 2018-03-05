FROM maven:3.3.9-jdk-8
RUN mkdir -p /opt/app
WORKDIR /opt/app
ADD . /opt/app
CMD ["mvn","clean","install","spring-boot:run", "-DskipTests" ]
