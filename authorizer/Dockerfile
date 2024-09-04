# Etapa 1: Construcción
FROM gradle:7.6-jdk17 AS build

# Directorio de trabajo
WORKDIR /app

# Copia el archivo de configuración de Gradle y el código fuente
COPY build.gradle settings.gradle /app/
COPY src /app/src

# Ejecuta el build de Gradle
RUN gradle build --no-daemon

# Etapa 2: Ejecución
FROM openjdk:17-jdk-slim

# Directorio de trabajo
WORKDIR /app

# Copia el archivo JAR generado en la etapa de construcción
COPY --from=build /app/build/libs/authorizer-0.0.1-SNAPSHOT.jar /app/authorizer.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 80

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "authorizer.jar"]
