# Etapa 1: Construcción
FROM openjdk:17-jdk-alpine AS build

# Configura el directorio de trabajo
WORKDIR /app

# Copia el código fuente
COPY . /app/src

# Compila el código fuente Java
RUN javac -d out -cp /app/src /app/src/AuthClient.java

# Etapa 2: Ejecución
FROM openjdk:17-jdk-alpine

# Configura el directorio de trabajo
WORKDIR /app

# Copia los archivos compilados desde la etapa de construcción
COPY --from=build /app/out /app/out

# Instala wget y dockerize
RUN apk add --no-cache wget && \
    wget https://github.com/jwilder/dockerize/releases/download/v0.6.1/dockerize-linux-amd64-v0.6.1.tar.gz && \
    tar -xzf dockerize-linux-amd64-v0.6.1.tar.gz && \
    mv dockerize /usr/local/bin/ && \
    rm dockerize-linux-amd64-v0.6.1.tar.gz

# Comando para ejecutar la aplicación con dockerize
ENTRYPOINT ["dockerize", "-wait", "tcp://authorizerserver:80", "-timeout", "60s", "java", "-cp", "/app/out", "AuthClient"]
