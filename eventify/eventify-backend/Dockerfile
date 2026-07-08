FROM eclipse-temurin:21-jdk AS build
ARG SERVICE_NAME
WORKDIR /workspace

COPY settings.gradle.kts build.gradle.kts gradlew ./
COPY gradle gradle
COPY */build.gradle.kts ./

RUN --mount=type=cache,id=gradle-${SERVICE_NAME},target=/root/.gradle \
    ./gradlew :${SERVICE_NAME}:dependencies --no-daemon || true

COPY . .
RUN --mount=type=cache,id=gradle-${SERVICE_NAME},target=/root/.gradle \
    ./gradlew :${SERVICE_NAME}:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME
WORKDIR /app

COPY --from=build /workspace/${SERVICE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]