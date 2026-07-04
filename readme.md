# 🚀 Ventalen

Sistema de gestión comercial desarrollado en **Java 21 con Spring Boot**, diseñado bajo principios de **arquitectura limpia, TDD y buenas prácticas de desarrollo backend**.

El proyecto comenzó como una API de catálogo de productos y actualmente evoluciona hacia una plataforma de gestión integral orientada al control de stock, ventas y administración comercial.

---

## 🎯 Objetivo

Construir una aplicación mantenible, escalable y extensible que permita gestionar:

* Productos
* Stock
* Ingresos de mercadería
* Ventas
* Clientes
* Proveedores
* Ajustes de inventario

Todo ello respaldado por una arquitectura sólida, pruebas automatizadas y despliegue reproducible mediante contenedores.

---

## 🚀 Tecnologías utilizadas

* Java 21
* Spring Boot 4
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven
* Docker
* Testcontainers
* JUnit 5
* Mockito
* OpenAPI / Swagger

---

## 🏗️ Arquitectura

El proyecto mantiene una separación clara de responsabilidades:

* **Entity** → Modelos y mapeos JPA.
* **Repository** → Acceso a datos.
* **Service** → Lógica de negocio.
* **Controller** → Endpoints REST.
* **DTO** → Transferencia de datos.
* **Exception** → Manejo centralizado de errores.
* **Security** → Autenticación y autorización.

Esta estructura facilita el mantenimiento, testing y evolución de nuevas funcionalidades.

---

## 🧪 Testing

El desarrollo está orientado a la calidad mediante:

* Pruebas unitarias.
* Pruebas de integración.
* Testcontainers con PostgreSQL.
* MockMvc para validación de endpoints REST.
* Desarrollo guiado por pruebas (TDD).

---

## 📚 Documentación

La carpeta `docs/` contiene documentación técnica relacionada con el proyecto.

Actualmente incluye:

* Diagrama Entidad-Relación (ER) de la base de datos.

A medida que el sistema evolucione se incorporarán:

* Diagramas de arquitectura.
* Casos de uso.
* Decisiones de diseño.
* Documentación funcional de módulos.

---

## 🔐 Seguridad

* Autenticación mediante JWT.
* Autorización basada en roles.
* Protección de endpoints sensibles.
* Usuario administrador inicial configurable.

---

## 📌 Estado actual

### Implementado

* Gestión de productos.
* Autenticación y autorización JWT.
* Persistencia con PostgreSQL.
* Contenedorización con Docker.
* Documentación OpenAPI.
* Suite de pruebas automatizadas.

### En desarrollo

* Gestión de stock.
* Registro de ingresos de mercadería.
* Gestión de clientes.
* Gestión de proveedores.
* Registro de ventas.
* Ajustes de inventario.
* Reportes y métricas operativas.

---

## 📈 Roadmap

* [x] Catálogo de productos
* [x] Seguridad con JWT
* [x] Dockerización
* [x] Testing con Testcontainers
* [ ] Gestión de stock
* [ ] Ingresos de mercadería
* [ ] Clientes
* [ ] Proveedores
* [ ] Ventas
* [ ] Ajustes de stock
* [ ] Reportes

---

⚠️ Proyecto en evolución activa. La funcionalidad actual es estable, mientras se incorporan nuevos módulos de gestión comercial.
