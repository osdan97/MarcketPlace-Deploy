# Utiliza una imagen base de Java
FROM amazoncorretto:17-alpine-jdk

# Establece el directorio de trabajo en la aplicación
#WORKDIR /app

# Copia el archivo JAR de la aplicación al contenedor
COPY target/MarcketPlace-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que se ejecutará la aplicación
#EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "/app.jar"]