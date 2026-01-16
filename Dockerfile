# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy pom.xml and download dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# ---------- Run stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
