StudentApp

Descripcion
- Aplicacion Java EE basada en Servlets/JSP para gestionar estudiantes.
- Contiene Dockerfile y docker-compose para ejecutar con MySQL.

Mejoras incluidas
- Logging consistente con Log4j (config en `src/log4j.properties`).
- Filtro `CharacterEncodingFilter` para forzar UTF-8 en todas las peticiones.
- Endpoint de salud `GET /health` para healthchecks.
- Login configurable por variables de entorno (`ADMIN_USER`, `ADMIN_PASSWORD`).
- Docker healthcheck de la app (requiere curl en la imagen de Tomcat).
- Limpieza: se eliminan `System.out` en filtros y se usa logger.

Requisitos
- Docker y Docker Compose instalados.

Como ejecutar (Docker Compose)
1. `docker-compose build`
2. `docker-compose up -d`
3. App: http://localhost:8080/
4. Admin: http://localhost:8080/admin

Credenciales por defecto
- Usuario: `admin`
- Password: `admin`
- Se pueden configurar via variables de entorno en `docker-compose.yml`:
  - `ADMIN_USER`, `ADMIN_PASSWORD`

Base de datos
- MySQL 8.0 con `init.sql` para crear la tabla `student`.
- Variables de conexion se inyectan a Tomcat a traves de `WebContent/META-INF/context.xml` usando:
  - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.

Seed de datos (demo)
- Insertar N alumnos aleatorios en la DB (por defecto 100):
  - `scripts/seed_students.sh 200`
  - Usa `docker compose exec db mysql` si está disponible; si no, intenta con el cliente `mysql` local.
  - Respeta variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` (con defaults del compose).

Estructura del proyecto
- Codigo Java: `src/`
- Recursos web (JSP/CSS/JS): `WebContent/`
- Config web.xml: `WebContent/WEB-INF/web.xml`

Roadmap sugerido (profesionalizar mas)
- Tests: agregar JUnit 5 y Testcontainers para pruebas de integracion con MySQL.
- Seguridad: almacenar usuarios/roles en DB y hash de passwords; considerar Spring Security.
- Persistencia: migrar a JPA/Hibernate o Spring JDBC Template.
- Observabilidad: migrar a SLF4J + Logback y agregar trazas estructuradas.
- CI/CD: workflow de GitHub Actions para build y analisis estatico (Checkstyle/SpotBugs/PMD).
- Docker: publicar imagen con tags versionados; reducir tamaño de imagen.

Configuración avanzada
- AppConfig centralizada (`src/com/studentapp/config/AppConfig.java`) con carga jerárquica: `-Dprop` > variables de entorno > `src/app.properties`.
- Filtro de contexto (`RequestContextFilter`):
  - Correlation ID (`X-Request-Id`) inyectado en la respuesta y MDC (aparece en logs).
  - CORS configurable por propiedades (orígenes, métodos, headers, credenciales, max-age).
  - Cabeceras de seguridad (CSP, X-Frame-Options, X-Content-Type-Options, Referrer-Policy).
- Login usa AppConfig: credenciales y timeouts de sesión configurables.
- Log4j: patrones incluyen `reqId=%X{requestId}` para correlación.

Props por defecto (`src/app.properties`)
- `admin.user`, `admin.password`.
- `session.timeout.short.seconds`, `session.timeout.long.seconds`.
- `cors.enabled`, `cors.allowed.origins`, `cors.allowed.methods`, `cors.allowed.headers`, `cors.allow.credentials`, `cors.max.age.seconds`.
- `security.headers.enabled`, `security.csp`, `security.frame.options`, `security.referrer.policy`.

Cómo sobreescribir
- Variables de entorno: usar nombres en MAYÚSCULA con `_` (ej: `ADMIN_USER`, `CORS_ENABLED`).
- System properties: `-Dadmin.user=foo -Dcors.enabled=true`.
- Modificar `src/app.properties` para defaults.
