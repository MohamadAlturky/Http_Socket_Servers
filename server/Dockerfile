FROM openjdk:8-jdk-alpine

LABEL maintainer="Wagner Franchin"

ENV WEBPATH=/webserver/
ENV WEBSERVER="WebServerSocket"

RUN apk update && apk add ca-certificates wget && update-ca-certificates

RUN mkdir -p $WEBPATH
COPY src/ $WEBPATH
WORKDIR $WEBPATH

RUN javac *.java task/*.java

EXPOSE 8000

CMD java $WEBSERVER