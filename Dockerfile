FROM gradle:jdk11-slim as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM openjdk:11-jre-slim

COPY --from=builder /home/gradle/src/docstampr-api-rest/build/libs/docstampr-api-rest*.jar docstampr-api-rest.jar

EXPOSE 9898

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /docstampr-api-rest.jar" ]
