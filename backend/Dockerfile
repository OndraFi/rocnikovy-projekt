FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY src src

RUN chmod +x gradlew

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre-focal
WORKDIR /app
EXPOSE 8080

COPY --from=builder /app/build/libs/RedSysAPI-0.0.1-SNAPSHOT.jar app.jar

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]
