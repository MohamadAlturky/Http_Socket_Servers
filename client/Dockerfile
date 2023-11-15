FROM openjdk:8-jdk-alpine

LABEL maintainer="Mohamad Alturky"

ENV PATH=/webserver/
ENV RUNNER="RestApiLoadTester"

RUN apk update && apk add ca-certificates wget && update-ca-certificates

RUN mkdir -p $PATH
COPY src/ $PATH
WORKDIR $PATH

RUN javac *.java

CMD java $RUNNER