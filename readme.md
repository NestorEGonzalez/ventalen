[![Java CI with Maven](https://github.com/NestorEGonzalez/ventalen/actions/workflows/maven.yml/badge.svg)](https://github.com/NestorEGonzalez/ventalen/actions/workflows/maven.yml)

# Ventalen

> **Ventalen es una aplicación backend para el control de stock, ingresos y ventas de productos.**

API REST desarrollada con **Java 21** y **Spring Boot** para centralizar operaciones de inventario y gestión comercial. Incluye autenticación con JWT, persistencia en PostgreSQL, validaciones de negocio y pruebas automatizadas, todo corriendo en Docker.

## Funcionalidades

- Gestión de productos, categorías, clientes y proveedores.
- Registro de ingresos que actualizan el stock tanto positivamente como negativamente.
- Registro de ventas con descuento automático de stock.
- Ventas pendientes de pago y posterior confirmación del pago.
- Ajustes de inventario con validación de stock disponible.
- Autenticación con JWT y autorización por roles.
- Respuestas de error centralizadas y validación de datos de entrada.

## Tecnologías y diseño

| Área | Tecnologías |
| --- | --- |
| Lenguaje y framework | Java 21 · Spring Boot 4 |
| API | Spring MVC · OpenAPI · Swagger UI |
| Persistencia | Spring Data JPA · Hibernate · PostgreSQL |
| Seguridad | Spring Security · JWT |
| Mapeo | MapStruct · DTOs |
| Calidad | JUnit 5 · Mockito · Testcontainers · JaCoCo |
| Ejecución | Maven · Docker · Docker Compose |

La aplicación organiza el código por funcionalidad y separa responsabilidades:

`HTTP/JSON → Controller → Service → Repository → PostgreSQL`

Los DTOs y MapStruct mantienen el contrato HTTP desacoplado de las entidades persistidas. Las operaciones de negocio que modifican el inventario se ejecutan transaccionalmente para preservar su consistencia.

## Requisitos

- **Docker y Docker Compose** para ejecutar la aplicación y las pruebas de integración.
- **Java 21** para ejecutar Maven localmente.

## Inicio rápido con Docker

1. Clona el repositorio:

   ```bash
   git clone https://github.com/NestorEGonzalez/ventalen.git
   cd ventalen
   ```

2. Crea un archivo `.env` en la raíz del proyecto:

   ```dotenv
   DB_PASSWORD=una_clave_local_segura
   JWT_SECRET=REEMPLAZAR_POR_UNA_CLAVE_BASE64
   ```

   `JWT_SECRET` debe ser una clave Base64 de al menos 32 bytes. Puedes generar una con:

   ```bash
   openssl rand -base64 32
   ```

   Copia el resultado en `.env`. No publiques este archivo ni sus valores.

3. Construye e inicia la aplicación y PostgreSQL:

   ```bash
   docker compose up --build
   ```

   La API queda disponible en `http://localhost:8080`. Para detener los servicios, usa `Ctrl+C`; también puedes ejecutar `docker compose down` desde otra terminal.

   En Docker Compose se configura un usuario administrador de desarrollo: **admin / admin123**. Cambia esas credenciales antes de cualquier despliegue real.

## Ejecutar las pruebas con Maven

Ejecuta la suite completa desde la raíz:

```bash
./mvnw test
```

Las pruebas de integración usan **Testcontainers** y requieren que Docker esté disponible. Para ejecutar solo las pruebas unitarias:

```bash
./mvnw test -Dtest="!*TestIT"
```

Para generar el informe de cobertura JaCoCo:

```bash
./mvnw verify
```

El informe HTML se genera en `target/site/jacoco/index.html`.

## Probar la API con Swagger

Con la aplicación en ejecución, abre [Swagger UI](http://localhost:8080/swagger-ui.html). La especificación también está disponible en:

- [OpenAPI JSON](http://localhost:8080/v3/api-docs)
- [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

Para probar operaciones protegidas:

1. Ejecuta `POST /auth/login` con las credenciales de desarrollo:

   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```

2. Copia el valor `token` de la respuesta.
3. Pulsa **Authorize** en Swagger UI y pega el token JWT. Swagger añadirá el esquema `Bearer` al enviar las solicitudes.
4. Prueba los endpoints documentados. Las operaciones de lectura `GET` bajo `/ventalen/**` son públicas; las operaciones de escritura requieren el rol `ADMIN`.

## Reglas de negocio destacadas

- Las ventas descuentan el stock disponible y no permiten que quede negativo.
- Los ingresos incrementan el stock.
- Una venta puede quedar pendiente de pago descontando el stock, evitando futuras discrepancias.
- Los ajustes y las operaciones se procesan con validaciones y transacciones.
- Los datos de entrada se validan y los errores se devuelven mediante respuestas centralizadas.

## Próximas mejoras

- [ ] Reportes de inventario y ventas.

