# MiHoja

Aplicacion web para gestionar hojas de vida de personal (consulta, insercion, edicion, importacion y descarga), construida con Spring Boot y Thymeleaf.

## Arquitectura del proyecto

El proyecto usa una **arquitectura monolitica en capas** con patron **MVC**:

- `controller`: capa web (rutas HTTP, binding de parametros, retorno de vistas).
- `service`: logica de negocio y orquestacion de casos de uso.
- `repository`: acceso a datos con Spring Data JPA.
- `model`: entidades JPA persistidas en base de datos.
- `dto`: objetos de transferencia para flujos de edicion/consulta compleja.
- `templates`: vistas del lado servidor con Thymeleaf.
- `static`: recursos estaticos (CSS, imagenes).

### Estilo arquitectonico

- Monolito modular (no microservicios).
- Renderizado server-side (SSR) con Thymeleaf.
- Persistencia relacional con Hibernate/JPA.
- Configuracion y wiring via Spring Boot.

## Tecnologias usadas

- Java 17
- Spring Boot 3.5.3
- Spring MVC (`spring-boot-starter-web`)
- Spring Data JPA + Hibernate (`spring-boot-starter-data-jpa`)
- Thymeleaf (`spring-boot-starter-thymeleaf`)
- PostgreSQL (produccion/desarrollo)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Apache POI (lectura/escritura Excel)
- OpenPDF (generacion de PDF)
- Commons Text
- Spring Boot DevTools
- JUnit/Spring Boot Test + H2 (tests)

## Configuracion principal

Archivo: `src/main/resources/application.properties`

Puntos relevantes:

- `spring.jpa.open-in-view=false` (las relaciones lazy deben cargarse en la capa de servicio/repositorio).
- Base de datos PostgreSQL configurable por variables:
  - `DB_URL`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- Puerto por variable `SERVER_PORT` (default `8080`).

## Ejecucion local

Desde la carpeta `MiHoja/`:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

La app inicia normalmente en:

`http://localhost:8080`

## Compilacion

```bash
# Windows
.\mvnw.cmd -DskipTests compile

# Linux/Mac
./mvnw -DskipTests compile
```

## Manejo de errores (nuevo)

Se agrego una vista amigable de error:

- Ruta de error: `/error`
- Vista: `src/main/resources/templates/error.html`
- Controlador: `AppErrorController`
- Opcion de detalle tecnico:
  - sin detalle: `/error`
  - con detalle: `/error?detalle=true`

La vista muestra:

- codigo HTTP
- mensaje amigable
- ruta y timestamp
- opcion de mostrar/ocultar stacktrace tecnico

## Estructura resumida

```text
src/main/java/com/miapp/MiHoja
  config/
  controller/
  dto/
  model/
  repository/
  service/
    maintenance/
    query/
    support/
    view/
src/main/resources
  templates/
  static/
  application.properties
```
