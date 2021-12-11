#
# Build stage
#
ARG JAVA_VERSION=16.0.1_9
FROM adoptopenjdk/openjdk16:jdk-${JAVA_VERSION}-alpine as compiler
COPY . .
RUN apk add sed \
  && ./gradlew shadowJar --no-daemon
#
# Run stage
#
FROM adoptopenjdk/openjdk16:jre-${JAVA_VERSION}-alpine
ARG ENV
ENV JVM_ARGS -XX:+UseContainerSupport \
  -XX:InitialRAMPercentage=60 \
  -XX:MaxRAMPercentage=60 \
  -XX:MaxDirectMemorySize=300m \
  -XX:+AlwaysPreTouch \
  -XX:+UseG1GC \
  -XX:+UseStringDeduplication \
  -XX:MaxGCPauseMillis=100 \
  -XX:ParallelGCThreads=4 \
  -XX:FlightRecorderOptions=stackdepth=256 \
  --add-opens=java.base/java.net=ALL-UNNAMED
# vault credentials
ENV APP_JAR_FILE cinema.jar
ENV APP_HOME /usr/app

COPY --from=compiler /application/build/libs/. $APP_HOME/
WORKDIR $APP_HOME

RUN apk --no-cache add curl

ENTRYPOINT ["sh", "-c"]
CMD ["exec java ${JVM_ARGS} -Denv=${ENV}  -jar ${APP_JAR_FILE}"]
EXPOSE 8080
