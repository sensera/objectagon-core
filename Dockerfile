FROM ubuntu:16.04

ENV OBJECTAGON_VERSION="1.0-SNAPSHOT"

RUN apt-get update \
    && apt-get install -y software-properties-common \
    && add-apt-repository -y ppa:webupd8team/java \
    && apt-get update \
    && echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | debconf-set-selections

RUN apt-get update && apt-get install -y \
    oracle-java8-installer \
    && apt-get autoclean

COPY target/objectagon-core-$OBJECTAGON_VERSION-jar-with-dependencies.jar /objectagon-core.jar
COPY resources/start-objectagon.sh /

EXPOSE 9900

ENTRYPOINT ["/start-objectagon.sh"]




