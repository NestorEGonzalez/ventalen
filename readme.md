[![Java CI with Maven](https://github.com/NestorEGonzalez/ventalen/actions/workflows/maven.yml/badge.svg)](https://github.com/NestorEGonzalez/ventalen/actions/workflows/maven.yml)

# 🛒 Ventalen

API REST desarrollada con **Java 21** y **Spring Boot** para la gestión integral de comercios.

El proyecto permite administrar productos, categorías, clientes, proveedores, ingresos, ventas y ajustes de stock, implementando reglas de negocio para garantizar la consistencia del inventario.

Fue desarrollado siguiendo una estrategia **Test Driven Development (TDD)** y utiliza **Spring Security**, **JWT**, **PostgreSQL**, **Docker** y **Testcontainers**.

## Características

- Gestión de productos y categorías.
- Administración de clientes y proveedores.
- Registro de ingresos y ventas.
- Ajustes automáticos de stock.
- Ventas pendientes de pago (fiado).
- Autenticación y autorización mediante JWT.
- Validaciones con Bean Validation.
- Manejo global de excepciones.
- Arquitectura por capas.
- Pruebas unitarias e integración.

## Tecnologías

| Categoría | Tecnologías |
|------------|-------------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4 |
| Persistencia | Spring Data JPA · Hibernate |
| Seguridad | Spring Security · JWT |
| Base de datos | PostgreSQL |
| Testing | JUnit 5 · Mockito · Testcontainers |
| Contenedores | Docker · Docker Compose |
| Build | Maven |
| Mapeo | MapStruct |

## Arquitectura y Diseño

El proyecto utiliza una **Arquitectura en Capas** limpia y mantenible, aplicando el patrón DTO para desacoplar las entidades de dominio de la capa de exposición API:

`Controller ➔ DTO ➔ MapStruct ➔ Service ➔ Repository ➔ PostgreSQL`

- **Manejo Global de Excepciones:** Centralizado mediante `@ControllerAdvice` para retornar respuestas estandarizadas.
- **Gestión Transaccional:** Uso explícito de `@Transactional` en la capa de servicios para garantizar atomicidad y consistencia (ACID) en operaciones multietapa (ej. actualización de stock al procesar ventas).

## Seguridad

- Autenticación Stateless basada en **Spring Security** y **JWT (JSON Web Tokens)**.
- Control de acceso basado en roles **(admin, user)** para proteger endpoints críticos según el perfil de usuario.

## Testing & Calidad de Código

Desarrollado bajo la metodología **Test Driven Development (TDD)**:

- **Pruebas Unitarias:** Cobertura de la lógica de negocio en la capa de servicios aislando dependencias con **Mockito**.
- **Pruebas de Integración:** Verificación de repositorios JPA y flujos de base de datos ejecutados sobre una instancia real de **PostgreSQL** orquestada por **Testcontainers**.

Ejecución de suite de pruebas:

```bash
    ./mvnw test
```
               
## Reglas de negocio

- No se permiten productos duplicados.
- No se permiten categorías duplicadas.
- No es posible registrar ventas que generen stock negativo.
- Los ingresos incrementan automáticamente el stock.
- Los ajustes actualizan el stock de forma consistente.
- Las ventas descuentan el stock disponible.
- Las ventas pueden registrarse como pendientes de pago (fiado) y descuentan stock.
- Las operaciones críticas se ejecutan dentro de transacciones para mantener la integridad de los datos.

## Instalación y ejecución

1. **Clonar el repositorio:**
```bash
    git clone [https://github.com/NestorEGonzalez/ventalen.git] (https://github.com/NestorEGonzalez/ventalen.git)
    cd ventalen
```
2. **Configurar variables de entorno:** (antes de levantar los contenedores, es necesario definir las variables de entorno en local):

```bash
    export DB_PASSWORD = una_contraseña_segura
    export JWT_SECRET = un_hash_sha256
```
También puede crearse un archivo `.env` dentro del proyecto con ambas variables.

3. **Iniciar la aplicación:**

```bash
    docker compose up --build
```
        
## Roadmap

- [x] Gestión de productos
- [x] Gestión de categorías
- [x] Gestión de clientes
- [x] Gestión de proveedores
- [x] Gestión de ingresos
- [x] Gestión de ventas
- [x] Gestión de stock
- [x] Seguridad con JWT
- [x] Docker
- [x] Tests unitarios
- [x] Tests de integración
- [ ] Reportes
- [ ] OpenAPI / Swagger
- [ ] JaCoCo
- [ ] GitHub Actions
