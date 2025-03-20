FROM eclipse-temurin:21-jdk

WORKDIR ./
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .

RUN ./gradlew --no-daemon dependencies

COPY src src
COPY config config

RUN ./gradlew --no-daemon build

ENV JAVA_OPTS "-Xmx512M -Xms512M"

EXPOSE 8080/tcp

#CMD /bin/bash
CMD java -jar build/libs/Task manager-0.0.1-SNAPSHOT.jar