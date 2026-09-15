# --- Etapa 1: compilar ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY gateway-service/pom.xml gateway-service/
COPY taxista-service/pom.xml taxista-service/
COPY calificacion-service/pom.xml calificacion-service/
COPY portal-web/pom.xml portal-web/
RUN mvn -q -pl portal-web -am dependency:go-offline -DskipTests

COPY . .
RUN mvn -q -pl portal-web -am clean package -DskipTests

# --- Etapa 2: ejecutar ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /build/portal-web/target/*.jar app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k"
ENV THYMELEAF_CACHE=true
EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
