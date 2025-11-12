FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || return 0

COPY src src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8080

COPY --from=builder /app/build/libs/RedSysAPI-0.0.1-SNAPSHOT.jar app.jar

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]
