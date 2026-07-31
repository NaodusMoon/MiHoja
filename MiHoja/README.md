# MiHoja

MiHoja es una aplicacion para gestionar hojas de vida de personal. El proyecto combina un backend en Spring Boot, vistas heredadas en Thymeleaf y un frontend en Next.js.

## Stack

### Backend

- Java 21
- Spring Boot 4.0.5
- Spring MVC
- Spring Data JPA / Hibernate
- PostgreSQL sobre Supabase
- Flyway
- Bean Validation
- Spring Boot Actuator
- MapStruct
- Testcontainers
- Thymeleaf

### Frontend

- Next.js 16.2.0
- React 19.2.0
- TypeScript
- App Router

### Infraestructura

- Dockerfile para backend
- Dockerfile para frontend
- `docker-compose.yml`

## Estructura

```text
MiHoja/
  src/main/java/...         Backend Spring Boot
  src/main/resources/...    Configuracion, templates y migraciones
  frontend/                 Frontend Next.js
  scripts/dev.mjs           Arranque local de backend + frontend
  dev.cmd                   Wrapper para ejecutar en Windows
  dev.sh                    Wrapper para ejecutar en Linux / macOS
  Dockerfile                Imagen del backend
  docker-compose.yml        Servicios backend + frontend
```

## Requisitos

- Java 21
- Node.js y npm
- Archivo `.env` en la raiz del proyecto

## Variables de entorno

Copia [`.env.example`](C:/Users/NaodusMoon/Music/mh/MiHoja/MiHoja/.env.example) como `.env` y completa los valores reales.

Ejemplo base:

```properties
MIHOJA_DB_URL=jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require
MIHOJA_DB_USERNAME=mihoja_app.TU_PROJECT_REF
MIHOJA_DB_PASSWORD=TU_PASSWORD
APP_FRONTEND_URL=http://localhost:3000

MIHOJA_STORAGE_PROVIDER=local
```

Para usar Supabase Storage:

```properties
MIHOJA_STORAGE_PROVIDER=supabase
SUPABASE_URL=https://TU_PROJECT_REF.supabase.co
SUPABASE_SERVICE_KEY=TU_SERVICE_ROLE_KEY
SUPABASE_STORAGE_BUCKET=mihoja
SUPABASE_STORAGE_PUBLIC=true
```

Variables usadas por el frontend:

```properties
API_BASE_URL=http://localhost:8080
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

## Ejecucion local

### Backend + frontend

Desde la raiz del proyecto:

```bash
npm run dev
```

En Windows PowerShell, si `npm` esta bloqueado por la politica de ejecucion, usa:

```powershell
.\dev.cmd
```

El comando levanta:

- backend: `http://localhost:8080`
- frontend: `http://localhost:3000`

Tambien instala automaticamente las dependencias del frontend si `frontend/node_modules/` no existe.

### Backend solamente

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### Frontend solamente

```bash
cd frontend
npm install
npm run dev
```

## Build

### Backend

```bash
# Windows
.\mvnw.cmd -DskipTests compile

# Linux / macOS
./mvnw -DskipTests compile
```

### Frontend

```bash
cd frontend
npm run build
```

## Docker

```bash
docker compose up --build
```

Servicios:

- backend: `http://localhost:8080`
- frontend: `http://localhost:3000`

## API

### `GET /api/dashboard/overview`

Entrega datos para el dashboard:

- metricas principales
- personas recientes
- highlights del stack

Ejemplo:

```json
{
  "metrics": [
    { "id": "total", "label": "Registros", "value": "114", "tone": "neutral" }
  ],
  "recentPeople": [],
  "highlights": []
}
```

## Base de datos y migraciones

- Base de datos: Supabase PostgreSQL
- Migraciones con Flyway
- `spring.jpa.hibernate.ddl-auto=validate`
- Migraciones en `src/main/resources/db/migration/`

## Storage

El almacenamiento de imagenes puede ejecutarse en modo local o Supabase:

- `local`
- `supabase`

Servicios:

- [ImageStorageService](C:/Users/NaodusMoon/Music/mh/MiHoja/MiHoja/src/main/java/com/miapp/MiHoja/service/storage/ImageStorageService.java)
- [LocalImageStorageService](C:/Users/NaodusMoon/Music/mh/MiHoja/MiHoja/src/main/java/com/miapp/MiHoja/service/storage/LocalImageStorageService.java)
- [SupabaseImageStorageService](C:/Users/NaodusMoon/Music/mh/MiHoja/MiHoja/src/main/java/com/miapp/MiHoja/service/storage/SupabaseImageStorageService.java)
