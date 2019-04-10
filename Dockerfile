FROM gradle:jdk11-slim as builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

FROM openjdk:11-jre-slim

RUN apt-get update && apt-get install -y openssh-client
RUN mkdir /root/.ssh

COPY --from=builder /home/gradle/src/docstampr-api-rest/build/libs/docstampr-api-rest*.jar docstampr-api-rest.jar
COPY --from=builder /home/gradle/src/infra/docker/config /root/.ssh/
COPY --from=builder /home/gradle/src/infra/docker/known_hosts /root/.ssh/


EXPOSE 8889

ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /docstampr-api-rest.jar" ]
