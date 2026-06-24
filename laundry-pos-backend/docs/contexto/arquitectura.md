# Arquitectura

## Stack

| Capa | Tecnología | Versión |
|---|---|---|
| Backend | Spring Boot | 3.2.0 / Java 17 |
| Frontend | Angular (standalone) | 17.x |
| ORM | Spring Data JPA / Hibernate | 6.3.1 |
| DB | PostgreSQL | 16 |
| Cache | Redis | 7 (solo contenedor, sin uso desde código) |
| UI | PrimeNG + PrimeFlex | 17.x |
| Autenticación | JWT (jjwt 0.12.3) | HMAC-SHA |
| Documentación | Springdoc OpenAPI | 2.3.0 |
| Build BE | Maven (wrappereado) | — |
| Build FE | Angular CLI | 17.x |
| Contenedores | Docker + Docker Compose | — |

## Monorepo

```
laundry/
├── laundry-pos-backend/    # Spring Boot API
├── laundry-pos-frontend/   # Angular SPA
├── .gitignore              # Raíz del monorepo
└── SPEC.md                 # Especificación original
```

## Backend: mapa de paquetes

```
com.laundrypos/
├── LaundryPosApplication.java
├── config/
│   ├── SecurityConfig.java     # SecurityFilterChain, BCrypt, JWT filter
│   ├── CorsConfig.java         # CORS abierto para /api/**
│   └── SwaggerConfig.java      # OpenAPI con BearerAuth
├── modules/
│   ├── auth/        model/dto/repository/service/security/controller
│   ├── users/       dto/service/controller  (reusa User de auth)
│   ├── clients/     model/dto/repository/service/controller
│   ├── sales/       model/dto/repository/service/controller
│   ├── inventory/   model/dto/repository/service/controller
│   ├── suppliers/   model/dto/repository/service/controller
│   ├── reports/     dto/service/controller  (sin entidad)
│   ├── notifications/ model/dto/repository/service/controller
│   ├── expenses/    model/dto/repository/service/controller
│   ├── services/    model/dto/repository/service/controller
│   └── cash_closure/ model/dto/repository/service/controller
└── shared/
    ├── exception/   GlobalExceptionHandler, ResourceNotFoundException
    ├── util/        JwtUtil
    └── constant/    ApiConstants
```

## Frontend: mapa de módulos

Cada módulo tiene 2 archivos: `*.component.ts` (standalone) + `*.routes.ts`.

- `core/` — servicios (auth, api, sales), guard, interceptor, modelos
- `shared/` — layout.component (shell post-login), sidebar.component
- `modules/` — auth, dashboard, pos, clients, inventory, servicios, suppliers, reports, expenses, cash-closure

## Flujo de datos

```
Browser → ng serve (:4200) → proxy (/api → 127.0.0.1:8085) → Spring Boot (/api)
                                                                      ↓
                                                                PostgreSQL (:5432)
```

En Docker:
```
Browser → nginx (:80) → proxy (/api → api:8085) → Spring Boot
                                                     ↓
                                               PostgreSQL (db:5432)
```

## Qué NO existe

- Tests (`src/test/` no existe en backend ni frontend)
- Recuperación de contraseña (forgot-password es stub)
- Refresh token real (hay endpoint pero sin lógica de rotación)
- Mensajería real (notificaciones solo persisten en DB)
- Exportación PDF/Excel (reportes no exportan)
- Cobertura de código ni CI/CD
- Migraciones Flyway/Liquibase (Hibernate ddl-auto: update)
