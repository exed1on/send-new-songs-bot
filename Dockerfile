FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
ENV SPRING_PROFILES_ACTIVE=prod

RUN apt-get update && \
    apt-get install -y python3.7 && \
    ln -s /usr/bin/python3.7 /usr/bin/python

RUN apt-get install -y ffmpeg

RUN pip3 install spotdl

ENV SPOTDL_PATH /usr/local/bin/spotdl

COPY --from=build /target/spotify-pl-new-song-to-tg-bot-0.0.1-SNAPSHOT.jar spotify-pl-new-song-to-tg-bot.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","spotify-pl-new-song-to-tg-bot.jar"]