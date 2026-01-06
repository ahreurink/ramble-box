#
# BUILD STAGE
#
FROM maven:3.9.12-eclipse-temurin-25-alpine AS build-base

ARG OPENAI_API_KEY

#RUN apk add docker

RUN apk add nodejs npm
RUN npm install -g @angular/cli
RUN mvn -U -B test verify
#COPY embabel /root/.m2/repository/com/embabel
##COPY m2 /root/.m2

#COPY src /usr/src/ramble-box/src
#COPY pom.xml /usr/src/ramble-box/pom.xml
#RUN mvn -f /usr/src/ramble-box/pom.xml -DskipTests package

#
# PACKAGE STAGE
#
FROM ramble-box-builder:latest

#COPY --from=build /root/.m2 /root/.m2
#COPY --from=build /usr/src/ramble-box/target/* /usr/ramble-box/

COPY scripts/gh-mcp.sh /usr/ramble-box/scripts/gh-mcp.sh
COPY target/RambleBox-0.1.0-SNAPSHOT.jar /usr/ramble-box/RambleBox-0.1.0-SNAPSHOT.jar

WORKDIR /usr/ramble-box

EXPOSE 8080

CMD ["java", "-jar", "/usr/ramble-box/RambleBox-0.1.0-SNAPSHOT.jar"]