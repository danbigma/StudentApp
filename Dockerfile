# Usar una imagen base de Maven para construir el proyecto
FROM maven:3.8.4-openjdk-8 AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y las dependencias de Maven
COPY pom.xml .

# Descargar las dependencias sin construir el proyecto
RUN mvn dependency:go-offline -B

# Copiar el resto del código fuente del proyecto
COPY src ./src
COPY WebContent ./WebContent

# Construir el proyecto
RUN mvn package

# Verificar que el archivo WAR se ha generado correctamente
RUN ls -la target/

# Renombrar el archivo WAR generado a ROOT para desplegar en "/"
RUN mv target/StudentApp-0.0.1-SNAPSHOT.war target/ROOT.war

# Usar una imagen base de Tomcat para desplegar la aplicación
FROM tomcat:9.0.53-jdk8-openjdk

# Add curl for healthcheck
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# Map DB_* env vars to Java system properties for Tomcat property replacement in context.xml
RUN set -eux; \
    f=/usr/local/tomcat/bin/setenv.sh; \
    echo '#!/bin/sh' > "$f"; \
    echo 'CATALINA_OPTS="$CATALINA_OPTS \\' >> "$f"; \
    echo ' -DDB_HOST=${DB_HOST:-db} \\' >> "$f"; \
    echo ' -DDB_PORT=${DB_PORT:-3306} \\' >> "$f"; \
    echo ' -DDB_NAME=${DB_NAME:-studentdb} \\' >> "$f"; \
    echo ' -DDB_USER=${DB_USER:-user} \\' >> "$f"; \
    echo ' -DDB_PASSWORD=${DB_PASSWORD:-user_password}"' >> "$f"; \
    echo 'export CATALINA_OPTS' >> "$f"; \
    chmod +x "$f"

# Copiar el archivo WAR renombrado al directorio webapps de Tomcat
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/

# Exponer el puerto 8080
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --retries=3 CMD curl -fsS http://localhost:8080/health || exit 1

# Comando para ejecutar Tomcat
CMD ["catalina.sh", "run"]
