FROM ubuntu:latest

# Updating apt-get repos
RUN apt-get update

# Installing JDK
RUN apt-get install default-jdk -y

# Installing maven
RUN apt-get install maven -y

# Copying project into the image
WORKDIR /usr

COPY . .

WORKDIR /usr/app

# Building the project and running it

RUN mvn package

# Add here jar name

CMD ["java","-jar","target/app-1.0-SNAPSHOT-jar-with-dependencies.jar"]