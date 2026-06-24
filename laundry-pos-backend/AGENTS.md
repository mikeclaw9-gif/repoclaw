# AGENTS.md — Laundry POS Backend

Backend API REST para POS de lavandería. Spring Boot 3.2.0 / Java 17 / PostgreSQL.

Monorepo en `../` con `laundry-pos-frontend/` hermano.

---

## Stack

| Dependencia | Versión |
|---|---|
| Spring Boot | 3.2.0 |
| jjwt | 0.12.3 |
| Springdoc OpenAPI | 2.3.0 |
| Lombok | (última) |
| ZXing (barcodes) | 3.5.3 |

---

## Módulos

Cada módulo sigue `model/`, `dto/`, `repository/`, `service/`, `controller/`. Excepción: `reports` (no tiene entidad propia).

| Módulo | Route | Paquete |
|---|---|---|
| Auth | `/auth` | `modules.auth` |
| Users | `/users` | `modules.users` (reusa `User` de auth) |
| Clients | `/clients` | `modules.clients` |
| Sales | `/sales` | `modules.sales` |
| Inventory | `/inventory` | `modules.inventory` |
| Suppliers | `/suppliers` | `modules.suppliers` |
| Reports | `/reports` | `modules.reports` |
| Notifications | `/notifications` | `modules.notifications` |
| Expenses | `/expenses` | `modules.expenses` |
| Services | `/services` | `modules.services` |
| Cash Closure | `/cash-closure` | `modules.cash_closure` |
| Health | `/health` | `modules.auth.controller` |

---

## Convenciones que un agente adivinaría mal

- **DTOs**: Java `record`, sin Lombok. Request tiene `@NotBlank`/`@NotNull`/`@Positive`/`@Size`; Response no tiene validación.
- **Entidades**: `@Data @Builder @NoArgsConstructor @AllArgsConstructor`. `@Table(name = "snake_case_plural")`, `@Column(name = "snake_case")`.
- **Constructor injection**: `@RequiredArgsConstructor` en services/controllers/config. Sin `@Autowired`. Excepción: `JwtUtil` usa constructor explícito con `@Value`.
- **Servicios**: `@Transactional` solo en escritura que modifica múltiples entidades. `orElseThrow(() -> new ResourceNotFoundException(...))`. `IllegalArgumentException` para validación de negocio.
- **API**: Controller `@RequestMapping` solo el nombre del módulo (ej: `/clients`, no `/api/clients`). El context path `/api` ya está en `application.yml`.
- **Seguridad**: JWT Bearer. Endpoints públicos: `/auth/**`, `/health`, `/actuator/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`. Roles: `ROLE_ADMIN`, `ROLE_CASHIER`. `/users/**` y `/reports/**` solo ADMIN. CSRF deshabilitado, STATELESS.
- **Excepciones**: `ResourceNotFoundException` → 404, `BadCredentialsException` → 401, `MethodArgumentNotValidException` → 400, `Exception` → 500. Todas retornan `ErrorResponse(int status, String message, LocalDateTime timestamp)`.
- **Estilo**: Sin comentarios (solo anotaciones Swagger). `var` para locales. Stream API con `.toList()`. Mensajes en español, identificadores en inglés. snake_case en DB. Enums con valores en español (ej: `OrderStatus { PENDIENTE, EN_PROCESO, LISTO, ENTREGADO }`).

---

## Puerto y conexiones

- **Puerto**: `server.port=8085` en `application.yml` (activo siempre, sin perfil). Docker EXPOSE 8085, compose mapea `8085:8085`.
- **DB**: misma en local y docker-compose — `localhost:5432`, usuario `miguel`, password `Gallego`, DB `laundry`.
- **Swagger UI**: `http://localhost:8085/api/swagger-ui.html`
- **Redis**: docker-compose incluye Redis 7 en `localhost:6379` (no usado aún desde código).
- **`ddl-auto: update`** — Hibernate crea esquemas automáticamente.

> No commitear credenciales reales si cambian. `.gitignore` está en la raíz del monorepo (`../.gitignore`), no dentro del backend.

---

## Comandos

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw test
docker-compose up -d                  # PostgreSQL + Redis + backend
java -jar target/laundry-pos-backend-1.0.0.jar --spring.profiles.active=prod
```

`src/test/` no existe — aún no hay tests. `init-scripts/` existe pero está vacío.

---

## Variables de entorno

| Variable | Default |
|---|---|
| `DATASOURCE_URL` / `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/laundry` |
| `DATASOURCE_USERNAME` / `SPRING_DATASOURCE_USERNAME` | `miguel` |
| `DATASOURCE_PASSWORD` / `SPRING_DATASOURCE_PASSWORD` | `Gallego` |
| `JWT_SECRET` | `f5bq3MoI4cTwg0pw1yEJ8klHYupJS5zUqQLoe51UDq4=` |
| `JWT_EXPIRATION` | `86400000` |

---

<!-- CODEGRAPH_START -->
## CodeGraph

Hay `.codegraph/` en la raíz. Usar `codegraph_explore` o `codegraph node` ANTES de grep/find/read para entender código. Si no hay `.codegraph/`, ignorar.
<!-- CODEGRAPH_END -->
