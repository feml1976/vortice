# Multi-stage build para optimizar el tamaño de la imagen

# Etapa 1: Build
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (se cachea esta capa si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# Crear usuario no-root para ejecutar la aplicación
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
