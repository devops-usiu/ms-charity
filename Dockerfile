# For Java 11, try this
FROM fabric8/java-alpine-openjdk11-jre:1.9.0
EXPOSE  8080
# Refer to Maven build -> final name extra
ARG JAR_FILE=target/ms-devsecops-wit-0.0.1-SNAPSHOT.jar

# cd /opt/app
WORKDIR /opt/app

# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]