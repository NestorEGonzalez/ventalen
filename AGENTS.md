# Ventalen — AGENTS.md

## Build & run

- **Build**: `./mvnw clean package -DskipTests`
- **Run locally**: `docker compose up --build` (requires `.env` with `DB_PASSWORD`, `JWT_SECRET`)
- **Run dev** (no Docker): `./mvnw spring-boot:run` — needs PostgreSQL running + env vars set (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`)
- **Maven wrapper** is committed and executable (`mvnw`)

## Test

- **All tests**: `./mvnw test`
- **Unit tests**: `*Test.java` (Mockito, `@WebMvcTest` or `@ExtendWith(MockitoExtension.class)`)
- **Integration tests**: `*TestIT.java` (SpringBootTest + Testcontainers) — require Docker daemon
- **Test base class**: `TestBase` provides `@Testcontainers` + `@ActiveProfiles("test")` + PostgreSQL 16 container
- **Skip ITs**: `./mvnw test -Dtest="!*TestIT"` (or just let them fail if no Docker)

## Java version mismatch

- `pom.xml` sets `<java.version>21</java.version>`
- But `maven-compiler-plugin` uses source/target **17**, and `Dockerfile` builds/runs on **eclipse-temurin:17**
- Either match to 17, or upgrade Dockerfile & compiler plugin to 21

## Project structure

- **Package**: `com.ventalen` — feature-based subpackages (`producto/`, `categoria/`, `stock/`, `auth/`, `security/`, `exception/`, `cliente/`, `proveedor/`, `ingreso/`, `venta/`, `ajuste/`, `motivo/`)
- **Layer per feature**: Entity → Repository → Service → Controller + DTO + Mapper (MapStruct)
- **Custom exceptions** in `exception/` package with `GlobalExceptionHandler` for centralized error response

## API

- All controllers mapped under `/productos` (`@RequestMapping("/productos")`), routes duplicate it (e.g. `POST /productos/productos`, `GET /productos/categorias`)
- **Auth**: `POST /auth/login` public, `POST /auth/register` requires ADMIN
- **Security**: JWT stateless auth; GET endpoints public, POST/PATCH/DELETE on `/productos/**` require ADMIN role
- **OpenAPI spec**: `src/main/resources/static/openapi.yaml`

## Config quirks

- `application.properties` reads all secrets from env vars — no defaults
- `application-test.properties` has hardcoded test JWT secret + admin creds
- `Initializer.java` creates admin user on first startup via `CommandLineRunner` (reads `ADMIN_USERNAME`/`ADMIN_PASSWORD` env vars)
- MapStruct annotation processor configured in `maven-compiler-plugin`

## Docker

- `docker-compose.yml` contains both `db` (postgres:15) and `app` services
- `Dockerfile` builds jar with `mvn clean package -DskipTests` then runs it
