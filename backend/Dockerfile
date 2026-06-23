# Stage 1: builder
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY pventabase-common/pom.xml pventabase-common/
COPY pventabase-usuarios/pom.xml pventabase-usuarios/
COPY pventabase-login/pom.xml pventabase-login/
COPY pventabase-inventario/pom.xml pventabase-inventario/
COPY pventabase-clientes/pom.xml pventabase-clientes/
COPY pventabase-app/pom.xml pventabase-app/

RUN mvn dependency:go-offline -B

COPY . .
RUN mvn package -DskipTests -pl pventabase-app -am

# Stage 2: runtime
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S appuser && adduser -S appuser -G appuser
USER appuser
WORKDIR /app

COPY --from=builder /build/pventabase-app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
