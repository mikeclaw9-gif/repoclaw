# AGENTS.md — pventabase backend

## Stack
Spring Boot 3.2.5 + Java 17 + Maven multi-módulo (6 módulos), PostgreSQL (H2 en test), Flyway, JWT (jjwt 0.12.5), MapStruct 1.5.5, Lombok, Testcontainers 1.19.8, Springdoc OpenAPI 2.3.0.

## Entrypoint
`pventabase-app` → `com.pventabase.app.PventabaseApplication` (scanBasePackages = "com.pventabase"). API en `http://localhost:8090/api`.

## Comandos exactos
```bash
mvn clean install -DskipTests                            # build completo
mvn spring-boot:run -pl pventabase-app -am               # levantar servidor (rebuild automático)
mvn spring-boot:run -pl pventabase-app -am -DskipTests   # levantar sin tests
mvn test -pl pventabase-app -am                          # tests (usa H2, Flyway deshabilitado)
```

## Arquitectura
```
pventabase-common   → entidad base (BaseEntity), DTOs compartidos, excepciones (ResourceNotFound, Duplicate, InvalidState), ErrorCode enum
pventabase-usuarios → CRUD usuarios (controller/service/mapper/repository/entity)
pventabase-login    → auth JWT (login/register), depende de usuarios (accede UsuarioRepository y RolUsuario)
pventabase-inventario → CRUD productos
pventabase-clientes → CRUD clientes + borrado lógico
pventabase-app      → entrypoint, SecurityConfig, WebConfig (CORS), GlobalExceptionHandler, Flyway migrations
```

## Convenciones (verificadas en código)
- Inyección solo por constructor: `@RequiredArgsConstructor` en Services
- `@Transactional` a nivel de clase en Services; `readOnly = true` en queries
- `@Valid` en DTOs de Controller
- MapStruct: `componentModel = "spring"`. **Siempre ignorar** `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `activo` en `toEntity`/`updateEntity` (evita null del frontend). Adicionales: `UsuarioMapper.updateEntity` ignora `password`; `ClienteMapper` ignora `eliminado`
- Entidades heredan de `BaseEntity` (id, createdAt, updatedAt, createdBy, updatedBy, activo). `@PrePersist`/`@PreUpdate` setean timestamps automáticamente
- ResponseDTOs extienden `BaseResponseDTO`
- Config solo `application.properties` (no YML). Profiles: main (PostgreSQL + Flyway validate), test (H2 + create-drop + Flyway disabled)
- Flyway migrations en `pventabase-app/src/main/resources/db/migration/`. DDL auto = `validate` — no tocar schema directo
- JWT: `sub` = email, `rol` = nombre del rol (ej: `ROLE_ADMIN`). Header: `Authorization: Bearer <token>`
- `.anyRequest().permitAll()` en SecurityConfig — JWT filter autentica si hay token válido pero **no bloquea** requests sin token
- CORS: `allowedOriginPatterns("*")` + `allowCredentials(true)`

## Gotchas
- **Cliente entity mapea nombres frontend a columnas BD existentes:** `direccion`→`domicilio`, `telefono`→`celular`, `email`→`correo` via `@Column(name = ...)`. DTOs usan nombres frontend.
- `spring.jpa.open-in-view=false` — no hay lazy loading fuera de transacciones
- `AuthService.register` usa `new Usuario()` manual (no MapStruct), con `passwordEncoder.encode`
- Docker multi-stage: builder usa `maven:3.9-eclipse-temurin-17-alpine`. docker-compose apunta a PostgreSQL externo via `host.docker.internal`
- Error codes: `RESOURCE_NOT_FOUND` (404), `DUPLICATE_RESOURCE` (409), `INVALID_STATE` (400), `VALIDATION_ERROR` (400), `INTERNAL_ERROR` (500). Formato fijo: `{errorCode, message, status, timestamp, details}`
- `mvn spring-boot:run` no refleja cambios en módulos dependientes si no se hace `mvn install` primero. Usar `mvn spring-boot:run -pl pventabase-app -am`
- Al crear nuevo módulo: depende solo de `pventabase-common`. Excepción: login depende también de usuarios

## Perfil
Eres un experto en desarrollo de software y buscas siempre las mejores soluciones, dominas Java, Spring boot, Angular, React, Bases de datos postgresql, Oracle, Mysql, redis... y engeneral de todo lo que requiere la industria. Siempre respondes de forma simple y entendible, no debes extenderte demasiado en tus respuestas solo cuando te solicite explicacion debes extenderte, limita siempre tus respuestas para que sean cortas pero entendibles. Siempre RESPONDE EN ESPAÑOL y Siempre debes terminar tus respuestas con el siguiente mensaje: "----------RESPUESTA---------"

<!-- CODEGRAPH_START -->
## CodeGraph

In repositories indexed by CodeGraph (a `.codegraph/` directory exists at the repo root), reach for it BEFORE grep/find or reading files when you need to understand or locate code:

- **MCP tools** (when available): `codegraph_explore` answers most code questions in one call — the relevant symbols' verbatim source plus the call paths between them. `codegraph_node` returns one symbol's source + callers, or reads a whole file with line numbers. If the tools are listed but deferred, load them by name via tool search.
- **Shell** (always works): `codegraph explore "<symbol names or question>"` and `codegraph node <symbol-or-file>` print the same output.

If there is no `.codegraph/` directory, skip CodeGraph entirely — indexing is the user's decision.
<!-- CODEGRAPH_END -->
